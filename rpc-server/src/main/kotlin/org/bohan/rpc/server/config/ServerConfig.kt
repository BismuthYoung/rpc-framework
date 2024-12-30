package org.bohan.rpc.server.config

import org.bohan.component.common.hocon.annotation.Config

@Config("server")
class ServerConfig {

    private lateinit var config: com.typesafe.config.Config

    val serverType: String
        get() = config.getString("type")

    val port: Int
        get() = config.getInt("port")

    val host: String
        get() = config.getString("host")
}