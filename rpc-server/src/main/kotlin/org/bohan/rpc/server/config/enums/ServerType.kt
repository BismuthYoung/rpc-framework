package org.bohan.rpc.server.config.enums

enum class ServerType(val serverName: String) {

    SIMPLE_RPC_SERVER("simple"),

    THREAD_POOL_RPC_SERVER("thread"),

    NETTY_RPC_SERVER("netty");

    companion object {
        fun getServerEnum(serverName: String): ServerType {
            return values().find { it.serverName == serverName } ?: throw IllegalArgumentException("未知的服务类型")
        }
    }
}