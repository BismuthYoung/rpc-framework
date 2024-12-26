package org.bohan.rpc.server.server

interface RpcServer {

    fun start(): Unit

    fun stop(): Unit

}