package org.bohan.rpc.server.server.impl

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.server.server.RpcServer
import org.bohan.rpc.server.worker.ThreadUtil
import org.bohan.rpc.server.worker.WorkThread
import java.lang.Exception
import java.net.ServerSocket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Slf4j
class ThreadPoolRpcServer(
    private val serviceProvider: org.bohan.rpc.server.provider.ServiceProvider,
    private val serverSocket: ServerSocket,
    private val threadPool: ExecutorService = Executors.newFixedThreadPool(ThreadUtil.calculateThreadCountForIo(20, 2))
):RpcServer {
    override fun start() {
        log.info("[rpc][服务端] 服务端正常启动")
        try {
            while (true) {
                val socket = serverSocket.accept()
                threadPool.execute(WorkThread(socket, serviceProvider))
            }
        } catch (e: Exception) {
            log.error("[rpc][服务端] 服务端出现异常", e)
        }
    }

    override fun stop() {
        TODO("Not yet implemented")
    }
}