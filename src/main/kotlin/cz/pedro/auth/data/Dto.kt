package cz.pedro.auth.data

import cz.pedro.auth.model.Model

interface Dto {

    fun toModel(): Model

}
