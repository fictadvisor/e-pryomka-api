package com.fictadvisor.pryomka.plugins

import com.fictadvisor.pryomka.data.db.Documents
import com.fictadvisor.pryomka.data.db.Users
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.domain.models.User
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun initDB() {
    val config = HikariConfig("dbconfig.properties")
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)
    createTables()
}

fun createTables() = transaction {
    SchemaUtils.setSchema(Schema("public"))
    SchemaUtils.create(Users, Documents)
}
