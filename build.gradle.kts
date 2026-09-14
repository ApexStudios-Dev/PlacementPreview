plugins {
    id("apex-conventions.neoforge")
    id("apex-conventions.maven-publishing")
    id("apex-conventions.jspecify")
}

group = "dev.apexstudios"
neoForge.version = libs.versions.neoforge.get()

repositories {
    mavenLocal()
    maven("https://prmaven.neoforged.net/NeoForge/pr3403") {
        content {
            includeModule("net.neoforged", "neoforge")
            includeModule("net.neoforged", "testframework")
        }
    }

    maven("https://maven.apexmodder.com/prs/GhostRenderer/pr2") {
        content {
            includeModule("dev.apexstudios", "ghostrenderer")
        }
    }
}

dependencies {
    implementation(libs.ghostrenderer)
    accessTransformers(libs.ghostrenderer)

    // WST does not include this by default
    implementation("io.github.llamalad7:mixinextras-neoforge:0.5.4")
}