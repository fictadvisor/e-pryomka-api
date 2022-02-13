package com.fictadvisor.pryomka.domain.models

sealed class User(
    val id: UserIdentifier
) {
    abstract val isEntrant: Boolean
    abstract val isOperator: Boolean
    abstract val isAdmin: Boolean

    class Entrant(
        id: UserIdentifier,
        val telegramId: Long,
        val firstName: String,
        val lastName: String? = null,
        val userName: String? = null,
        val photoUrl: String? = null,
    ) : User(id) {
        override val isEntrant: Boolean = true
        override val isOperator: Boolean = false
        override val isAdmin: Boolean = false
    }

    class Staff(
        id: UserIdentifier,
        val name: String,
        val role: Role,
    ) : User(id) {
        enum class Role {
            Operator,
            Admin,
        }

        override val isEntrant: Boolean = false
        override val isOperator: Boolean = role == Role.Operator
        override val isAdmin: Boolean = role == Role.Admin
    }
}
