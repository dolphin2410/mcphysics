repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.github.monun:tap-api:4.8.0")
    compileOnly("io.github.monun:kommand-api:3.0.0")
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly(project(":extensions:tap"))
    compileOnly(project(":core"))
}

tasks {
    jar {
        archiveFileName.set("mcphysics.jar")
    }
}