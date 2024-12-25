package hocon.config

import org.bohan.component.common.hocon.annotation.Config

@Config("client")
class ClientConfig {

    lateinit var config: com.typesafe.config.Config

    val clientType: String
        get() = config.getString("type")

}