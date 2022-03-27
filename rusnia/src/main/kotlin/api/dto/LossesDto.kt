package api.dto

import domain.model.TotalLosses
import kotlinx.serialization.Serializable

@Serializable
data class LossesDto(val losses: TotalLosses)
