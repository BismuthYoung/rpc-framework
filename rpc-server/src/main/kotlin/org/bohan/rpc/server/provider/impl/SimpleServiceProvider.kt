package org.bohan.rpc.server.provider.impl

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.server.provider.ServiceProvider

@Slf4j
class SimpleServiceProvider: ServiceProvider {
    // 集合中存放服务的实例
    private val interfaceProvider = mutableMapOf<String, Any>()

    override fun provideServiceInterface(service: Any) {
        val interfaceName = service::class.java.interfaces

        interfaceName.forEach { interfaceClazz ->
            interfaceProvider[interfaceClazz.name] = service
            log.debug("当前服务容器注册键为 {}，值为 {} 的项", interfaceClazz.name, service)
        }
    }

    override fun getService(interfaceName: String?): Any {
        if (interfaceName == null) {
            throw IllegalArgumentException("希望获取的接口名称不能为空")
        }
        return interfaceProvider[interfaceName] ?: NullPointerException("当前尝试获取的服务名为 $interfaceName，该服务不存在。")
    }
}