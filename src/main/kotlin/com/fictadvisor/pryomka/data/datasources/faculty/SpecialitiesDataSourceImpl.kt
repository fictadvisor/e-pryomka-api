package com.fictadvisor.pryomka.data.datasources.faculty

import com.fictadvisor.pryomka.data.db.LearningFormats
import com.fictadvisor.pryomka.data.db.Specialities
import com.fictadvisor.pryomka.data.db.SpecialitiesFormats
import com.fictadvisor.pryomka.data.db.SpecialitiesFormats.learningFormat
import com.fictadvisor.pryomka.data.db.SpecialitiesFormats.speciality
import com.fictadvisor.pryomka.data.db.db
import com.fictadvisor.pryomka.data.mappers.toSpeciality
import com.fictadvisor.pryomka.domain.datasource.faculty.SpecialitiesDataSource
import com.fictadvisor.pryomka.domain.models.LearningFormatIdentifier
import com.fictadvisor.pryomka.domain.models.faculty.Speciality
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.selects.select
import org.jetbrains.exposed.sql.*

class SpecialitiesDataSourceImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : SpecialitiesDataSource {
    override suspend fun allSpecialities(): List<Speciality> = db(dispatcher) {
        val lf = LearningFormats.alias("lf")
        val s = Specialities.alias("s")

        SpecialitiesFormats
            .leftJoin(lf, { learningFormat }, { lf[LearningFormats.id] })
            .rightJoin(s, { speciality }, { s[Specialities.id] })
            .selectAll()
            .map {
                Speciality(
                    code = it[s[Specialities.code]],
                    name = it[s[Specialities.name]]
                )
            }
    }

    override suspend fun getSpeciality(code: Int): Speciality = db(dispatcher) {
        Specialities.select { Specialities.code eq code }
            .limit(1)
            .map { it.toSpeciality() }
            .first()
    }

    override suspend fun createSpeciality(speciality: Speciality): Unit = db(dispatcher) {
        Specialities.insert {
            it[code] = speciality.code
            it[name] = speciality.name
        }
    }

    override suspend fun editSpeciality(code: Int, newSpeciality: Speciality): Unit = db(dispatcher) {
        Specialities.update(
            where = { Specialities.code eq code }
        ) {
            it[Specialities.code] = newSpeciality.code
            it[name] = newSpeciality.name
        }
    }

    override suspend fun setLearningFormats(
        specialityCode: Int,
        learningFormatIds: List<LearningFormatIdentifier>,
    ) = db(dispatcher) {
        val specialityId = Specialities
            .select { Specialities.code eq specialityCode }
            .limit(1)
            .map { it[Specialities.id] }
            .first()

        SpecialitiesFormats.deleteWhere { SpecialitiesFormats.speciality eq specialityId }

        learningFormatIds.forEach { format ->
            SpecialitiesFormats.insert {
                it[learningFormat] = format.value
                it[speciality] = specialityId
            }
        }
    }

    override suspend fun deleteSpeciality(code: Int): Unit = db(dispatcher) {
        Specialities.deleteWhere { Specialities.code eq code }
    }
}
