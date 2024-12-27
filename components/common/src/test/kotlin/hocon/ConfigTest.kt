package hocon

import com.typesafe.config.ConfigFactory
import hocon.config.ClientConfig
import org.bohan.component.common.hocon.ConfigLoader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConfigTest {

    @Test
    fun `read correct config`() {
        val configFromHocon = ConfigFactory.load()
        val clientConfig = configFromHocon.getString("rpc-framework.client.type")

        assertEquals(clientConfig, "simple")
    }

    @Test
    fun `read config by annotation`() {
        val clientConfig = ConfigLoader.loadConfig(ClientConfig::class.java)
        assertEquals(clientConfig.clientType, "simple")
    }

}