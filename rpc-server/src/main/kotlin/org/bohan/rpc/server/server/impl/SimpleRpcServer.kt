package org.bohan.rpc.server.server.impl

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.server.provider.ServiceProvider
import org.bohan.rpc.server.server.RpcServer
import org.bohan.rpc.server.worker.WorkThread
import java.io.IOException
import java.net.ServerSocket

@Slf4j
class SimpleRpcServer(
    private val serviceProvider: ServiceProvider,
    private val serverSocket: ServerSocket
): RpcServer {
    override fun start() {
        try {
            log.info("服务已启动")
            while (true) {
                val socket = serverSocket.accept()
                Thread(WorkThread(socket, serviceProvider)).start()
            }
        } catch (e: IOException) {
            log.error("线程异常", e)
        }
    }

    override fun stop() {
        TODO("Not yet implemented")
    }
}