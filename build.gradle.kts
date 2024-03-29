import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.71"
    kotlin("plugin.spring") version "1.3.71"
    kotlin("plugin.jpa") version "1.3.71"
    kotlin("kapt") version "1.3.72"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    jacoco
    id("com.github.dawnwords.jacoco.badge") version "0.2.0"
}

group = "cz.pedro"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_11

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "Hoxton.SR4"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:2.2.2.RELEASE")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-ribbon:2.2.2.RELEASE")
    implementation("org.springframework.cloud:spring-cloud-starter-consul-config:2.2.2.RELEASE")
    implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery:2.2.2.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("com.auth0:java-jwt:3.19.1")
    implementation("org.liquibase:liquibase-core")
    implementation("com.h2database:h2:2.1.210")
    implementation("io.github.microutils:kotlin-logging:2.1.21")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("com.h2database:h2")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.3")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

ktlint {
    version.set("0.34.2")
    verbose.set(true)
    android.set(false)
    disabledRules.set(setOf("comment-spacing", "import-ordering", "parameter-list-wrapping"))
}

jacoco {
    toolVersion = "0.8.5"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.isEnabled = true
        csv.isEnabled = true
    }
}

tasks.generateJacocoBadge {
    dependsOn(tasks.jacocoTestReport)
    jacocoBadgeGenSetting {
        jacocoReportPath = "$buildDir/reports/jacoco/test/jacocoTestReport.xml"
        readmePath = "$projectDir/README.md"
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.65".toBigDecimal()
            }
        }
    }
}
