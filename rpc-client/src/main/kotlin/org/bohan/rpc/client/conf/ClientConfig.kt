package org.bohan.rpc.client.conf

import org.bohan.component.common.hocon.annotation.Config

@Config("client")
class ClientConfig {

    private lateinit var config: com.typesafe.config.Config

    val clientType: String
        get() = config.getString("type") ?: throw NoSuchElementException("该配置不存在")
    val host: String
        get() = config.getString("host") ?: throw NoSuchElementException("该配置不存在")
    val port: Int
        get() = config.getInt("port") ?: throw NoSuchElementException("该配置不存在")
    val balanceStrategy: String
        get() = config.getString("balanceStrategy") ?: throw NoSuchElementException("该项配置不存在")
}