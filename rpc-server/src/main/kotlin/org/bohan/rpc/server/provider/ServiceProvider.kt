package org.bohan.rpc.server.provider

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.server.registry.ServiceRegister
import java.lang.NullPointerException
import java.net.InetSocketAddress

/**
 * 本地服务存放器
 */
@Slf4j
class ServiceProvider(
    private val host: String,
    private val port: Int,
    private val serviceRegister: ServiceRegister
) {

    // 集合中存放服务的实例
    private val interfaceProvider = mutableMapOf<String, Any>()

    fun provideServiceInterface(service: Any) {
        val interfaceName = service::class.java.interfaces

        interfaceName.forEach { interfaceClazz ->
            // 本机映射表
            interfaceProvider[interfaceClazz.name] = service
            // 在注册中心注册服务
            serviceRegister.register(interfaceClazz.name, InetSocketAddress(host, port))
            log.debug("当前服务容器注册键为 {}，值为 {} 的项", interfaceClazz.name, service)
        }
    }

    fun getService(interfaceName: String?): Any {
        if (interfaceName == null) {
            throw IllegalArgumentException("希望获取的接口名称不能为空")
        }
        return interfaceProvider[interfaceName] ?: NullPointerException("当前尝试获取的服务名为 $interfaceName，该服务不存在。")
    }

}