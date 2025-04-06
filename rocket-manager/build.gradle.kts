val ktor_version = "2.3.7"
val kotlin_version = "1.9.22"
val logback_version = "1.4.12"
val serialization_version = "1.6.0"

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("io.ktor.plugin") version "2.3.7"
    application
}

group = "com"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.hasProperty("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor core
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")

    // Serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Tests
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
}
