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
    testImplementation("io.mockk:mockk:1.13.10")

    implementation("ch.qos.logback:logback-classic:latest.release")

    implementation("com.github.haifengl:smile-kotlin:3.0.2")
    implementation("com.github.haifengl:smile-core:3.0.2")
    implementation("org.bytedeco:openblas-platform:0.3.26-1.5.10")

    implementation("com.openai:openai-java:latest.release")

    implementation("io.github.classgraph:classgraph:latest.release")
    implementation(kotlin("reflect"))

    implementation("io.github.oshai:kotlin-logging-jvm:latest.release")
    implementation("io.github.cdimascio:dotenv-kotlin:latest.release")
    implementation("dev.kord:kord-core:latest.release")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
