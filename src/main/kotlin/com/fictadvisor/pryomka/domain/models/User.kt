package com.fictadvisor.pryomka.domain.models

data class User(
    val id: UserIdentifier,
    val name: String,
    val role: Role,
) {
    enum class Role {
        Entrant,
        Operator,
        Admin,
    }
}
