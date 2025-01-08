package org.bohan.rpc.client.cache

/**
 * 服务本地缓存接口
 */
interface ServiceCache {

    /**
     * 向本地缓存添加服务方法
     */
    fun addServiceToCache(serviceName: String, serviceAddress: String): Unit

    /**
     * 修改服务地址方法
     */
    fun replaceServiceAddress(serviceName: String, oldAddress: String, newAddress: String): Unit

    /**
     * 从缓存中取服务地址方法
     */
    fun getServiceFromCache(serviceName: String): List<String>

    /**
     * 从缓存中删除服务方法
     */
    fun deleteService(serviceName: String, address: String)

}