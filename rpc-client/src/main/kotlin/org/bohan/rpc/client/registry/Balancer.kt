package org.bohan.rpc.client.registry

interface Balancer {

    fun selectServer(addressStringList: List<String>): String

    fun addNode(node: String): Unit

    fun deleteNode(node: String): Unit

}