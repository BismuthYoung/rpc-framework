package org.bohan.rpc.server.registry.impl

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode
import org.bohan.component.common.hocon.ConfigLoader
import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.server.config.ZkConfig
import org.bohan.rpc.server.registry.ServiceRegister
import java.net.InetSocketAddress

@Slf4j
class ZkServiceRegister: ServiceRegister {

    private val client: CuratorFramework

    private val config = ConfigLoader.loadConfig(ZkConfig::class.java)

    init {
        val policy = ExponentialBackoffRetry(1000, 3)
        client = CuratorFrameworkFactory.builder()
            .connectString(config.zookeeperAddress)
            .sessionTimeoutMs(40000)
            .retryPolicy(policy)
            .namespace(config.projectRootPath)
            .build()

        client.start()
        log.info("[rpc][服务端] zookeeper 连接成功")
    }

    override fun register(serviceName: String, serviceAddress: InetSocketAddress, canRetry: Boolean) {
        log.info("[rpc][服务端] 进入 zookeeper 节点注册方法")
        try {
            // serviceName 创建成永久节点，服务提供者下线时，不删服务名，只删地址
            if (client.checkExists().forPath("/$serviceName") == null) {
                log.debug("[rpc][服务端] zookeeper 节点不存在，开始节点创建")
                client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath("/$serviceName")
                log.debug("[rpc][服务端] zookeeper 服务节点创建完毕")
            }

            val path = "/$serviceName/${getServiceAddress(serviceAddress)}"

            client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path)
            log.debug("[rpc][服务端] zookeeper 地址节点创建完毕")
            if (canRetry) {
                val retryPath = "/${config.retryPath}/$serviceName"
                client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(retryPath)
                log.debug("[rpc][服务端] zookeeper 白名单节点创建完毕")
            }
        } catch (e: Exception) {
            log.error("zookeeper 出现问题", e)
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