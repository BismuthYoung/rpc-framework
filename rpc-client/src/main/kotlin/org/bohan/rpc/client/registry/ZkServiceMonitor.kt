package org.bohan.rpc.client.registry

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.cache.ChildData
import org.apache.curator.framework.recipes.cache.CuratorCache
import org.apache.curator.framework.recipes.cache.CuratorCacheListener
import org.bohan.rpc.client.cache.impl.SimpleServiceCache

class ZkServiceMonitor(
    private val client: CuratorFramework,
    private val serviceCache: SimpleServiceCache
) {

    /**
     * zk 中的路径形如 /rpc-frame/order-service/192.168.1.100:8080，解析为路径后为
     *  ["rpc-frame", "order-service", "192.168.1.100:8080"]
     */

    fun watchToUpdate(path: String) {
        val curatorCache = CuratorCache.build(client, path)
        curatorCache.listenable().addListener(CuratorCacheListener { type, childData, childData1 ->
            when (type) {
                CuratorCacheListener.Type.NODE_CREATED -> handleNodeCreated(childData1)
                CuratorCacheListener.Type.NODE_CHANGED -> handleNodeChanged(childData, childData1)
                CuratorCacheListener.Type.NODE_DELETED -> handleNodeDeleted(childData)
                else -> {}
            }
        })

        curatorCache.start()
    }

    private fun handleNodeCreated(childData: ChildData) {
        val pathList = parsePath(childData)
        if (pathList.size > 2) {
            serviceCache.addServiceToCache(pathList[1], pathList[2])
        }
    }

    private fun handleNodeChanged(oldChildData: ChildData, newChildData: ChildData) {
        val oldPathList = parsePath(oldChildData)
        val newPathList = parsePath(newChildData)

        serviceCache.replaceServiceAddress(oldPathList[1], oldPathList[2], newPathList[2])
    }

    private fun handleNodeDeleted(childData: ChildData) {
        val pathList = parsePath(childData)
        if (pathList.size > 2) {
            serviceCache.deleteService(pathList[1], pathList[2])
        }
    }

    private fun parsePath(childData: ChildData): Array<String> {
        val path = childData.path
        return path.split("/").toTypedArray()
    }

}