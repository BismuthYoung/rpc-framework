package org.bohan.rpc.contract.transformer.serializer.impl

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.contract.transformer.serializer.Serializer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

@Slf4j
class ObjectSerializer: Serializer {
    override fun serialize(obj: Any): ByteArray? {
        var bytes: ByteArray? = null
        try {
            ByteArrayOutputStream().use { bos ->
                ObjectOutputStream(bos).use { oos ->
                    oos.writeObject(obj)
                    oos.flush()
                }
                bytes = bos.toByteArray()
            }
        } catch (e: IOException) {
            log.error("[rpc][序列化] 序列化出现问题", e)
        }
        return bytes
    }

    override fun deserialize(bytes: ByteArray, messageType: Int): Any? {
        var obj: Any? = null
        try {
            ByteArrayInputStream(bytes).use { bis ->
                ObjectInputStream(bis).use { ois ->
                    obj = ois.readObject()
                }
            }
        } catch (e: IOException) {
            log.error("[rpc][序列化] 基于流的反序列化出现异常", e)
        }

        return obj
    }

    override fun getType(): Int {
        return 0
    }
}