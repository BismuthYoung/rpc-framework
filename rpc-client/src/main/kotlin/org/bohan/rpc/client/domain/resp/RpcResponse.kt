package org.bohan.rpc.client.domain.resp

import lombok.Builder
import org.bohan.rpc.client.domain.enums.ResponseStatus

@Builder
data class RpcResponse<T>(
    val message: String? = null,
    val status: Int? = null,
    val data: T? = null
) {
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
            return RpcResponse(status = ResponseStatus.SUCCESS.code, message = ResponseStatus.SUCCESS.msg, data = data)
        }

        fun <T> build(status: ResponseStatus): RpcResponse<T?> {
            return RpcResponse(status = status.code, message = status.msg)
        }
    }

    fun successful(): Boolean {
        return this.status == ResponseStatus.SUCCESS.code
    }
}
