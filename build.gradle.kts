val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
}

group = "com.fictadvisor.pryomka"
version = "0.0.1"
application {
    mainClass.set("com.fictadvisor.pryomka.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("org.postgresql:postgresql:42.3.1")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.mockito:mockito-core:4.1.0")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}