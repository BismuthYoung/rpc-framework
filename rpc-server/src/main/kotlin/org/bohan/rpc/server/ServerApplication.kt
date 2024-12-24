package org.bohan.rpc.server

import org.bohan.rpc.server.service.impl.UserServiceImpl
import org.bohan.rpc.server.provider.ServiceProvider
import org.bohan.rpc.server.server.impl.SimpleRpcServer

fun main() {
    val userService = UserServiceImpl()

    val serverProvider = ServiceProvider()
    serverProvider.provideServiceInterface(userService)

    val rpcServer = SimpleRpcServer(serverProvider)

    rpcServer.start(9999)
}