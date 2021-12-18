package com.fictadvisor.pryomka.domain.models

data class Application(
    val userId: UserIdentifier,
    val passport: Document? = null,
    val photo: Document? = null,
    val contract: Document? = null,
)
