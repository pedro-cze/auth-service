package cz.pedro.auth.repository

import cz.pedro.auth.model.AppUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class DbInit : CommandLineRunner {

    @Autowired
    lateinit var repository: UserRepository
    @Autowired
    lateinit var encoder: PasswordEncoder

    override fun run(vararg args: String?) {
        val user = AppUser(1,"user", encoder.encode("user123"), "USER")
        val admin = AppUser(2,"admin", encoder.encode("admin123"), "ADMIN", "ACCESS_TEST1, ACCESS_TEST2")
        val manager = AppUser(3,"manager", encoder.encode("manager123"), "MANAGER", "ACCESS_TEST1")
        repository.saveAll(listOf(user, admin, manager))
    }
}
