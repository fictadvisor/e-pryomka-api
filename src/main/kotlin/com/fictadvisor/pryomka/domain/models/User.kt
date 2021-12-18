package com.fictadvisor.pryomka.domain.models

data class User(
    val id: UserIdentifier,
    val role: Role,
) {
    enum class Role {
        Entrant,
        Operator,
        Admin,
    }
}
