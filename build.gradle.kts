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
        implementation("org.projectlombok:lombok:1.18.36")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")  // JUnit 5 API
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.2") // JUnit 5 Engine
        testImplementation("org.mockito:mockito-core:3.11.2")  // Mockito 用于模拟对象
        testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.0") // Kotlin Test
    }

    // 配置 Kotlin 编译选项
    kotlin {
        jvmToolchain(11)  // 使用 JDK 11 编译
    }
}