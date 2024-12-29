package org.bohan.rpc.client.conf.enums

enum class ClientType(val clientName: String) {

    SIMPLE_RPC_CLIENT("simple"),

    NETTY_RPC_CLIENT("netty");

    companion object {
        fun getEnumByName(name: String): ClientType {
            return values().find { it.clientName == name } ?: throw IllegalArgumentException("未知的客户端名称")
        }
    }
}