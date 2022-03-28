package api.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class LossesDto(val losses: Map<LocalDate, Map<String, Long>>)
