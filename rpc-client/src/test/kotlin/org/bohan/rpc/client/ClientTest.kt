package org.bohan.rpc.client

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.client.impl.IOClient
import org.bohan.rpc.client.proxy.ClientProxy
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse
import org.bohan.rpc.contract.service.UserService
import org.junit.Test
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.Exception
import java.net.ServerSocket
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Slf4j
class ClientTest {

    private fun startMockServer(port: Int) {
        Thread {
            ServerSocket(port).use { serverSocket ->
                log.info("服务端正常启动，监听端口: $port")
                val clientSocket = serverSocket.accept()
                log.debug("客户端已连接：${clientSocket.inetAddress.hostAddress}")

                try {
                    val oos = ObjectOutputStream(clientSocket.getOutputStream())
                    val ois = ObjectInputStream(clientSocket.getInputStream())

                    val rpcRequest = ois.readObject() as RpcRequest
                    log.info("服务端已读取客户端请求，请求内容为 $rpcRequest")

                    val rpcResponse = RpcResponse.success("Success")

                    oos.writeObject(rpcResponse)
                    oos.flush()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    @Test
    fun `test sendRequest with valid request`() {
        try {
            val port = 3876
            startMockServer(port) // 启动模拟服务端

            // 模拟客户端请求
            val req = RpcRequest(
                methodName = "testMethod",
                interfaceName = "testInterface",
                params = arrayOf("param1"),
                paramsType = arrayOf(String::class.java)
            )
            log.info("请求内容为: $req")

            // 等待服务端启动完成 (可选，避免 race condition)
            Thread.sleep(500)

            val response = IOClient.sendRequest("127.0.0.1", port, req)
            assertNotNull(response)
            assertEquals("Success", response.data)
        } catch (e: Exception) {
            log.error("出现异常", e)
        }
    }


    @Test
    fun `test sendRequest with invalid response`() {
        val req = RpcRequest(
            methodName = "testMethod",
            interfaceName = "testInterface",
            params = arrayOf("param1"),
            paramsType = arrayOf(String::class.java)
        )

        val response = IOClient.sendRequest("127.0.0.1", 9998, req)
        assertNull(response)
    }

    @Test
    fun clientTest() {
        val clientProxy = ClientProxy("127.0.0.1", 9999)
        val proxyService = clientProxy.getProxy(UserService::class.java)

        val user = proxyService.getUserById(42)
        println("从服务端获得的用户对象为：$user")
    }

}