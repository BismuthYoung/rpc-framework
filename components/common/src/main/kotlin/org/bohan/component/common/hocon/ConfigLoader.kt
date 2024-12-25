package org.bohan.component.common.hocon

import com.typesafe.config.ConfigFactory
import org.bohan.component.common.hocon.annotation.Config
import rpc_framework.component_common.BuildConfig
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

object ConfigLoader {

    private val config = ConfigFactory.load()

    fun <T:Any> loadConfig(clazz: Class<T>): T {
        val annotation = clazz.getAnnotation(Config::class.java)
            ?: throw IllegalArgumentException("${clazz.simpleName} 必须包含 @Config 注解")

        val configPath = if (annotation.path.isNotBlank()) {
            "${BuildConfig.PROJECT_NAME}.${annotation.path}"
        } else {
            BuildConfig.PROJECT_NAME
        }

        val subConfig = config.getConfig(configPath)
        val instance = clazz.kotlin.createInstance()

        clazz.kotlin.declaredMemberProperties.forEach { property ->
            if (property.name == "config") {
                property.javaField?.apply {
                    isAccessible = true
                    set(instance, subConfig)
                }
            }
        }

        return instance
    }

}