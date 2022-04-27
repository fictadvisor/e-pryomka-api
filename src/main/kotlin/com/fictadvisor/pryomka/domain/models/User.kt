package com.fictadvisor.pryomka.domain.models

/** User of the system.
 * @param id unique user identifier */
sealed class User(
    val id: UserIdentifier
) {
    /** Whether it is an [Entrant] */
    abstract val isEntrant: Boolean

    /** Whether it is an [Staff] with [Staff.Role.Operator]. */
    abstract val isOperator: Boolean

    /** Whether it is an [Staff] with [Staff.Role.Admin]. */
    abstract val isAdmin: Boolean

    /** Entrant that submits application to FICT.
     * @param telegramId unique user identifier in the Telegram
     * @param firstName user's first name in the Telegram
     * @param lastName user's last name in the Telegram
     * @param userName user's tag in the Telegram
     * @param photoUrl url of a user's Telegram avatar
     * */
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

    /** Staff of the Entrance Committee.
     * @param name login
     * @param role operator or admin */
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
