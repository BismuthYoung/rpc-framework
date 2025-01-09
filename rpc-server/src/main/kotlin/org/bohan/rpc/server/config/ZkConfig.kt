package org.bohan.rpc.server.config

import org.bohan.component.common.hocon.annotation.Config

@Config("zookeeper")
class ZkConfig {

    private lateinit var config: com.typesafe.config.Config

    val zookeeperAddress: String
        get() = config.getString("zk-address")

    val zkRootPath: String
        get() = config.getString("zk-root-path")

    val projectRootPath: String
        get() = config.getString("project-root-path")

    val retryPath: String
        get() = config.getString("retry-path")
}
