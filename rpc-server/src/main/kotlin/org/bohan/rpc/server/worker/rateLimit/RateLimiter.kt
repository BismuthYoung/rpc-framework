package org.bohan.rpc.server.worker.rateLimit

interface RateLimiter {

    fun getToken(): Boolean

}