package org.bohan.rpc.client

import org.bohan.rpc.client.proxy.ClientProxy
import org.bohan.rpc.contract.service.UserService
import org.junit.Test

class ClientTest {

    @Test
    fun clientTest() {
        val clientProxy = ClientProxy("127.0.0.1", 9999)
        val proxyService = clientProxy.getProxy(UserService::class.java)

        val user = proxyService.getUserById(42)
        println("从服务端获得的用户对象为：$user")
    }

}