package org.bohan.rpc.server.provider

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import java.lang.NullPointerException

/**
 * 本地服务存放器
 */
@Slf4j
class ServiceProvider {

    // 集合中存放服务的实例
    private val interfaceProvider = mutableMapOf<String, Any>()

    fun provideServiceInterface(service: Any) {
        val serviceName = service::class.java.name
        val interfaceName = service::class.java.interfaces

        interfaceName.forEach { interfaceClazz ->
            interfaceProvider[interfaceClazz.name] = service
            log.debug("当前服务容器注册键为 {}，值为 {} 的项", interfaceClazz.name, service)
        }
    }

    fun getService(interfaceName: String): Any {
        return interfaceProvider[interfaceName] ?: NullPointerException("当前尝试获取的服务名为 $interfaceName，该服务不存在。")
    }

}