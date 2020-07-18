package cz.pedro.auth.service

import cz.pedro.auth.entity.User

interface TokenGenerationService {

    fun generateToken(user: User): String
}
