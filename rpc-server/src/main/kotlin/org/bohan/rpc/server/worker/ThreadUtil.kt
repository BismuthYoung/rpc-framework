package org.bohan.rpc.server.worker

import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse
import org.bohan.rpc.server.provider.ServiceProvider
import java.lang.reflect.InvocationTargetException

class ThreadUtil {

    companion object {
        fun calculateThreadCountForIo(waitTime: Int, calculateTime: Int): Int {
            val cpuCores = Runtime.getRuntime().availableProcessors()
            return (cpuCores * (1 + waitTime.toDouble() / calculateTime)).toInt()
        }

         fun getResponse(serviceProvider: ServiceProvider, rpcRequest: RpcRequest): RpcResponse<Any?> {
            // 得到服务名
            val interfaceName = rpcRequest.interfaceName

            // 得到服务端相应服务实现类
            val service = serviceProvider.getService(interfaceName)

            // 反射调用方法
            return try {
                val method = service::class.java.getMethod(rpcRequest.methodName, *rpcRequest.paramsType)
                val invokeResult = method.invoke(service, *rpcRequest.params)
                log.info("方法执行结果为：$invokeResult")
                RpcResponse.success(invokeResult)
            } catch (e: NoSuchMethodException) {
                log.info("方法执行错误", e)
                RpcResponse.error()
            } catch (e: IllegalAccessException) {
                log.info("非法访问错误", e)
                RpcResponse.error()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
                RpcResponse.error()
            }
        }
    }

}