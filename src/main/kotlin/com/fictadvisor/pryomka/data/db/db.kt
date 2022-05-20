package com.fictadvisor.pryomka.data.db

import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.domain.models.TokenMetadata
import com.fictadvisor.pryomka.domain.models.User
import kotlinx.coroutines.CoroutineDispatcher
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object Entrants : Table() {
    val id = uuid("id").autoGenerate()
    val telegramId = long("telegram_id")
    val firstName = varchar("first_name", 64)
    val lastName = varchar("last_name", 64).nullable()
    val userName = varchar("user_name", 32).nullable()
    val photoUrl = varchar("photo_url", 128).nullable()

    override val primaryKey = PrimaryKey(id)
}

object Staff : Table() {
    val id = uuid("id").autoGenerate()
    val login = varchar("login", 32)
    val password = varchar("password", 128)
    val salt = varchar("salt", 32)
    val role = enumeration("role", User.Staff.Role::class)

    override val primaryKey = PrimaryKey(id)
}

object Applications : Table() {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id") references Entrants.id
    val speciality = uuid("speciality_id").references(Specialities.id, onDelete = ReferenceOption.CASCADE)
    val funding = enumeration("funding", Application.Funding::class)
    val learningFormat = uuid("learning_format_id").references(LearningFormats.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = timestamp("creation_time")
    val status = enumeration("status", Application.Status::class)
    val statusMessage = varchar("status_message", 256).nullable().default(null)

    override val primaryKey = PrimaryKey(id)
}

object Documents : Table() {
    val id = uuid("id").autoGenerate()
    val applicationId = uuid("application_id") references Applications.id
    val path = varchar("path", 512) // replace with file name, path is redundant
    val type = enumeration("type", DocumentType::class)
    val key = varchar("key", 128)

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(applicationId, type)
    }
}

object Reviews : Table() {
    val id = uuid("id").autoGenerate()
    val applicationId = uuid("application_id").uniqueIndex() references Applications.id
    val operatorId = uuid("operator_id") references Staff.id

    override val primaryKey = PrimaryKey(id)
}

object Tokens : Table() {
    val id = integer("id").autoIncrement()
    val token = varchar("token", 128).uniqueIndex()
    val salt = varchar("salt",  32)
    val userId = uuid("user_id")
    val validUntil = timestamp("valid_until")
    val type = enumeration("type", TokenMetadata.Type::class)
    val pairedToken = integer("paired_token")
        .references(Tokens.id, onDelete = ReferenceOption.CASCADE)
        .nullable()

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

object LearningFormats : Table("learning_formats") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name",  64).uniqueIndex()

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

object Specialities : Table() {
    val id = uuid("id").autoGenerate().uniqueIndex()
    val code = integer("code").uniqueIndex()
    val name = varchar("name",  256)

    override val primaryKey: PrimaryKey = PrimaryKey(LearningFormats.id)
}

object SpecialitiesFormats : Table("specialities_formats") {
    val id = integer("id").autoIncrement()
    val speciality = uuid("speciality_id").references(Specialities.id, onDelete = ReferenceOption.CASCADE)
    val learningFormat = uuid("learning_format_id").references(LearningFormats.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey: PrimaryKey = PrimaryKey(id)

    init {
        uniqueIndex(speciality, learningFormat)
    }
}

suspend fun <T> db(
    context: CoroutineDispatcher? = null,
    db: Database? = null,
    transactionIsolation: Int? = null,
    statement: suspend Transaction.() -> T
): T = newSuspendedTransaction(context, db, transactionIsolation, statement)
