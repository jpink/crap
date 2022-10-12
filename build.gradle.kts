plugins {
    kotlin("multiplatform")
    `maven-publish`
}

group = "fi.papinkivi"
version = "2.1.0"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.microutils:kotlin-logging:_")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Testing.kotest.runner.junit5)
                runtimeOnly("ch.qos.logback:logback-classic:_")
                runtimeOnly("org.codehaus.janino:janino:_")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.fazecast:jSerialComm:_")
                runtimeOnly("ch.qos.logback:logback-classic:_")
                runtimeOnly("org.codehaus.janino:janino:_")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(Testing.kotest.runner.junit5)
                runtimeOnly("ch.qos.logback:logback-classic:_")
                runtimeOnly("org.codehaus.janino:janino:_")
            }
        }
    }
}
