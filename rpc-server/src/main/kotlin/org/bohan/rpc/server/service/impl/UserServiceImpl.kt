package org.bohan.rpc.server.service.impl

import org.bohan.rpc.contract.domain.entity.User
import org.bohan.rpc.contract.service.UserService
import kotlin.random.Random

class UserServiceImpl: UserService {
    override fun getUserById(id: Int): User? {
        val seed = Random(42)
        val randomNum = seed.nextInt()
        return if (randomNum % 42 != 0) User(randomNum, "Bohan", true) else null
    }

    override fun insertUser(user: User): Int {
        println("用户 ${user.id} ${user.username} ${user.sex} 已插入")
        return 0
    }
}