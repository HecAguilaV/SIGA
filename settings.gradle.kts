plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "siga"

include("services:common")
include("services:auth")
include("services:inventory")
include("services:sales")
include("services:billing")
include("services:gateway")
include("services:registry")
include("services:agent")
include("services:notification")
