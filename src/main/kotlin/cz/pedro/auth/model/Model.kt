package cz.pedro.auth.model

import cz.pedro.auth.data.Dto

interface Model {

    fun toDto(): Dto

}
