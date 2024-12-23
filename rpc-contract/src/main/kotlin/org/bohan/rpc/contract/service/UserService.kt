package org.bohan.rpc.contract.service

import org.bohan.rpc.contract.domain.entity.User

interface UserService {

    fun getUserById(id: Int): User?

    fun insertUser(user: User): Int

}