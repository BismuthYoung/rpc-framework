package org.bohan.rpc.client

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.client.impl.IOClient
import org.bohan.rpc.client.client.impl.SimpleSocketRpcClient
import org.bohan.rpc.client.proxy.ClientProxy
import org.bohan.rpc.contract.domain.entity.User
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse
import org.bohan.rpc.contract.service.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*

import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.Exception
import java.net.ServerSocket


@Slf4j
class ClientTest {

    @Nested
    @DisplayName("基于 Socket 的 Rpc 客户端测试")
    inner class SendRequestTest {
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
        @Tag("正常测试")
        @DisplayName("测试客户端能否正确收到响应")
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

                Thread.sleep(500)

                val response = IOClient.sendRequest("127.0.0.1", port, req)
                assertNotNull(response)
                assertEquals("Success", response?.data)
            } catch (e: Exception) {
                log.error("出现异常", e)
            }
        }


        @Test
        @Tag("异常测试")
        @DisplayName("测试客户端异常情况下能否返回空值")
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
    }

    @Nested
    @DisplayName("动态代理功能测试")
    inner class ProxyTest {

        @Test
        @Tag("正常测试")
        @DisplayName("测试客户端代理能否获取正确的对象")
        fun `test getProxy method returns proxy`() {
            val mockClient = mock(SimpleSocketRpcClient::class.java)
            val proxy = ClientProxy(mockClient)
            assertTrue(proxy.getProxy(UserService::class.java) is UserService)
        }

        @Test
        @Tag("正常测试")
        @DisplayName("动态代理功能测试")
        fun `test proxy result`() {
            val mockClient = mock(SimpleSocketRpcClient::class.java)
            val mockUser = User(3366, "Bohan", true)
            `when`(mockClient.sendRequest(RpcRequest(
                interfaceName = "org.bohan.rpc.contract.service.UserService",
                methodName = "getUserById",
                params = arrayOf(42),
                paramsType = arrayOf(Int::class.java)
            ))).thenReturn(RpcResponse.success(mockUser))

            val proxy = ClientProxy(mockClient)
            val userService = proxy.getProxy(UserService::class.java)
            val returnUser = userService.getUserById(42)
            assertNotNull(returnUser)
            assertEquals(returnUser?.id, mockUser.id)
        }

        @Test
        @Tag("正常测试")
        @DisplayName("请求构造测试")
        fun `request build test`() {
            val mockClient = mock(SimpleSocketRpcClient::class.java)
            val proxy = ClientProxy(mockClient)
            val userService = proxy.getProxy(UserService::class.java)
            val mockUser = User(3366, "Bohan", true)

            // 模拟 RpcResponse
            val mockResponse = RpcResponse.success(mockUser)
            `when`(mockClient.sendRequest(any(RpcRequest::class.java))).thenReturn(mockResponse)

            // 调用代理方法
            val receivedUser = userService.getUserById(3366)

            // 捕获传递给 sendRequest 方法的 RpcRequest
            val captor: ArgumentCaptor<RpcRequest> = ArgumentCaptor.forClass(RpcRequest::class.java)
            verify(mockClient).sendRequest(captor.capture())

            // 获取捕获到的 RpcRequest
            val request = captor.value

            // 验证 RpcRequest 是否正确构造
            assertNotNull(request)
            assertEquals("getUserById", request.methodName)
            assertEquals("org.bohan.rpc.contract.service.UserService", request.interfaceName)
            assertEquals(1, request.params.size)
            assertEquals(3366, request.params[0])
            assertEquals(Int::class.java, request.paramsType[0])
        }

    }

}