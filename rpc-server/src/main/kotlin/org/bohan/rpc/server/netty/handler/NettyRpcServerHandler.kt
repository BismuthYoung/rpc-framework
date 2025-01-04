package org.bohan.rpc.server.netty.handler

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.server.worker.ThreadUtil

class NettyRpcServerHandler(
    private val serviceProvider: org.bohan.rpc.server.provider.ServiceProvider
): SimpleChannelInboundHandler<RpcRequest>() {
    public override fun channelRead0(ctx: ChannelHandlerContext?, msg: RpcRequest?) {
        if (msg != null) {
            val response = ThreadUtil.getResponse(serviceProvider, msg)
            ctx?.writeAndFlush(response)
            ctx?.close()
        }
    }
}