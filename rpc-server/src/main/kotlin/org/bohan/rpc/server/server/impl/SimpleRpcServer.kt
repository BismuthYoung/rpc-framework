package org.bohan.rpc.server.server.impl

import org.bohan.rpc.server.provider.ServiceProvider
import org.bohan.rpc.server.server.RpcServer
import org.bohan.rpc.server.worker.WorkThread
import java.io.IOException
import java.net.ServerSocket

class SimpleRpcServer(
    private val serviceProvider: ServiceProvider
): RpcServer {
    override fun start(port: Int) {
        try {
            val serverSocket = ServerSocket(port)
            println("服务已启动")
            while (true) {
                val socket = serverSocket.accept()
                Thread(WorkThread(socket, serviceProvider)).start()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun stop() {
        TODO("Not yet implemented")
    }
}