package org.bohan.rpc.server

import org.bohan.component.common.hocon.ConfigLoader
import org.bohan.rpc.server.config.ServerConfig
import org.bohan.rpc.server.config.enums.ServerType
import org.bohan.rpc.server.service.impl.UserServiceImpl
import org.bohan.rpc.server.provider.ServiceProvider
import org.bohan.rpc.server.server.impl.SimpleRpcServer
import org.bohan.rpc.server.server.impl.ThreadPoolRpcServer

fun main() {
    val config = ConfigLoader.loadConfig(ServerConfig::class.java)
    val userService = UserServiceImpl()

    val serverProvider = ServiceProvider()
    serverProvider.provideServiceInterface(userService)

    val rpcServer = when(ServerType.getServerEnum(config.serverType)) {
        ServerType.SIMPLE_RPC_SERVER -> SimpleRpcServer(serverProvider)
        ServerType.THREAD_POOL_RPC_SERVER -> ThreadPoolRpcServer(serverProvider)
    }

    rpcServer.start(config.port)
}