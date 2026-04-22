plugins {
    kotlin("jvm") version "2.3.0"
}

group = "com.klosradieschen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://snapshots.kord.dev")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("ch.qos.logback:logback-classic:latest.release")
    implementation("io.github.oshai:kotlin-logging-jvm:latest.release")
    implementation("io.github.cdimascio:dotenv-kotlin:latest.release")
    implementation("dev.kord:kord-core:0.18.1")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}