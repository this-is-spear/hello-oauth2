pluginManagement {
    plugins {
        kotlin("jvm") version "1.9.25"
        kotlin("plugin.spring") version "1.9.25"
        kotlin("plugin.jpa") version "1.9.25"
        id("org.springframework.boot") version "3.4.3"
        id("io.spring.dependency-management") version "1.1.7"
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    }

    repositories {
        mavenCentral()
    }
}

rootProject.name = "hello-oauth"
include("oauth-client")
include("oauth-resource-server")
include("oauth-authorization-server")
