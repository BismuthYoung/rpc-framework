package org.bohan.rpc.server.server.impl

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.server.netty.initializer.NettyServerInitializer
import org.bohan.rpc.server.server.RpcServer

@Slf4j
class NettyRpcServer(
    private val serviceProvider: org.bohan.rpc.server.provider.ServiceProvider,
    private val port: Int
): RpcServer {
    override fun start() {
        // netty 服务线程组boss负责建立连接， work负责具体的请求
        val bossGroup = NioEventLoopGroup()
        val workGroup = NioEventLoopGroup()
        log.info("[rpc][服务端] Netty 服务端正常启动")

        try {
            val serverBootstrap = ServerBootstrap()
            serverBootstrap
                .option(ChannelOption.SO_REUSEADDR, true)
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(NettyServerInitializer(serviceProvider))

            log.debug("[rpc][服务端] 当前待绑定的端口为：$port")
            val channelFuture = serverBootstrap.bind(port).sync()
            channelFuture.channel().closeFuture().sync()
        } catch (e: InterruptedException) {
            log.error("[rpc][服务端] 线程被中断", e)
        } catch (e: Exception) {
            log.error("[rpc][服务端] 出现未知异常", e)
        } finally {
            bossGroup.shutdownGracefully()
            workGroup.shutdownGracefully()
        }
    }

    override fun stop() {
    }
}