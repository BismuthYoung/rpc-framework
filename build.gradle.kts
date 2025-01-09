plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.0"
}


tasks.test {
    useJUnitPlatform() // 如果你使用 JUnit 5 测试框架
}


allprojects {
    repositories {
        mavenCentral()
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
        implementation("io.netty:netty-all:4.1.51.Final")
        implementation("org.apache.curator:apache-curator:5.7.1")
        implementation("org.apache.curator:curator-framework:5.7.1")
        implementation("org.apache.curator:curator-recipes:5.7.1")
        implementation("com.alibaba:fastjson:2.0.53")
        implementation("com.google.guava:guava:31.1-jre")
        implementation("com.github.rholder:guava-retrying:2.0.0")


        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")  // JUnit 5 API
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2") // JUnit 5 Engine
        testImplementation("org.mockito:mockito-core:3.11.2")  // Mockito 用于模拟对象
    }

    // 配置 Kotlin 编译选项
    kotlin {
        jvmToolchain(11)  // 使用 JDK 11 编译
    }
}