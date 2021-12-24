package com.fictadvisor.pryomka.data.db

import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.domain.models.User
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 64)
    val role = enumeration("role", User.Role::class)

    override val primaryKey = PrimaryKey(id)
}

object Applications : Table() {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id") references Users.id
    val status = enumeration("status", Application.Status::class)
    val statusMsg = varchar("status_msg", 256)

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
