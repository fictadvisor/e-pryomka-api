package com.fictadvisor.pryomka.plugins

import com.fictadvisor.pryomka.Environment
import com.fictadvisor.pryomka.data.db.Documents
import com.fictadvisor.pryomka.data.db.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun initDB() {
    val properties = Properties().apply {
        setProperty("dataSourceClassName", Environment.DB_SOURCE_CLASS_NAME)
        setProperty("dataSource.serverName", Environment.DB_HOST)
        setProperty("dataSource.portNumber", Environment.DB_PORT)
        setProperty("dataSource.databaseName", Environment.DB_NAME)
        setProperty("dataSource.user", Environment.DB_USER)
        setProperty("dataSource.password", Environment.DB_PASSWORD)
    }
    val config = HikariConfig(properties)
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)
    createTables()
}

fun createTables() = transaction {
    SchemaUtils.setSchema(Schema(Environment.DB_SCHEMA))
    SchemaUtils.create(Users, Documents)
}
