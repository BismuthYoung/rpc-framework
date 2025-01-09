package org.bohan.rpc.client.proxy.breaker

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log

@Slf4j
class SimpleCircuitBreakerProvider {

    private val breakerMap = mutableMapOf<String, SimpleCircuitBreaker>()

    @Synchronized
    fun getCircuitBreaker(serviceName: String): SimpleCircuitBreaker {
        return breakerMap[serviceName] ?: run {
            log.debug("[rpc][服务端] 为 $serviceName 服务添加新熔断器")
            val breaker = SimpleCircuitBreaker(1, 0.5, 10000L)
            breakerMap[serviceName] = breaker
            breaker
        }
    }

}