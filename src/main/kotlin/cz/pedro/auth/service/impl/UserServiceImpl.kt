package cz.pedro.auth.service.impl

import cz.pedro.auth.data.UserDetailDto
import cz.pedro.auth.data.UserDto
import cz.pedro.auth.entity.User
import cz.pedro.auth.repository.UserRepository
import cz.pedro.auth.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.Optional

@Service
class UserServiceImpl(@Autowired val userRepository: UserRepository) : UserService {

    override fun getUsers(): List<UserDto> {
        return userRepository.findAll().map { UserDto(
            id = it.id!!,
            serviceName = it.serviceName,
            firstName = it.firstName,
            lastName = it.lastName,
            email = it.email,
            userName = it.username,
            password = it.password,
            authorities = it.authorities,
            active = it.active
        ) }
    }

    override fun getUsersByServiceName(serviceName: String): List<User> {
        // TODO validate serviceName

        return userRepository.findByServiceName(serviceName)
    }

    override fun getUserDetail(userId: UUID): UserDetailDto? {
        val user: Optional<User> = userRepository.findById(userId)
        return user.let {
            UserDetailDto()
        }
    }
}
