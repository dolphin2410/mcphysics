plugins {
    kotlin("jvm") version "1.7.10"
}

group = "me.dolphin2410"
version = "0.1.0"

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {

}

tasks {
    jar {
        archiveFileName.set("mcphysics.jar")
    }
}