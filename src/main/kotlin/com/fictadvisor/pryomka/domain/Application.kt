package com.fictadvisor.pryomka.domain

@JvmInline value class Path(val value: String)

data class Application(
    val userId: Int,
    val passport: Path? = null,
    val photo: Path? = null,
    val contract: Path? = null,
)
