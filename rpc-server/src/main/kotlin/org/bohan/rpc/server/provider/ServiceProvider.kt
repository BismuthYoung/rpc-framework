package org.bohan.rpc.server.provider

/**
 * 本地服务存放器
 */
class ServiceProvider {

    // 集合中存放服务的实例
    private val interfaceProvider = mutableMapOf<String, Any>()

    fun provideServiceInterface(service: Any) {
        val serviceName = service::class.java.name
        val interfaceName = service::class.java.interfaces

        interfaceName.forEach { interfaceClazz ->
            interfaceProvider[interfaceClazz.name] = service
        }
    }

    fun getService(interfaceName: String): Any? {
        return interfaceProvider[interfaceName]
    }

}