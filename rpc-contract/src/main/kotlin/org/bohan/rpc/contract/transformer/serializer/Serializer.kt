package org.bohan.rpc.contract.transformer.serializer

import org.bohan.rpc.contract.transformer.serializer.impl.JsonSerializer
import org.bohan.rpc.contract.transformer.serializer.impl.ObjectSerializer

interface Serializer {

    /**
     * 把对象序列化成字节数组
     */
    fun serialize(obj: Any): ByteArray?

    /**
     * 从字节数组反序列化成消息, 使用java自带序列化方式不用messageType也能得到相应的对象（序列化字节数组里包含类信息）
     * 其它方式需指定消息格式，再根据message转化成相应的对象
     */
    fun deserialize(bytes: ByteArray, messageType: Int): Any?

    /**
     * 0：java自带序列化方式
     * 1: json序列化方式
     */
    fun getType(): Int

    /**
     * 根据编号取出序列化器
     */
    companion object {
        fun getSerializerByCode(code: Int): Serializer? {
            return when (code) {
                0 -> ObjectSerializer()
                1 -> JsonSerializer()
                else -> null
            }
        }
    }

}