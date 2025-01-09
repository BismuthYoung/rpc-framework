package org.bohan.rpc.client.proxy.breaker

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.proxy.breaker.enums.CircuitBreakerState
import java.util.concurrent.atomic.AtomicInteger

@Slf4j
class SimpleCircuitBreaker(
    private val failureThreshold: Int,
    private val halfOpenSuccessRate: Double,
    private val retryTimePeriod: Long
) {

    @Volatile
    private var breakerState = CircuitBreakerState.CLOSED

    private val failureCount = AtomicInteger(0)
    private val successCount = AtomicInteger(0)
    private val requestCount = AtomicInteger(0)

    @Volatile
    private var lastFailureTime = 0L

    @Synchronized
    fun allowRequest(): Boolean {
        val currentTime = System.currentTimeMillis()
        log.debug("[rpc][客户端] 当前熔断器状态为：${breakerState.name}")
        return when (breakerState) {
            CircuitBreakerState.CLOSED -> {
                true
            }
            CircuitBreakerState.HALF_OPEN -> {
                requestCount.incrementAndGet()
                true
            }
            CircuitBreakerState.OPEN -> {
                if (currentTime - lastFailureTime > retryTimePeriod) {
                    breakerState = CircuitBreakerState.HALF_OPEN
                    resetCounts()
                    true
                } else {
                    log.error("[rpc][客户端] 客户端已熔断")
                    false
                }
            }
        }
    }

    @Synchronized
    fun recordSuccess() {
        if (breakerState == CircuitBreakerState.HALF_OPEN) {
            // 假设熔断器处于半开状态，判断当前成功次数有没有大于给定成功率
            successCount.incrementAndGet()
            if (successCount.get() >= halfOpenSuccessRate * requestCount.get()) {
                // 假设成功次数大于给定成功率，熔断器关闭
                breakerState = CircuitBreakerState.CLOSED
                resetCounts()
            }
        } else {
            // 假设熔断器处于关闭状态，不需要记录当前熔断器请求次数
            resetCounts()
        }
    }

    @Synchronized
    fun recordFailure() {
        failureCount.incrementAndGet()
        log.error("[rpc][客户端] 熔断器记录失败次数：${failureCount.get()}")
        lastFailureTime = System.currentTimeMillis()
        if (breakerState == CircuitBreakerState.HALF_OPEN) {
            // 假设熔断器处于半开状态，发送请求失败后，进入开启状态，等到下一个重试周期的到来
            breakerState = CircuitBreakerState.OPEN
            lastFailureTime = System.currentTimeMillis()
        } else if (failureCount.get() >= failureThreshold) {
            // 假设熔断器处于关闭状态，当前失败超过阈值，则熔断器开启
            breakerState = CircuitBreakerState.OPEN
        }
        // 熔断器处于开启状态时请求不会失败
    }

    fun getState() = breakerState

    private fun resetCounts() {
        failureCount.set(0)
        successCount.set(0)
        requestCount.set(0)
    }

}