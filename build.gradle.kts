plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.0"
    id("jacoco")
}

jacoco {
    toolVersion = "0.8.8" // 使用适当的 Jacoco 版本
}

// jacoco 任务配置
tasks.test {
    useJUnitPlatform() // 如果你使用 JUnit 5 测试框架
    finalizedBy("jacocoTestReport") // 确保在测试之后执行 Jacoco 报告生成
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // 确保在测试后生成报告
    reports {
        // 启用 XML 和 HTML 报告
        xml.required.set(true)  // 使用 required() 来启用 xml 报告
        html.required.set(true) // 使用 required() 来启用 html 报告
    }
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

    val slf4jApiVersion = "1.7.25"
    val logbackVersion = "1.2.3"

    dependencies {
        implementation(kotlin("stdlib"))
        // 其他公共依赖可以放在这里
        implementation("org.projectlombok:lombok:1.18.36")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
        implementation("ch.qos.logback:logback-core:$logbackVersion")
        implementation("org.slf4j:slf4j-api:$slf4jApiVersion")
        implementation("com.typesafe:config:1.4.2")

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