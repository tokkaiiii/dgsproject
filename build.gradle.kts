plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"

    // Flyway DB 마이그레이션
    id("org.flywaydb.flyway") version "10.8.1"
    id("nu.studer.jooq") version "9.0"
}

group = "com.sahara"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["netflixDgsVersion"] = "10.0.4"

val jooqVersion: String by extra("3.19.19")
val dbUrl = System.getenv("JOOQ_DB_URL") ?: "jdbc:mysql://localhost:3309/dgs"
val dbUsername = System.getenv("JOOQ_DB_USERNAME") ?: "root"
val dbPassword = System.getenv("JOOQ_DB_PASSWORD") ?: "1234"
val dbDriver = System.getenv("JOOQ_DB_DRIVER") ?: "com.mysql.cj.jdbc.Driver"

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Dgs
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-graphql-starter")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")

    // Database & Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")
    implementation("com.mysql:mysql-connector-j")

    // jOOQ
    jooqGenerator("com.mysql:mysql-connector-j")
    jooqGenerator("org.jooq:jooq-meta-extensions:$jooqVersion")
    jooqGenerator(project(":custom-strategy"))

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.netflix.graphql.dgs:graphql-dgs-spring-graphql-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

flyway {
    driver = dbDriver
    url = dbUrl
    user = dbUsername
    password = dbPassword
    locations = arrayOf("classpath:db/migration")
    cleanDisabled = true
}

jooq {
    version.set(jooqVersion)
    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(true)
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = dbDriver
                    url = dbUrl
                    user = dbUsername
                    password = dbPassword
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    strategy.apply {
                        name = "com.sahara.dgsproject.PrefixGeneratorStrategy"
                    }
                    database.apply {
                        name = "org.jooq.meta.mysql.MySQLDatabase"
                        inputSchema = "dgs"
                        includes = ".*"
                        excludes = "flyway_schema_history"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isDaos = true
                        isFluentSetters = true
                        isJavaTimeTypes = true
                    }
                    target.apply {
                        packageName = "com.example.generated"
                        directory = "build/generated/jooq/main"
                    }
                }
            }
        }
    }
}

dependencyManagement {
    imports {
        mavenBom("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:${property("netflixDgsVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
