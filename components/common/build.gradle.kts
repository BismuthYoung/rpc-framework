description = "技术组件 - 公共技术组件"

plugins {
    id("com.github.gmazzo.buildconfig") version "3.0.3"
}

buildConfig {
    buildConfigField("String", "PROJECT_NAME", "\"${rootProject.name}\"")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}