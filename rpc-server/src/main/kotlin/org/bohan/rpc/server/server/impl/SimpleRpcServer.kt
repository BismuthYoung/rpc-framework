package org.bohan.rpc.server.server.impl

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.server.provider.ServiceProvider
import org.bohan.rpc.server.server.RpcServer
import org.bohan.rpc.server.worker.WorkThread
import java.io.IOException
import java.net.ServerSocket
import java.net.SocketException

@Slf4j
class SimpleRpcServer(
    private val serviceProvider: ServiceProvider,
    private val serverSocket: ServerSocket
): RpcServer {

    private var isRunning = true  // 标志，控制服务器是否继续运行
    private val threads = mutableListOf<Thread>()  // 用来保存工作线程，便于管理

    override fun start() {
        try {
            log.info("服务已启动")
            while (isRunning) {
                try {
                    val socket = serverSocket.accept()
                    val workerThread = Thread(WorkThread(socket, serviceProvider))
                    workerThread.start()
                    threads.add(workerThread)  // 保存启动的线程，便于后续中断
                } catch (e: IOException) {
                    if (!isRunning) {
                        log.info("服务器已停止接收连接")
                    } else {
                        log.error("服务器异常", e)
                    }
                    break
                }
            }
        } catch (e: Exception) {
            log.error("线程异常", e)
        }
    }

    override fun stop() {
        try {
            log.info("停止服务器...")
            isRunning = false
            serverSocket.close()
            for (thread in threads) {
                thread.interrupt()
            }
        } catch (e: IOException) {
            log.error("停止服务器时发生错误", e)
        }
    }
}