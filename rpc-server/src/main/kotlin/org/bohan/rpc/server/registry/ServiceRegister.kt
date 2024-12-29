package org.bohan.rpc.server.registry

import java.net.InetSocketAddress

interface ServiceRegister {

    fun register(serviceName: String, serviceAddress: InetSocketAddress)

}