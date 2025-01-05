package org.bohan.rpc.contract.transformer.serializer.impl

import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONReader
import org.apache.zookeeper.server.Request
import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse
import org.bohan.rpc.contract.transformer.serializer.Serializer

@Slf4j
class JsonSerializer: Serializer {
    override fun serialize(obj: Any): ByteArray? {
        return try {
            JSONObject.toJSONBytes(obj)
        } catch (e: Exception) {
            log.error("fastjson 序列化错误", e)
            null
        }
    }

    override fun deserialize(bytes: ByteArray, messageType: Int): Any {
        return when (messageType) {
            // 将 Json 格式的请求反序列化为请求对象
            0 -> {
                val config = JSONReader.Feature.SupportClassForName
                val request = JSON.parseObject(bytes, RpcRequest::class.java, config)
                val paramsAndTypeRelation = request.paramsType.zip(request.params).toMap().toMutableMap()
                paramsAndTypeRelation.entries.forEach { entry ->
                    if (! entry.key.isAssignableFrom(entry.value.javaClass)) {
                        paramsAndTypeRelation[entry.key] = JSON.to(entry.key, entry.value)
                    }
                }
                RpcRequest(
                    request.interfaceName,
                    request.methodName,
                    paramsAndTypeRelation.values.toTypedArray(),
                    request.paramsType
                )
            }
            // 将 Json 格式的响应反序列化为请求对象
            1 -> {
                val response = JSON.parseObject(bytes, RpcResponse::class.java, JSONReader.Feature.SupportClassForName)
                if (response.dataType != null && response.data != null) {
                    if (! response.dataType.isAssignableFrom(response.data.javaClass)) {
                        RpcResponse(
                            message = response.message,
                            status = response.status,
                            data = JSON.to(response.dataType, response.data),
                            dataType = response.dataType
                        )
                    } else {
                        response
                    }
                } else if (response.data == null && response.dataType == null) {
                    response
                } else {
                    log.error("[rpc][序列化] 响应 $response 格式错误")
                    throw IllegalArgumentException("响应格式错误")
                }
            }
            else -> {
                log.error("[rpc][contract] 不支持的消息类型")
                throw IllegalArgumentException("Json 序列化器不支持编码为 $messageType 的消息类型")
            }
        }
    }

    override fun getType(): Int {
        return 1
    }
}