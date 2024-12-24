package org.bohan.rpc.server

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.contract.domain.entity.User
import org.bohan.rpc.contract.service.UserService
import org.bohan.rpc.server.provider.ServiceProvider
import org.bohan.rpc.server.service.impl.UserServiceImpl
import org.junit.Test
import org.mockito.Mockito

@Slf4j
class ServerTest {

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

}