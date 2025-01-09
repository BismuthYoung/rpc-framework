package org.bohan.rpc.client.client

import org.bohan.rpc.client.registry.ServiceCenter
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse

interface RpcClient {

    fun sendRequest(request: RpcRequest?): RpcResponse<*>?

    fun getServiceCenter(): ServiceCenter? { return null }

}