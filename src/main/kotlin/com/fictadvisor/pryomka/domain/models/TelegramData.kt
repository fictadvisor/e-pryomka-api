package com.fictadvisor.pryomka.domain.models

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.long

/** Data which user sends when authorizes using Telegram.
 * @param firstName user's first name in the Telegram
*/
typealias TelegramData = Map<String, JsonElement>

private fun TelegramData.getPrimitive(key: String) = get(key) as JsonPrimitive
private fun TelegramData.getPrimitiveOrNull(key: String) = get(key) as? JsonPrimitive

val TelegramData.authDate: Long get() = getPrimitive("auth_date").long

val TelegramData.firstName: String get() = getPrimitive("first_name").content

val TelegramData.id: Long get() = getPrimitive("id").long

val TelegramData.lastName: String? get() = getPrimitiveOrNull("last_name")?.contentOrNull

val TelegramData.photoUrl: String? get() = getPrimitiveOrNull("photo_url")?.contentOrNull

val TelegramData.userName: String? get() = getPrimitiveOrNull("username")?.contentOrNull
