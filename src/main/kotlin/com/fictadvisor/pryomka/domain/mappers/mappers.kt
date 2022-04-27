package com.fictadvisor.pryomka.domain.mappers

import com.fictadvisor.pryomka.domain.models.TelegramData
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.generateUserId

/** Generates [User.Entrant] instance using this [TelegramData]. */
fun TelegramData.toEntrant() = User.Entrant(
    id = generateUserId(),
    telegramId = id,
    firstName = firstName,
    lastName = lastName,
    userName = userName,
    photoUrl = photoUrl,
)
