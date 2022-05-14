package com.fictadvisor.pryomka.api.dto.faculty

import kotlinx.serialization.Serializable

@Serializable
data class AllDetailedSpecialitiesDto(val specialities: List<SpecialityDetailedDto>)
