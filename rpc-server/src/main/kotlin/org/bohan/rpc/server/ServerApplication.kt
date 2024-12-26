package org.bohan.rpc.server

import org.bohan.component.common.hocon.ConfigLoader
import org.bohan.rpc.server.config.ServerConfig
import org.bohan.rpc.server.config.enums.ServerType
import org.bohan.rpc.server.service.impl.UserServiceImpl
import org.bohan.rpc.server.provider.ServiceProvider
import org.bohan.rpc.server.server.impl.SimpleRpcServer
import org.bohan.rpc.server.server.impl.ThreadPoolRpcServer
import java.net.ServerSocket

fun main() {
    val config = ConfigLoader.loadConfig(ServerConfig::class.java)
    val userService = UserServiceImpl()

    val serverProvider = ServiceProvider()
    serverProvider.provideServiceInterface(userService)

    val serverSocket = ServerSocket(config.port)

    val rpcServer = when(ServerType.getServerEnum(config.serverType)) {
        ServerType.SIMPLE_RPC_SERVER -> SimpleRpcServer(serverProvider, serverSocket)
        ServerType.THREAD_POOL_RPC_SERVER -> ThreadPoolRpcServer(serverProvider, serverSocket)
    }

    rpcServer.start()
}