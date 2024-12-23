plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.0"
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}

subprojects {
    // 为每个子项目应用 Kotlin 插件
    plugins.apply("kotlin")

    dependencies {
        implementation(kotlin("stdlib"))
        // 其他公共依赖可以放在这里
    }

    // 配置 Kotlin 编译选项
    kotlin {
        jvmToolchain(11)  // 使用 JDK 11 编译
    }
}