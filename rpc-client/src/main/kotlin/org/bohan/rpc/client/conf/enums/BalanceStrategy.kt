package org.bohan.rpc.client.conf.enums

import org.bohan.rpc.client.registry.Balancer
import org.bohan.rpc.client.registry.impl.ConsistencyHashBalancer
import org.bohan.rpc.client.registry.impl.RandomBalancer
import org.bohan.rpc.client.registry.impl.RoundBalancer

enum class BalanceStrategy(val strategyName: String, val strategy: Balancer) {

    RANDOM("random", RandomBalancer()),

    ROUND_ROBIN("round", RoundBalancer()),

    CONSISTENCY_HASH("hash", ConsistencyHashBalancer());

    companion object {
        fun getStrategyByName(name: String): Balancer {
            return values().find { it.strategyName == name }?.strategy ?: throw NoSuchElementException("无法根据名称找到策略")
        }
    }

}