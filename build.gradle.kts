val kotlinVersion: String by project
val ktorVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project
val koinVersion: String by project
val kotlinDateTimeVersion: String by project
val hikariCpVersion: String by project
val postgresVersion: String by project
val junitVersion: String by project
val mockitoVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
    jacoco
}

group = "com.fictadvisor.pryomka"
version = "0.0.1"
application {
    mainClass.set("com.fictadvisor.pryomka.ApplicationKt")
}

repositories {
    mavenCentral()
}

tasks.test {
    extensions.configure(JacocoTaskExtension::class) {
        setDestinationFile(file("$buildDir/jacoco/jacoco.exec"))
    }

    finalizedBy("jacocoTestReport")
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
    reports {
        html.isEnabled = false
        xml.isEnabled = true
        csv.isEnabled = false
    }
}

val testCoverage by tasks.registering {
    group = "verification"
    description = "Runs the unit tests with coverage"

    dependsOn(":test", ":jacocoTestReport")

    tasks["jacocoTestReport"].mustRunAfter(tasks["test"])
}

dependencies {
    // Database
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("com.zaxxer:HikariCP:$hikariCpVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")

    // Ktor
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")

    // DI
    implementation("io.insert-koin:koin-ktor:$koinVersion")

    // Utils
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinDateTimeVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.insert-koin:koin-test:$koinVersion")
    testImplementation("io.insert-koin:koin-test-junit4:$koinVersion")

    // Other modules
    implementation(project(":rusnia"))
}