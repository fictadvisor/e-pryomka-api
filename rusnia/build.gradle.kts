plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    jacoco
}

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project
val kotlinDateTimeVersion: String by project
val skrapeItVersion = "1.1.5"

group = "com.alegator1209.rusnia"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinDateTimeVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("it.skrape:skrapeit:$skrapeItVersion")
}
