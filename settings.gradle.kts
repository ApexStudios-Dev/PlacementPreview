pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.apexmodder.com/releases")
    }

    if(file("../../ApexGradle").exists()) {
        includeBuild("../../ApexGradle")
    } else {
        resolutionStrategy {
            eachPlugin {
                if(requested.id.namespace == "apex-conventions") {
                    useVersion("0.1.94")
                }
            }
        }
    }
}

dependencyResolutionManagement {
    versionCatalogs.create("libs") {
        version("neoforge", "26.3.0-alpha.0+rc-3.20260914.185244")

        library("ghostrenderer", "dev.apexstudios", "ghostrenderer").version("26.3.2-beta-pr-2")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "PlacementPreview"
