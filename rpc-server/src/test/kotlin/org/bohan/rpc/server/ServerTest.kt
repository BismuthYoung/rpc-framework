package org.bohan.rpc.server

import junit.framework.TestCase.*
import org.bohan.component.common.log.Slf4j
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse
import org.bohan.rpc.contract.service.UserService
import org.bohan.rpc.server.provider.ServiceProvider
import org.bohan.rpc.server.server.impl.SimpleRpcServer
import org.bohan.rpc.server.service.impl.UserServiceImpl
import org.bohan.rpc.server.worker.WorkThread
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket

@Slf4j
class ServerTest {

    /**
     * 测试 ServiceProvider 是否正确工作
     */
    @Test
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
        assert(rpcResponse.successful())
        assert(rpcResponse.data == "Hello, Test")
    }

    /**
     * 测试基于 Socket 的单线程服务能否正常工作
     */
    @Test
    fun `test simple rpc server handling rpc request`() {

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