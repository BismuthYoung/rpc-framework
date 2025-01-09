package org.bohan.rpc.server.worker.rateLimit.impl

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.server.worker.rateLimit.RateLimiter

@Slf4j
class TokenBucketRateLimiter(
    private val rate: Int,
    private val capacity: Int
): RateLimiter {

    @Volatile
    private var currentTokenCount = capacity

    @Volatile
    private var timeStamp = System.currentTimeMillis()

    override fun getToken(): Boolean {
        log.debug("[rpc][服务端] 进入令牌桶")
        // 如果桶内有剩余令牌
        if (currentTokenCount > 0) {
            currentTokenCount --
            return true
        }

        // 如果桶内没有剩余令牌
        val currentTime = System.currentTimeMillis()

        // 如果距离上一次的请求的时间大于 RATE 的时间
        if (currentTime - timeStamp > rate) {
            // 生成令牌
            val newTokenCount = (currentTime - timeStamp) / rate
            if (newTokenCount > 2) {
                currentTokenCount += (newTokenCount - 1).toInt()
            }
            // 保持桶内令牌数量恒定
            if (currentTokenCount > capacity) {
                currentTokenCount = capacity
            }
            // 刷新时间戳
            timeStamp = currentTime

            return true
        }

        return false
    }

}