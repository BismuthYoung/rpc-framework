package org.bohan.rpc.client.proxy.retry

import com.github.rholder.retry.*
import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.client.RpcClient
import org.bohan.rpc.contract.domain.enums.ResponseStatus
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse
import java.util.concurrent.TimeUnit

@Slf4j
class RpcRequestRetryHandler(
    private val rpcClient: RpcClient
) {

    fun sendRequestWithRetry(request: RpcRequest): RpcResponse<*> {
        val retryStrategy = RetryerBuilder.newBuilder<RpcResponse<*>>()
            .retryIfException()
            .retryIfResult { response -> response.status != ResponseStatus.SUCCESS.code }
            .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
            .withStopStrategy(StopStrategies.stopAfterAttempt(5))
            .withRetryListener(object: RetryListener{
                override fun <V : Any?> onRetry(attempt: Attempt<V>?) {
                    log.debug("[rpc][客户端] 尝试第 ${attempt?.attemptNumber} 次调用")
                }
            })
            .build()

        return try {
            retryStrategy.call { rpcClient.sendRequest(request) }
        } catch (e: Exception) {
            log.error("[rpc][客户端] 重试机制出现异常")
            RpcResponse.error<Any>("出现异常")
        }
    }

}