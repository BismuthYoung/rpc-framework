package org.bohan.rpc.client

import org.bohan.component.common.hocon.ConfigLoader
import org.bohan.rpc.client.client.impl.NettyRpcClient
import org.bohan.rpc.client.client.impl.SimpleSocketRpcClient
import org.bohan.rpc.client.conf.ClientConfig
import org.bohan.rpc.client.conf.enums.ClientType
import org.bohan.rpc.client.proxy.ClientProxy
import org.bohan.rpc.client.registry.impl.ZkServiceCenter
import org.bohan.rpc.contract.service.UserService
import org.slf4j.LoggerFactory


fun main() {
    val config = ConfigLoader.loadConfig(ClientConfig::class.java)
    val serviceCenter = ZkServiceCenter()
    val client = when (ClientType.getEnumByName(config.clientType)) {
        ClientType.SIMPLE_RPC_CLIENT -> SimpleSocketRpcClient(config.host, config.port)
        ClientType.NETTY_RPC_CLIENT -> NettyRpcClient(serviceCenter)
    }

    val proxy = ClientProxy(client)
    val proxyUserService = proxy.getProxy(UserService::class.java)
    println(proxyUserService.getUserById(42))
}