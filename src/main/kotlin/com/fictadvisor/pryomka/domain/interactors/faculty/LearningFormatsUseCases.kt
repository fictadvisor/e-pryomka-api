package com.fictadvisor.pryomka.domain.interactors.faculty

import com.fictadvisor.pryomka.domain.datasource.faculty.LearningFormatsDataSource
import com.fictadvisor.pryomka.domain.models.LearningFormatIdentifier
import com.fictadvisor.pryomka.domain.models.faculty.LearningFormat
import com.fictadvisor.pryomka.domain.models.faculty.Speciality
import com.fictadvisor.pryomka.domain.models.generateLearningFormatId

interface LearningFormatsUseCases {
    suspend fun getAllFormats(): List<LearningFormat>
    suspend fun getAllFormats(speciality: Speciality): List<LearningFormat>
    suspend fun getFormat(id: LearningFormatIdentifier): LearningFormat
    suspend fun createFormat(name: String): LearningFormat
    suspend fun editFormat(id: LearningFormatIdentifier, newName: String)

    /** **Warning**: deletion of the format leads to deletion of all application with this format */
    suspend fun deleteFormat(id: LearningFormatIdentifier)
}

class LearningFormatsUseCasesImpl(private val ds: LearningFormatsDataSource) : LearningFormatsUseCases {
    override suspend fun getAllFormats(): List<LearningFormat> = ds.getAllFormats()
    override suspend fun getAllFormats(speciality: Speciality): List<LearningFormat> = ds.getAllFormats(speciality)

    override suspend fun getFormat(id: LearningFormatIdentifier): LearningFormat = ds.getFormat(id)

    override suspend fun createFormat(name: String): LearningFormat {
        val format = LearningFormat(generateLearningFormatId(), name)
        ds.createFormat(format)
        return format
    }

    override suspend fun editFormat(id: LearningFormatIdentifier, newName: String) {
        ds.editFormat(id, newName)
    }

    override suspend fun deleteFormat(id: LearningFormatIdentifier) = ds.deleteFormat(id)
}
