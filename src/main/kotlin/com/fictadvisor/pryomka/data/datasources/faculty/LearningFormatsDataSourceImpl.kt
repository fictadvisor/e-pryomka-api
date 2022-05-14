package com.fictadvisor.pryomka.data.datasources.faculty

import com.fictadvisor.pryomka.data.db.LearningFormats
import com.fictadvisor.pryomka.data.db.Specialities
import com.fictadvisor.pryomka.data.db.SpecialitiesFormats
import com.fictadvisor.pryomka.data.mappers.toLearningFormat
import com.fictadvisor.pryomka.domain.datasource.faculty.LearningFormatsDataSource
import com.fictadvisor.pryomka.domain.models.LearningFormatIdentifier
import com.fictadvisor.pryomka.domain.models.faculty.LearningFormat
import com.fictadvisor.pryomka.domain.models.faculty.Speciality
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class LearningFormatsDataSourceImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LearningFormatsDataSource {
    override suspend fun getAllFormats(): List<LearningFormat> = newSuspendedTransaction(dispatcher) {
        LearningFormats.selectAll().map { it.toLearningFormat() }
    }

    override suspend fun getAllFormats(speciality: Speciality) = newSuspendedTransaction(dispatcher) {
        val s = Specialities.alias("s")
        val lf = LearningFormats.alias("lf")

        SpecialitiesFormats
            .innerJoin(s, { SpecialitiesFormats.speciality }, { s[Specialities.id] })
            .innerJoin(lf, { SpecialitiesFormats.learningFormat }, { lf[LearningFormats.id] })
            .select { s[Specialities.code] eq speciality.code }
            .map {
                LearningFormat(
                    id = LearningFormatIdentifier(it[lf[LearningFormats.id]]),
                    name = it[lf[LearningFormats.name]]
                )
            }
    }

    override suspend fun getFormat(id: LearningFormatIdentifier) = newSuspendedTransaction(dispatcher) {
        LearningFormats.select { LearningFormats.id eq id.value }
            .map { it.toLearningFormat() }
            .first()
    }

    override suspend fun createFormat(format: LearningFormat): Unit = newSuspendedTransaction(dispatcher) {
        LearningFormats.insert {
            it[id] = format.id.value
            it[name] = format.name
        }
    }

    override suspend fun editFormat(
        id: LearningFormatIdentifier, newName: String
    ): Unit = newSuspendedTransaction(dispatcher) {
        LearningFormats.update(
            where = { LearningFormats.id eq id.value }
        ) {
            it[name] = newName
        }
    }

    override suspend fun deleteFormat(id: LearningFormatIdentifier): Unit = newSuspendedTransaction(dispatcher) {
        LearningFormats.deleteWhere { LearningFormats.id eq id.value }
    }
}
