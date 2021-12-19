package com.fictadvisor.pryomka

object Environment {
    private val env = System.getenv()

    // Encryption
    val SECRET get() = env["SECRET"] ?: error("SECRET is not set")

    // DB
    val DB_SOURCE_CLASS_NAME get() = env["DB_SOURCE_CLASS_NAME"] ?: "org.postgresql.ds.PGSimpleDataSource"
    val DB_HOST get() = env["DB_HOST"] ?: "localhost"
    val DB_PORT get() = env["DB_PORT"] ?: "5432"
    val DB_USER get() = env["DB_USER"] ?: error("DB_USER not set")
    val DB_PASSWORD get() = env["DB_PASSWORD"] ?: error("DB_PASSWORD not set")
    val DB_NAME get() = env["DB_NAME"] ?: error("DB_NAME not set")
    val DB_SCHEMA get() = env["DB_SCHEMA"] ?: "public"

    // Server
    val PORT get() = env["PORT"]?.toInt() ?: 8080
    val HOST get() = env["HOST"] ?: "0.0.0.0"
}
