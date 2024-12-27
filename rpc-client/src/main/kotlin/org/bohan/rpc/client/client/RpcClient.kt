package org.bohan.rpc.client.client

import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse

interface RpcClient {

    fun sendRequest(request: RpcRequest?): RpcResponse<*>?

}