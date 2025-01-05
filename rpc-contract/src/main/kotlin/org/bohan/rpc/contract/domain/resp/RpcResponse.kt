package org.bohan.rpc.contract.domain.resp

import lombok.Builder
import org.bohan.rpc.contract.domain.enums.ResponseStatus
import java.io.Serializable

@Builder
data class RpcResponse<T>(
    val message: String? = null,
    val status: Int? = null,
    val data: T? = null,
    val dataType: Class<*>? = null
): Serializable {
    companion object {
        fun <T> success(): RpcResponse<T?> {
            return RpcResponse(status = ResponseStatus.SUCCESS.code, message = ResponseStatus.SUCCESS.msg)
        }

        fun <T> error(message: String?): RpcResponse<T?> {
            return RpcResponse(status = ResponseStatus.ERROR.code, message = message ?: "")
        }

        fun <T> error(): RpcResponse<T?> {
            return RpcResponse(status = ResponseStatus.ERROR.code)
        }

        fun <T> paramError(data: T?): RpcResponse<T?> {
            return RpcResponse(status = ResponseStatus.PARAM_ERROR.code)
        }

        fun <T> paramError(): RpcResponse<T?> {
            return RpcResponse(status = ResponseStatus.PARAM_ERROR.code)
        }

        fun <T> success(data: T): RpcResponse<T> {
            return RpcResponse(status = ResponseStatus.SUCCESS.code, message = ResponseStatus.SUCCESS.msg, data = data, dataType = data!!::class.java)
        }

        fun <T> build(status: ResponseStatus): RpcResponse<T?> {
            return RpcResponse(status = status.code, message = status.msg)
        }
    }

    fun successful(): Boolean {
        return this.status == ResponseStatus.SUCCESS.code
    }
}
