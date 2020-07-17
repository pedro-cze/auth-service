package cz.pedro.auth.service

import cz.pedro.auth.entity.User
import org.springframework.stereotype.Service

@Service
interface TokenGenerationService {

    fun generateToken(user: User): String

}
