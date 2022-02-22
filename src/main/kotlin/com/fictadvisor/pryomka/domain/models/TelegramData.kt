package com.fictadvisor.pryomka.domain.models

data class TelegramData(
    val authDate: Long,
    val firstName: String,
    val id: Long,
    val lastName: String? = null,
    val photoUrl: String? = null,
    val userName: String? = null,
    val hash: String
) {
    val dataCheckString get() = buildString {
        append("auth_date=$authDate\n")
        append("first_name=$firstName\n")
        append("id=$id")
        lastName?.let { append("last_name=$lastName") }
        photoUrl?.let { append("photo_url=$photoUrl") }
        userName?.let { append("username=$userName") }
    }
}
