package org.bohan.rpc.server.registry

import java.net.InetSocketAddress

interface ServiceRegister {

    /**
     * @param canRetry 用于控制该服务是否是幂等的。
     *  如果 canRetry 为 true，表示该服务支持重试，那么会在 Zookeeper 中的 RETRY 路径下创建一个临时节点，表示该服务是可以在故障或熔断时重试的服务。
     *  如果 canRetry 为 false，则不会在 RETRY 路径下创建任何节点，表示该服务不支持重试。
     */
    fun register(serviceName: String, serviceAddress: InetSocketAddress, canRetry: Boolean)

}