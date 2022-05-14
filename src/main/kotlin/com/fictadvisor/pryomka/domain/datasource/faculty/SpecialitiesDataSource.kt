package com.fictadvisor.pryomka.domain.datasource.faculty

import com.fictadvisor.pryomka.domain.models.LearningFormatIdentifier
import com.fictadvisor.pryomka.domain.models.faculty.Speciality

interface SpecialitiesDataSource {
    suspend fun allSpecialities(): List<Speciality>
    suspend fun getSpeciality(code: Int): Speciality
    suspend fun createSpeciality(speciality: Speciality)
    suspend fun editSpeciality(code: Int, newSpeciality: Speciality)
    suspend fun setLearningFormats(specialityCode: Int, learningFormatIds: List<LearningFormatIdentifier>)
    suspend fun deleteSpeciality(code: Int)
}
