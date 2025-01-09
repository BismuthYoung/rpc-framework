package org.bohan.rpc.client.registry.impl

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.registry.Balancer

@Slf4j
class RoundBalancer: Balancer {

    private var roundRobinIndex = -1

    override fun selectServer(addressStringList: List<String>): String {
        log.debug("[rpc][客户端] 轮转负载均衡算法选择了 ${++ roundRobinIndex} 服务")
        return addressStringList[roundRobinIndex % addressStringList.size]
    }

    override fun addNode(node: String) {
        TODO("Not yet implemented")
    }

    override fun deleteNode(node: String) {
        TODO("Not yet implemented")
    }
}