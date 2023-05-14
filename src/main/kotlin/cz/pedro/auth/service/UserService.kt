package cz.pedro.auth.service

import cz.pedro.auth.data.UserDetailDto
import cz.pedro.auth.data.UserDto
import cz.pedro.auth.entity.User
import java.util.UUID

interface UserService {

    fun getUsers(): List<UserDto>

    fun getUsersByServiceName(serviceName: String): List<User>

    fun getUserDetail(userId: UUID): UserDetailDto?
}
