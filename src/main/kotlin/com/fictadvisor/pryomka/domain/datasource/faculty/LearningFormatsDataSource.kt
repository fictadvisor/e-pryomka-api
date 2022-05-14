package com.fictadvisor.pryomka.domain.datasource.faculty

import com.fictadvisor.pryomka.domain.models.LearningFormatIdentifier
import com.fictadvisor.pryomka.domain.models.faculty.LearningFormat
import com.fictadvisor.pryomka.domain.models.faculty.Speciality

interface LearningFormatsDataSource {
    suspend fun getAllFormats(): List<LearningFormat>
    suspend fun getAllFormats(speciality: Speciality): List<LearningFormat>
    suspend fun getFormat(id: LearningFormatIdentifier): LearningFormat
    suspend fun createFormat(format: LearningFormat)
    suspend fun editFormat(id: LearningFormatIdentifier, newName: String)
    suspend fun deleteFormat(id: LearningFormatIdentifier)
}
