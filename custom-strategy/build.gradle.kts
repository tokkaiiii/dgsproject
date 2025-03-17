plugins {
    kotlin("jvm")
}

group = "com.sahara"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

val jooqVersion: String by rootProject.extra

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jooq:jooq-codegen:$jooqVersion")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}