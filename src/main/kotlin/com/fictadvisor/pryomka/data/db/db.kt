package com.fictadvisor.pryomka.data.db

import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.domain.models.User
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 512)
    val role = enumeration("role", User.Role::class)

    override val primaryKey = PrimaryKey(id)
}

object Documents : Table() {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id") references Users.id
    val path = varchar("path", 512)
    val type = enumeration("type", DocumentType::class)

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(userId, type)
    }
}
