package log

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.junit.jupiter.api.Test

@Slf4j
class Slf4jTest {

    @Test
    fun logTest() {
        log.info("这是一条日志")
    }

}