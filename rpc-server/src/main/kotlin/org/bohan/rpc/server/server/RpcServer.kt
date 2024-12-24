package org.bohan.rpc.server.server

interface RpcServer {

    fun start(port: Int): Unit

    fun stop(): Unit

}