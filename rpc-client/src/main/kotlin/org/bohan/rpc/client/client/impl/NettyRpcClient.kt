package org.bohan.rpc.client.client.impl

import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.util.AttributeKey
import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.client.RpcClient
import org.bohan.rpc.client.netty.initializer.NettyClientInitializer
import org.bohan.rpc.client.registry.ServiceCenter
import org.bohan.rpc.client.registry.impl.ZkServiceCenter
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse

@Slf4j
class NettyRpcClient(
    private val serviceCenter: ZkServiceCenter
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
            val socketAddress = serviceCenter.serviceDiscovery(request?.interfaceName
                ?: throw NoSuchElementException("Rpc 请求中服务名称为空"))
            val host = socketAddress?.hostName ?: throw NoSuchElementException("返回地址中域名为空")
            val channelFuture = bootstrap.connect(host, socketAddress.port).sync()
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
        } catch (e: NoSuchElementException) {
            log.error("[rpc][客户端] zk 节点信息异常", e)
            null
        } catch (e: Exception) {
            log.error("[rpc][客户端] 出现未知错误", e)
            null
        }
    }

    override fun getServiceCenter(): ServiceCenter {
        return serviceCenter
    }

}