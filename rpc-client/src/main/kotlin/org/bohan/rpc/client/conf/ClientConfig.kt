package org.bohan.rpc.client.conf

import org.bohan.component.common.hocon.annotation.Config

@Config("client")
class ClientConfig {

    private lateinit var config: com.typesafe.config.Config

    val clientType: String
        get() = config.getString("type")
}