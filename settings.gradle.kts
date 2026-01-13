enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    includeBuild("build-logic")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://devrepo.kakao.com/nexus/content/groups/public/") }
        maven("https://repository.map.naver.com/archive/maven")
    }
}

rootProject.name = "Neki"
include(":app")
include(":core:common")
include(":core:designsystem")
include(":core:domain")
include(":core:data")
include(":core:data-api")
include(":core:model")
include(":core:navigation")
include(":feature:auth:api")
include(":feature:auth:impl")
include(":feature:pose:api")
include(":feature:pose:impl")
include(":feature:archive:api")
include(":feature:archive:impl")
include(":feature:map:api")
include(":feature:map:impl")
include(":feature:photo-upload:api")
include(":feature:photo-upload:impl")
include(":core:ui")
