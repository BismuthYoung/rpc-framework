package org.bohan.rpc.server.service.impl

import org.bohan.rpc.contract.domain.entity.User
import org.bohan.rpc.contract.service.UserService
import kotlin.random.Random

class UserServiceImpl: UserService {
    override fun getUserById(id: Int): User? {
        val seed = Random(42)
        val randomNum = seed.nextInt()
        if (randomNum % 42 == 0) {
            return null
        } else {
            return User(randomNum, "Bohan", true)
        }
    }

    override fun insertUser(user: User): Int {
        println("用户 ${user.id} ${user.username} ${user.sex} 已插入")
        return 0
    }
}