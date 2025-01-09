package org.bohan.rpc.server.worker.rateLimit.provider

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.server.worker.rateLimit.RateLimiter
import org.bohan.rpc.server.worker.rateLimit.impl.TokenBucketRateLimiter

@Slf4j
class RateLimiterProvider {

    private val rateLimitMap = mutableMapOf<String, RateLimiter>()

    fun getRateLimiter(interfaceName: String): RateLimiter {
        return if (! rateLimitMap.containsKey(interfaceName)) {
            log.debug("[rpc][服务端] 将名称为 $interfaceName 的限流器放入 provider 中")
            val rateLimiter = TokenBucketRateLimiter(100, 10)
            rateLimitMap[interfaceName] = rateLimiter
            rateLimiter
        } else {
            log.debug("[rpc][服务端] 找到名为 {} 的限流器，值为 {}[interfaceName", interfaceName, rateLimitMap)
            rateLimitMap[interfaceName] !!
        }
    }

}