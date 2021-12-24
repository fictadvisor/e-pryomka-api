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

fun User.Role.canApply(status: Application.Status) = when (this) {
    User.Role.Entrant -> status == Application.Status.Cancelled
    User.Role.Operator -> status == Application.Status.Rejected || status == Application.Status.Approved
    User.Role.Admin -> true
}
