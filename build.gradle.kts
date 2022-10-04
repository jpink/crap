plugins {
    kotlin("multiplatform")
}

group = "fi.papinkivi"
version = "0.1.0-SNAPSHOT"

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
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(Testing.kotest.runner.junit5)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.fazecast:jSerialComm:_")
            }
        }
        val jvmTest by getting
    }
}
