package org.bohan.rpc.client.client.impl

import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.util.AttributeKey
import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.client.RpcClient
import org.bohan.rpc.client.netty.initializer.NettyClientInitializer
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse

@Slf4j
class NettyRpcClient(
    private val host: String,
    private val port: Int,
): RpcClient {

    companion object {
        private val bootstrap = Bootstrap()
        private val eventLoopGroup = NioEventLoopGroup()

        init {
            bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel::class.java)
                .handler(NettyClientInitializer())
        }
    }

    override fun sendRequest(request: RpcRequest?): RpcResponse<*>? {
        return try {
            val channelFuture = bootstrap.connect(host, port).sync()
            val channel = channelFuture.channel()
            channel.writeAndFlush(request)
            channel.closeFuture().sync()

            val key = AttributeKey.valueOf<RpcResponse<Any?>>("RpcResponse")
            val response = channel.attr(key).get()
            log.info("[rpc][客户端] 接收到的请求内容为：$response")
            response
        } catch (e: InterruptedException) {
            log.error("[rpc][客户端] 客户端线程被中断", e)
            null
        }
    }


}