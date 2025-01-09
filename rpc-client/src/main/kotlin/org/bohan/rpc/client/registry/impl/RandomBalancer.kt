package org.bohan.rpc.client.registry.impl

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.registry.Balancer
import kotlin.random.Random
import kotlin.random.nextInt

@Slf4j
class RandomBalancer: Balancer {
    override fun selectServer(addressStringList: List<String>): String {
        val idx = Random(42).nextInt(addressStringList.indices)
        log.debug("[rpc][客户端] 随机负载均衡算法选择了第 $idx 个节点")

        return addressStringList[idx]
    }

    override fun addNode(node: String) {
    }

    override fun deleteNode(node: String) {
    }
}