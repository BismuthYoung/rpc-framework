package org.bohan.rpc.client.netty.initializer

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.codec.serialization.ClassResolver
import io.netty.handler.codec.serialization.ObjectDecoder
import io.netty.handler.codec.serialization.ObjectEncoder
import org.bohan.rpc.client.netty.handler.NettyClientHandler
import org.bohan.rpc.contract.transformer.codec.NettyDecoder
import org.bohan.rpc.contract.transformer.codec.NettyEncoder
import org.bohan.rpc.contract.transformer.serializer.impl.JsonSerializer

class NettyClientInitializer: ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel?) {
        // 在 ChannelPipeline 中，数据的处理是按照顺序进行的，从上到下依次经过每个 ChannelHandler。
        // 不同的 ChannelHandler 负责不同的任务，通常的顺序是：
        //  1. 解码器：先对接收到的数据进行解码。
        //  2. 业务处理：然后对解码后的数据进行业务处理。
        //  3. 编码器：最后将业务处理后的数据进行编码，准备发送给客户端。
        val channelPipeline = ch?.pipeline()
        //消息格式 【长度】【消息体】，解决沾包问题
        // LengthFieldBasedFrameDecoder 是 Netty 中一个专门用来解决 粘包 和 拆包 问题的解码器。
        // 它基于消息帧的长度信息来确定每个消息的边界，从而确保解码器能够正确地分隔不同的消息。
        // 参数说明：
        //  1. maxFrameLength: 该参数指定了消息帧的最大长度。
        //  2. lengthFieldOffset: 该参数指定了长度字段在消息中的偏移位置，即从消息的开始算起，消息体中存放长度信息的字段的起始位置。
        //  3. lengthFieldLength: 该参数指定了长度字段的长度，即消息中用于存储长度信息的字段所占的字节数。
        //  4. lengthAdjustment: 该参数用于调整从长度字段到消息体的偏移量。
        //  5. initialBytesToStrip: 该参数指定解码器应该跳过的初始字节数。
        channelPipeline?.addLast(
            NettyDecoder()
        )
        //计算当前待发送消息的长度，写入到前4个字节中
//        channelPipeline?.addLast(LengthFieldPrepender(4))

        //使用Java序列化方式，netty的自带的解码编码支持传输这种结构
        channelPipeline?.addLast(NettyEncoder(JsonSerializer()))

        //使用了Netty中的ObjectDecoder，它用于将字节流解码为 Java 对象。
        //在ObjectDecoder的构造函数中传入了一个ClassResolver 对象，用于解析类名并加载相应的类。
//        channelPipeline?.addLast(
//            ObjectDecoder {
//                Class.forName(it)
//            }
//        )

        channelPipeline?.addLast(NettyClientHandler())
    }
}