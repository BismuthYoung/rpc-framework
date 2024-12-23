package org.bohan.rpc.client.domain.req

import lombok.Builder
import java.io.Serializable

@Builder
data class RpcRequest(
    val interfaceName: String,
    val methodName: String,
    val params: Array<Any>,
    val paramsType: Array<Class<*>?>?
): Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RpcRequest

        if (interfaceName != other.interfaceName) return false
        if (methodName != other.methodName) return false
        return params.contentEquals(other.params)
    }

    override fun hashCode(): Int {
        var result = interfaceName.hashCode()
        result = 31 * result + methodName.hashCode()
        result = 31 * result + params.contentHashCode()
        return result
    }
}
