package org.bohan.rpc.contract.transformer.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.contract.domain.enums.MessageType
import org.bohan.rpc.contract.transformer.serializer.Serializer
import java.io.IOException

@Slf4j
class NettyDecoder: ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext?, `in`: ByteBuf?, out: MutableList<Any>?) {
        log.info("[rpc][序列化] 进入反序列化 Handler")
        if (`in` != null) {
            val messageType = `in`.readShort()
            if (messageType.toInt() != MessageType.REQUEST.code && messageType?.toInt() != MessageType.RESPONSE.code) {
                log.error("[rpc][序列化] 反序列化 handler 收到不符合类型的序列化内容")
                throw IllegalArgumentException("暂不支持此类消息的反序列化")
            }

            val serializerType = `in`.readShort()
            val serializer = Serializer.getSerializerByCode(serializerType.toInt())
                ?: throw IllegalArgumentException("没有编号为 $serializerType 的序列化器")
            val length = `in`.readInt()
            val bytes = ByteArray(length)
            `in`.readBytes(bytes)
            val deserializedMessage = serializer.deserialize(bytes, messageType.toInt()) ?: throw IOException("消息反序列化失败")
            out?.add(deserializedMessage)
        }
    }
}