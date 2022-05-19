package com.fictadvisor.pryomka.domain.mappers

import com.fictadvisor.pryomka.domain.models.*

/** Generates [User.Entrant] instance using this [TelegramData]. */
fun TelegramData.toEntrant() = User.Entrant(
    id = generateUserId(),
    telegramId = id,
    firstName = firstName,
    lastName = lastName,
    userName = userName,
    photoUrl = photoUrl,
)
