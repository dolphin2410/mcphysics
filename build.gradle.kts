plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.dokka") version "1.9.20" apply false
    `maven-publish`
    signing
}

group = "io.github.dolphin2410"
version = "0.2.0"

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

project("core") {
    apply(plugin = "org.jetbrains.dokka")
    tasks {
        create<Jar>("sourcesJar") {
            archiveClassifier.set("sources")
            from(sourceSets["main"].allSource)
        }

        create<Jar>("dokkaJar") {
            archiveClassifier.set("javadoc")
            dependsOn("dokkaHtml")

            from("$buildDir/dokka/html/") {
                include("**")
            }
        }
    }
}

project("extensions:tap") {
    apply(plugin = "org.jetbrains.dokka")
    tasks {
        create<Jar>("sourcesJar") {
            archiveClassifier.set("sources")
            from(sourceSets["main"].allSource)
        }

        create<Jar>("dokkaJar") {
            archiveClassifier.set("javadoc")
            dependsOn("dokkaHtml")

            from("$buildDir/dokka/html/") {
                include("**")
            }
        }
    }
}

// 참고: https://github.com/monun/paper-sample-complex/blob/master/sample-publish/build.gradle.kts
publishing {
    repositories {
        mavenLocal()

        maven {
            name = "server"
            url = rootProject.uri("../debug/libraries")
        }

        maven {
            name = "central"

            credentials.runCatching {
                val nexusUsername: String by project
                val nexusPassword: String by project
                username = nexusUsername
                password = nexusPassword
            }.onFailure {
                logger.warn("Failed to load nexus credentials, Check the gradle.properties")
            }

            url = uri(
                if ("SNAPSHOT" in version as String) {
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                } else {
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                }
            )
        }
    }

    publications {
        fun MavenPublication.setup(target: Project, publishName: String = target.name) {
            artifactId = publishName
            from(target.components["java"])
            artifact(target.tasks["sourcesJar"])
            artifact(target.tasks["dokkaJar"])

            pom {
                name.set(publishName)
                description.set("Physics for Minecraft")
                url.set("https://github.com/dolphin2410/mcphysics")

                licenses {
                    license {
                        name.set("GNU General Public License version 3")
                        url.set("https://opensource.org/licenses/GPL-3.0")
                    }
                }

                developers {
                    developer {
                        id.set("dolphin2410")
                        name.set("dolphin2410")
                        email.set("dolphin2410@outlook.kr")
                        url.set("https://github.com/dolphin2410")
                        roles.addAll("developer")
                        timezone.set("Asia/Seoul")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/dolphin2410/mcphysics.git")
                    developerConnection.set("scm:git:ssh://github.com:dolphin2410/mcphysics.git")
                    url.set("https://github.com/dolphin2410/mcphysics")
                }
            }
        }

        create<MavenPublication>("core") {
            setup(project(":core"), publishName = "mcphysics-core")
        }

        create<MavenPublication>("tap_extension") {
             setup(project(":extensions:tap"), publishName = "mcphysics-tap")
        }

    }
}

//signing {
//    isRequired = true
//    sign(publishing.publications["core"], publishing.publications["tap_extension"])
//}