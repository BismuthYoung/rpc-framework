package org.bohan.rpc.contract.domain.enums

enum class ResponseStatus(
    val code: Int,
    val msg: String
) {

    SUCCESS(0, "成功"),

    ERROR(-1, "内部系统错误"),

    PARAM_ERROR(400, "参数错误"),

}