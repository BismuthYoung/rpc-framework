package org.bohan.rpc.client.proxy.breaker.enums

enum class CircuitBreakerState {

    /**
     * 在该状态下，熔断器允许请求通过，且不会干预服务调用。
     * 正常情况下，熔断器应该在 CLOSED 状态。
     */
    OPEN,

    /**
     * 在该状态下，熔断器会允许一些请求通过，目的是检查服务是否已经恢复。
     * 只有在这些请求成功的比例达到一定阈值，熔断器才会重新切换回 CLOSED 状态，否则就会切换回 OPEN 状态。
     */
    HALF_OPEN,

    /**
     * 在该状态下，熔断器拒绝所有请求，防止继续发送请求给已知故障的服务。
     * 它会记录最后一次失败的时间，并在一定的恢复时间之后尝试切换到 HALF_OPEN 状态。
     */
    CLOSED;

}