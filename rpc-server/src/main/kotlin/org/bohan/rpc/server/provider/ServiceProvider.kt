package org.bohan.rpc.server.provider

import org.bohan.rpc.server.worker.rateLimit.RateLimiter

interface ServiceProvider {

    fun provideServiceInterface(service: Any) {}

    fun providerServiceInterface(service: Any, canRetry: Boolean) {}

    fun getService(interfaceName: String?): Any

    fun getRateLimiter(interfaceName: String?): RateLimiter

}