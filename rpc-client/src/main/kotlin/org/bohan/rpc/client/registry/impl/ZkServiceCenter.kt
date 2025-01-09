package org.bohan.rpc.client.registry.impl

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.bohan.component.common.hocon.ConfigLoader
import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.cache.impl.SimpleServiceCache
import org.bohan.rpc.client.conf.ClientConfig
import org.bohan.rpc.client.conf.enums.BalanceStrategy
import org.bohan.rpc.client.registry.ServiceCenter
import org.bohan.rpc.client.registry.ZkServiceMonitor
import java.net.InetSocketAddress
import java.util.NoSuchElementException

@Slf4j
class ZkServiceCenter: ServiceCenter {

    private val client: CuratorFramework

    private val cache: SimpleServiceCache

    private val config = ConfigLoader.loadConfig(ClientConfig::class.java)

    companion object {
        private const val ROOT_PATH = "rpc-frame"
        private const val ZK_ROOT_PATH = "/"
    }

    init {
        val policy = ExponentialBackoffRetry(1000, 3)
        client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .sessionTimeoutMs(40000)
            .retryPolicy(policy)
            .namespace(ROOT_PATH)
            .build()

        client.start()
        log.info("[rpc][服务端] zookeeper 连接成功")

        // 后续可能需要修改
        cache = SimpleServiceCache()
        val monitor = ZkServiceMonitor(client, cache)
        monitor.watchToUpdate(ZK_ROOT_PATH)
    }

    override fun serviceDiscovery(serviceName: String): InetSocketAddress? {
        return try {
            // 先从本地缓存中找
            var addressStringList: List<String>
            addressStringList = cache.getServiceFromCache(serviceName)
            log.info("[rpc][客户端] 从缓存中找到的服务信息为 $addressStringList")
            if (addressStringList.isEmpty()) {
                addressStringList = client.children.forPath("/$serviceName")
                log.info("[rpc][客户端] 缓存为空，从 zk 中找到的服务信息为 $addressStringList")
            }
            val addressString = BalanceStrategy.getStrategyByName(config.balanceStrategy).selectServer(addressStringList)
                ?: throw NoSuchElementException("当前服务不存在线上节点")

            parseAddress(addressString)
        } catch (e: Exception) {
            log.error("[rpc][客户端] zookeeper 连接出现异常", e)
            null
        }
    }

    private fun getServiceAddress(serverAddress: InetSocketAddress): String {
        return "${serverAddress.hostName}:${serverAddress.port}"
    }

    // 字符串解析为地址
    private fun parseAddress(address: String): InetSocketAddress {
        val result = address.split(":")
        return InetSocketAddress(result[0], result[1].toInt())
    }
}