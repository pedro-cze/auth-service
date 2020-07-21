package cz.pedro.auth.service

import cz.pedro.auth.security.model.AuthRequester

interface TokenGenerationService {

    fun generateToken(user: AuthRequester): String
}
