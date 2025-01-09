package org.bohan.rpc.client.registry.impl

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.registry.Balancer
import java.util.UUID

@Slf4j
class ConsistencyHashBalancer: Balancer {

    private companion object {
        const val VIRTUAL_NUM = 5
    }

    private val shards = sortedMapOf<Int, String>()

    private val realNodes = mutableListOf<String>()

    private fun init(addressStringList: List<String>) {
        addressStringList.forEach { service ->
            addNode(service)
        }
    }

    private fun getServer(node: String, addressStringList: List<String>): String {
        init(addressStringList)
        val hash = getHash(node)
        val key = shards.tailMap(hash).firstKey() ?: shards.lastKey()

        return shards[key]?.substringBefore("&&") ?: throw IllegalArgumentException("虚拟节点为空")
    }

    override fun selectServer(addressStringList: List<String>): String {
        val randomString = UUID.randomUUID().toString()
        return getServer(randomString, addressStringList)
    }

    override fun addNode(node: String) {
        if (! realNodes.contains(node)) {
            realNodes.add(node)
            log.debug("[rpc][客户端] 真实节点 $node 被添加")
            (0 until VIRTUAL_NUM).forEach { i ->
                val virtualNode = "$node&&VN$i"
                val hash = getHash(virtualNode)
                shards[hash] = virtualNode
                log.debug("[rpc][客户端] 虚拟节点 $virtualNode 被添加，哈希值为 $hash")
            }
        }
    }

    override fun deleteNode(node: String) {
        if (realNodes.contains(node)) {
            realNodes.remove(node)
            log.debug("[rpc][客户端] 真实节点 $node 被移除")
            (0 until VIRTUAL_NUM).forEach { i ->
                val virtualNode = "$node&&VN$i"
                val hash = getHash(virtualNode)
                shards.remove(hash)
                log.debug("[rpc][客户端] 虚拟节点 $virtualNode 被移除")
            }
        }
    }

    /**
     * FNV1_32_HASH算法
     */
    private fun getHash(str: String): Int {
        val p = 16777619
        var hash = 2166136261L.toInt()
        for (i in str.indices) {
            hash = (hash xor str[i].toInt()) * p
        }
        hash += hash shl 13
        hash = hash xor (hash shr 7)
        hash += hash shl 3
        hash = hash xor (hash shr 17)
        hash += hash shl 5
        // 如果算出来的值为负数则取其绝对值
        return if (hash < 0) Math.abs(hash) else hash
    }
}