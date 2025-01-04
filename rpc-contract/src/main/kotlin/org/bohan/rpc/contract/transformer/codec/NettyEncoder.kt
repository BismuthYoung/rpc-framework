package org.bohan.rpc.contract.transformer.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.contract.domain.enums.MessageType
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse
import org.bohan.rpc.contract.transformer.serializer.Serializer

@Slf4j
class NettyEncoder(
    private val serializer: Serializer
): MessageToByteEncoder<Any>() {
    override fun encode(ctx: ChannelHandlerContext?, msg: Any?, out: ByteBuf?) {
        log.info("[rpc][序列化] 待序列化的消息内容为 {}", msg)
        if (msg == null) {
            throw NullPointerException("序列化传入消息为空")
        }
        when (msg) {
            is RpcRequest -> out?.writeShort(MessageType.REQUEST.code)
            is RpcResponse<*> -> out?.writeShort(MessageType.RESPONSE.code)
        }
        out?.writeShort(serializer.getType())
        val bytes = serializer.serialize(msg)
        out?.writeInt(bytes?.size ?: 0)
        out?.writeBytes(bytes)
    }
}