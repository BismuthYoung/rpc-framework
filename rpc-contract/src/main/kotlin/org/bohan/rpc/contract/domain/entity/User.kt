package org.bohan.rpc.contract.domain.entity

import java.io.Serializable

data class User(
    val id: Int,
    val username: String,
    val sex: Boolean
): Serializable
