package org.bohan.rpc.server.server.impl

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoop
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.server.netty.initializer.NettyServerInitializer
import org.bohan.rpc.server.provider.ServiceProvider
import org.bohan.rpc.server.server.RpcServer

class NettyRpcServer(
    private val serviceProvider: ServiceProvider,
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
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(NettyServerInitializer(serviceProvider))

            val channelFuture = serverBootstrap.bind(port).sync()
            channelFuture.channel().closeFuture().sync()
        } catch (e: InterruptedException) {
            log.error("[rpc][服务端] 线程被中断", e)
        } finally {
            bossGroup.shutdownGracefully()
            workGroup.shutdownGracefully()
        }
    }

    override fun stop() {
        TODO("Not yet implemented")
    }
}