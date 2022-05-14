package com.fictadvisor.pryomka.domain.interactors.faculty

import com.fictadvisor.pryomka.domain.datasource.faculty.LearningFormatsDataSource
import com.fictadvisor.pryomka.domain.datasource.faculty.SpecialitiesDataSource
import com.fictadvisor.pryomka.domain.models.LearningFormatIdentifier
import com.fictadvisor.pryomka.domain.models.faculty.LearningFormat
import com.fictadvisor.pryomka.domain.models.faculty.Speciality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlin.coroutines.coroutineContext

interface SpecialitiesUseCases {
    suspend fun allSpecialities(): List<Speciality>
    suspend fun allSpecialitiesWithLearningFormats(): Map<Speciality, List<LearningFormat>>
    suspend fun getSpeciality(code: Int): Speciality
    suspend fun createSpeciality(speciality: Speciality)
    suspend fun editSpeciality(code: Int, newSpeciality: Speciality)
    suspend fun setLearningFormats(specialityCode: Int, learningFormatIds: List<LearningFormatIdentifier>)

    /** **Warning**: This action will also delete all application with this speciality! */
    suspend fun deleteSpeciality(code: Int)
}

class SpecialitiesUseCasesImpl(
    private val specialitiesDataSource: SpecialitiesDataSource,
    private val learningFormatsDataSource: LearningFormatsDataSource,
) : SpecialitiesUseCases {
    override suspend fun allSpecialities() = specialitiesDataSource.allSpecialities()

    override suspend fun allSpecialitiesWithLearningFormats(): Map<Speciality, List<LearningFormat>> {
        val scope = CoroutineScope(coroutineContext)

        return allSpecialities().associateWith {
            scope.async {
                learningFormatsDataSource.getAllFormats(it)
            }
        }.mapValues { (_, v) -> v.await() }
    }

    override suspend fun getSpeciality(code: Int): Speciality = specialitiesDataSource.getSpeciality(code)

    override suspend fun createSpeciality(speciality: Speciality) = specialitiesDataSource.createSpeciality(speciality)

    override suspend fun editSpeciality(code: Int, newSpeciality: Speciality) = specialitiesDataSource.editSpeciality(code, newSpeciality)

    override suspend fun setLearningFormats(specialityCode: Int, learningFormatIds: List<LearningFormatIdentifier>) {
        specialitiesDataSource.setLearningFormats(specialityCode, learningFormatIds)
    }

    override suspend fun deleteSpeciality(code: Int) = specialitiesDataSource.deleteSpeciality(code)
}
