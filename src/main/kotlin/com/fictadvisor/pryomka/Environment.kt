package com.fictadvisor.pryomka

object Environment {
    private val env = System.getenv()

    // Encryption
    val SECRET get() = env["SECRET"] ?: error("SECRET is not set")

    // JWT
    val JWT_SECRET get() = env["JWT_SECRET"] ?: error("JWT_PRIVATE_KEY is not set")
    val JWT_ISSUER get() = env["JWT_ISSUER"] ?: "https://vstup.fictadvisor.com/"
    val JWT_AUDIENCE get() = env["JWT_AUDIENCE"] ?: "https://vstup.fictadvisor.com/api/"
    val JWT_REALM get() = env["JWT_REALM"] ?: "Access to api"
    val JWT_ACCESS_TOKEN_EXPIRATION_TIME
        get() = env["JWT_ACCESS_TOKEN_EXPIRATION_TIME"]?.toLong() ?: 900_000L // 15 min
    val JWT_REFRESH_TOKEN_EXPIRATION_TIME
        get() = env["JWT_REFRESH_TOKEN_EXPIRATION_TIME"]?.toLong() ?: 64_800_000L // 18 hours

    // DB
    val DB_SOURCE_CLASS_NAME get() = env["DB_SOURCE_CLASS_NAME"] ?: "org.postgresql.ds.PGSimpleDataSource"
    val DB_HOST get() = env["DB_HOST"] ?: "localhost"
    val DB_PORT get() = env["DB_PORT"] ?: "5432"
    val DB_USER get() = env["DB_USER"] ?: error("DB_USER not set")
    val DB_PASSWORD get() = env["DB_PASSWORD"] ?: error("DB_PASSWORD not set")
    val DB_NAME get() = env["DB_NAME"] ?: error("DB_NAME not set")
    val DB_SCHEMA get() = env["DB_SCHEMA"] ?: "public"
    val UPLOADS_DIR get() = env["UPLOADS_DIR"] ?: "uploads"

    // Server
    val PORT get() = env["PORT"]?.toInt() ?: 8080
    val HOST get() = env["HOST"] ?: "0.0.0.0"

    // Telegram
    val TG_BOT_ID get() = env["TG_BOT_ID"] ?: error("TG_BOT_ID not set")
}
