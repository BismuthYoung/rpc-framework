package org.bohan.rpc.client.registry

import java.net.InetSocketAddress

interface ServiceCenter {

    fun serviceDiscovery(serviceName: String): InetSocketAddress?

    fun checkRetry(serviceName: String): Boolean

}