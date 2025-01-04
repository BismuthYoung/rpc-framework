package org.bohan.rpc.server.provider

interface ServiceProvider {

    fun provideServiceInterface(service: Any)

    fun getService(interfaceName: String?): Any

}