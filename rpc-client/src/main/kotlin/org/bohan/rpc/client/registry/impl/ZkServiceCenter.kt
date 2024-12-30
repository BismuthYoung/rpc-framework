package org.bohan.rpc.client.registry.impl

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.registry.ServiceCenter
import java.net.InetSocketAddress
import java.util.NoSuchElementException

@Slf4j
class ZkServiceCenter: ServiceCenter {

    private val client: CuratorFramework

    companion object {
        private const val ROOT_PATH = "rpc-frame"
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
    }

    override fun serviceDiscovery(serviceName: String): InetSocketAddress? {
        return try {
            val addressStringList = client.children.forPath("/$serviceName")
            val addressString = addressStringList.first() ?: throw NoSuchElementException("当前服务不存在线上节点")

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