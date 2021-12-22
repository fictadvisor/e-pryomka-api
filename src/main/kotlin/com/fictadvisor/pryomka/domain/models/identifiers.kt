package com.fictadvisor.pryomka.domain.models

import java.util.*

@JvmInline value class UserIdentifier(val value: UUID)
@JvmInline value class DocumentIdentifier(val value: UUID)
@JvmInline value class ApplicationIdentifier(val value: UUID)
typealias DocumentKey = String
