plugins {
    kotlin("jvm") version "2.3.0"
    application
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

    implementation("io.ktor:ktor-client-core:latest.release")
    implementation("io.ktor:ktor-client-cio:latest.release")

    implementation("io.github.classgraph:classgraph:latest.release")
    implementation(kotlin("reflect"))

    implementation("io.github.oshai:kotlin-logging-jvm:latest.release")
    implementation("io.github.cdimascio:dotenv-kotlin:latest.release")
    implementation("dev.kord:kord-core:latest.release")

    implementation("org.jetbrains.exposed:exposed-core:latest.release")
    implementation("org.jetbrains.exposed:exposed-dao:latest.release")
    implementation("org.jetbrains.exposed:exposed-jdbc:latest.release")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:latest.release")
    implementation("org.mariadb.jdbc:mariadb-java-client:latest.release")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.klosradieschen.Main")
}

tasks.test {
    useJUnitPlatform()
}
