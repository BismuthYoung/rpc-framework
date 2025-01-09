package org.bohan.rpc.server

import org.bohan.component.common.hocon.ConfigLoader
import org.bohan.rpc.server.config.ServerConfig
import org.bohan.rpc.server.config.enums.ServerType
import org.bohan.rpc.server.provider.impl.SimpleServiceProvider
import org.bohan.rpc.server.service.impl.UserServiceImpl
import org.bohan.rpc.server.provider.impl.ZkServiceProvider
import org.bohan.rpc.server.registry.impl.ZkServiceRegister
import org.bohan.rpc.server.server.impl.NettyRpcServer
import org.bohan.rpc.server.server.impl.SimpleRpcServer
import org.bohan.rpc.server.server.impl.ThreadPoolRpcServer
import org.bohan.rpc.server.worker.rateLimit.provider.RateLimiterProvider
import java.net.ServerSocket

fun main() {
    val config = ConfigLoader.loadConfig(ServerConfig::class.java)
    val userService = UserServiceImpl()
    val serviceRegister = ZkServiceRegister()
    val rateLimiterProvider = RateLimiterProvider()

    val zkServiceProvider = ZkServiceProvider(config.host, config.port, serviceRegister, rateLimiterProvider)
    val simpleServiceProvider = SimpleServiceProvider(rateLimiterProvider)
    zkServiceProvider.provideServiceInterface(userService)
    simpleServiceProvider.provideServiceInterface(userService)

    val rpcServer = when(ServerType.getServerEnum(config.serverType)) {
        ServerType.SIMPLE_RPC_SERVER -> {
            val serverSocket = ServerSocket(config.port)
            SimpleRpcServer(simpleServiceProvider, serverSocket)
        }
        ServerType.THREAD_POOL_RPC_SERVER -> {
            val serverSocket = ServerSocket(config.port)
            ThreadPoolRpcServer(simpleServiceProvider, serverSocket)
        }
        ServerType.NETTY_RPC_SERVER -> {
            NettyRpcServer(zkServiceProvider, config.port)
        }
    }

    rpcServer.start()
}