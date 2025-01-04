package org.bohan.rpc.contract


import com.alibaba.fastjson2.JSON
import org.bohan.rpc.contract.domain.entity.User
import org.bohan.rpc.contract.domain.resp.RpcResponse
import org.junit.jupiter.api.Test

class SerializerTest {

    @Test
    fun deserializeTest() {
        val response = RpcResponse.success(User(42, "Bohan", true))
        val jsonResponse = JSON.toJSONBytes(response)
        val deserializedJson = JSON.parseObject(jsonResponse, RpcResponse::class.java)
        println(deserializedJson)
    }

}