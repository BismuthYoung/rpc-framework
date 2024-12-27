package org.bohan.rpc.server

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse
import org.bohan.rpc.contract.service.UserService
import org.bohan.rpc.server.provider.ServiceProvider
import org.bohan.rpc.server.server.impl.SimpleRpcServer
import org.bohan.rpc.server.server.impl.ThreadPoolRpcServer
import org.bohan.rpc.server.service.impl.UserServiceImpl
import org.bohan.rpc.server.worker.WorkThread
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

import org.mockito.Mockito.*

import java.io.*
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CountDownLatch

@Slf4j
class ServerTest {

    @Nested
    @DisplayName("组件测试")
    inner class ComponentTest {
        /**
         * 测试 ServiceProvider 是否正确工作
         */
        @Test
        @Tag("正常测试")
        @DisplayName("测试服务容器能否正常工作")
        fun providerTest() {
            val serviceProvider = ServiceProvider()
            val userService = UserServiceImpl()
            serviceProvider.provideServiceInterface(userService)

            // 验证服务是否被正确注册
            val retrievedService = serviceProvider.getService(UserService::class.java.name)
            assertNotNull(retrievedService) // 确保服务成功获取
            assertEquals(userService, retrievedService) // 确保获取的服务与注入的服务一致
        }

        /**
         * 测试 WorkThread 是否正确工作
         */
        @Test
        @Tag("正常测试")
        @DisplayName("测试工作线程能否正常工作")
        fun `test work thread handling rpc request`() {
            // 模拟服务容器
            val mockServiceProvider = mock(ServiceProvider::class.java)

            // 模拟用户服务，返回一个可以直接调用 sayHello 的对象
            val mockService = object : Any() {
                fun sayHello(name: String): String {
                    return "Hello, $name"
                }
            }

            // 当 ServiceProvider 获取到 "TestService" 时返回 mockService
            `when`(mockServiceProvider.getService("TestService"))
                .thenReturn(mockService)

            // 模拟 socket
            val mockSocket = mock(Socket::class.java)

            // 请求
            val rpcRequest = RpcRequest(
                interfaceName = "TestService",
                methodName = "sayHello",
                params = arrayOf("Test"),
                paramsType = arrayOf(String::class.java)
            )

            val byteArrayOutputStream = ByteArrayOutputStream()
            val byteArrayInputStream = ByteArrayInputStream(serialize(rpcRequest))

            `when`(mockSocket.getOutputStream())
                .thenReturn(byteArrayOutputStream)
            `when`(mockSocket.getInputStream())
                .thenReturn(byteArrayInputStream)

            // 创建工作线程并运行
            val workThread = WorkThread(mockSocket, mockServiceProvider)
            workThread.run()

            // 获取响应结果并进行断言
            val rpcResponse = deserialize<RpcResponse<*>>(byteArrayOutputStream.toByteArray())

            // Assert that the response is correct
            Assertions.assertTrue(rpcResponse.successful())
            Assertions.assertTrue(rpcResponse.data == "Hello, Test")
        }
    }

    @Nested
    @DisplayName("服务端测试")
    inner class ServerTest {
        @Test
        @Tag("正常测试")
        @DisplayName("测试基于 Socket 的单线程服务能否正常工作")
        fun `test simple rpc server handling rpc request`() {
            // 模拟服务容器
            val mockServiceProvider = mock(ServiceProvider::class.java)

            // 模拟用户服务，返回一个可以直接调用 sayHello 的对象
            val mockService = object : Any() {
                fun sayHello(name: String): String {
                    return "Hello, $name"
                }
            }

            // 当 ServiceProvider 获取到 "TestService" 时返回 mockService
            `when`(mockServiceProvider.getService("TestService"))
                .thenReturn(mockService)

            // 模拟 socket
            val mockServerSocket = mock(ServerSocket::class.java)
            val mockSocket = mock(Socket::class.java)
            `when`(mockServerSocket.accept())
                .thenAnswer {
                    mockSocket
                }
                .then {
                    mockServerSocket.close()
                }


            // 请求
            val rpcRequest = RpcRequest(
                interfaceName = "TestService",
                methodName = "sayHello",
                params = arrayOf("Test"),
                paramsType = arrayOf(String::class.java)
            )

            val byteArrayOutputStream = ByteArrayOutputStream()
            val latch = CountDownLatch(1)

            `when`(mockSocket.getInputStream()).thenAnswer {
                ByteArrayInputStream(serialize(rpcRequest))
            }
            `when`(mockSocket.getOutputStream())
                .thenReturn(byteArrayOutputStream)

            Thread {
                try {
                    SimpleRpcServer(mockServiceProvider, mockServerSocket).start()
                } catch (e: IOException) {
                    log.error("socket 出现异常", e)
                } catch (e: EOFException) {
                    log.error("流出现异常", e)
                } catch (e: Exception) {
                    log.error("出现未知异常", e)
                } finally {
                    latch.countDown()
                }
            }.start()

            latch.await()
            Thread.sleep(100)

            // 获取响应结果并进行断言
            val rpcResponse = deserialize<RpcResponse<*>>(byteArrayOutputStream.toByteArray())
            log.info("[rpc][服务端测试] 测试客户端收到的响应为：$rpcResponse")

            // Assert that the response is correct
            Assertions.assertTrue(rpcResponse.successful())
            Assertions.assertTrue(rpcResponse.data == "Hello, Test")
        }

        @Test
        @Tag("正常测试")
        @DisplayName("测试基于线程池的单线程服务能否正常工作")
        fun `test rpc server based on thread pool`() {
            @Test
            @Tag("正常测试")
            @DisplayName("测试基于 Socket 的单线程服务能否正常工作")
            fun `test simple rpc server handling rpc request`() {
                // 模拟服务容器
                val mockServiceProvider = mock(ServiceProvider::class.java)

                // 模拟用户服务，返回一个可以直接调用 sayHello 的对象
                val mockService = object : Any() {
                    fun sayHello(name: String): String {
                        return "Hello, $name"
                    }
                }

                // 当 ServiceProvider 获取到 "TestService" 时返回 mockService
                `when`(mockServiceProvider.getService("TestService"))
                    .thenReturn(mockService)

                // 模拟 socket
                val mockServerSocket = mock(ServerSocket::class.java)
                val mockSocket = mock(Socket::class.java)
                `when`(mockServerSocket.accept())
                    .thenAnswer {
                        mockSocket
                    }
                    .then {
                        mockServerSocket.close()
                    }


                // 请求
                val rpcRequest = RpcRequest(
                    interfaceName = "TestService",
                    methodName = "sayHello",
                    params = arrayOf("Test"),
                    paramsType = arrayOf(String::class.java)
                )

                val byteArrayOutputStream = ByteArrayOutputStream()
                val latch = CountDownLatch(1)

                `when`(mockSocket.getInputStream()).thenAnswer {
                    ByteArrayInputStream(serialize(rpcRequest))
                }
                `when`(mockSocket.getOutputStream())
                    .thenReturn(byteArrayOutputStream)

                Thread {
                    try {
                        ThreadPoolRpcServer(mockServiceProvider, mockServerSocket).start()
                    } catch (e: IOException) {
                        log.error("socket 出现异常", e)
                    } catch (e: EOFException) {
                        log.error("流出现异常", e)
                    } catch (e: Exception) {
                        log.error("出现未知异常", e)
                    } finally {
                        latch.countDown()
                    }
                }.start()

                latch.await()
                Thread.sleep(100)

                // 获取响应结果并进行断言
                val rpcResponse = deserialize<RpcResponse<*>>(byteArrayOutputStream.toByteArray())
                log.info("[rpc][服务端测试] 测试客户端收到的响应为：$rpcResponse")

                // Assert that the response is correct
                Assertions.assertTrue(rpcResponse.successful())
                Assertions.assertTrue(rpcResponse.data == "Hello, Test")
            }
        }
    }

    private fun serialize(obj: Any): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(obj)
        objectOutputStream.flush()
        return byteArrayOutputStream.toByteArray()
    }
    private inline fun <reified T> deserialize(byteArray: ByteArray): T {
        val byteArrayInputStream = ByteArrayInputStream(byteArray)
        return ObjectInputStream(byteArrayInputStream).use { it.readObject() as T }
    }

}