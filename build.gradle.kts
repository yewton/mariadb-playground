buildscript {
    // see https://github.com/flyway/flyway/issues/2289
    val mariaVersion by extra { "2.3.0" }

    repositories {
        jcenter()
    }

    dependencies {
        classpath("org.mariadb.jdbc:mariadb-java-client:$mariaVersion")
    }
}

plugins {
    kotlin("jvm") version "1.3.21"
    id("com.diffplug.gradle.spotless") version "3.18.0"
    id("org.flywaydb.flyway") version "5.2.4"
}

val mariaVersion: String by rootProject.extra
val spekVersion = "2.0.0"
val junitVersion = "5.4.0"

repositories {
    jcenter()
}

spotless {
    kotlin {
        ktlint()
    }
    kotlinGradle {
        ktlint()
    }
    sql {
        target("**/*.sql")
        dbeaver()
    }
}

flyway {
    url = "jdbc:mariadb://127.0.0.1:13306/?user=root&password=admin"
    user = "root"
    password = "admin"
    schemas = arrayOf("test")
}

tasks.named<Test>("test") {
    useJUnitPlatform {
        includeEngines("spek2")
    }
    testLogging {
        events("PASSED", "FAILED", "SKIPPED")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.mariadb.jdbc:mariadb-java-client:$mariaVersion")

    testImplementation(kotlin("test"))
    testRuntimeOnly(kotlin("reflect"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion") {
        exclude(group = "org.junit.platform")
        exclude(group = "org.jetbrains.kotlin")
    }

    testImplementation("org.flywaydb:flyway-core:5.2.4")
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}
