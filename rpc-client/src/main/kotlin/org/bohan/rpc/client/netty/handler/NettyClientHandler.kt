package org.bohan.rpc.client.netty.handler

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.AttributeKey
import org.bohan.rpc.contract.domain.resp.RpcResponse

class NettyClientHandler: SimpleChannelInboundHandler<RpcResponse<Any?>>() {
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: RpcResponse<Any?>?) {
        val key = AttributeKey.valueOf<RpcResponse<Any?>>("RpcResponse")
        ctx?.channel()?.attr(key)?.set(msg)
        ctx?.channel()?.close()
    }
}