package org.bohan.rpc.client.cache.impl

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.cache.ServiceCache

@Slf4j
class SimpleServiceCache: ServiceCache {

    companion object {
        private val cache = mutableMapOf<String, MutableList<String>>()
    }

    override fun addServiceToCache(serviceName: String, serviceAddress: String) {
        cache.getOrPut(serviceName) { mutableListOf() }.add(serviceAddress)
        log.info("[rpc][客户端] 用户缓存添加服务 $serviceName, 地址为 $serviceAddress")
    }

    override fun replaceServiceAddress(serviceName: String, oldAddress: String, newAddress: String) {
        cache[serviceName]?.apply {
            remove(oldAddress)
            add(newAddress)
        } ?: log.error("[rpc][客户端] 修改失败，$serviceName 不存在")
    }

    override fun getServiceFromCache(serviceName: String): List<String> {
        return cache[serviceName] ?: emptyList()
    }

    override fun deleteService(serviceName: String, address: String) {
        cache[serviceName]?.remove(address)
        log.info("[rpc][服务端] 删除 $serviceName 下地址为 $address 的服务")
    }
}