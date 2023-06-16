package com.github.jamsiedaly.autometricsplugin

import com.intellij.DynamicBundle
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformDataKeys
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@NonNls
private const val BUNDLE = "messages.MyBundle"

const val BUILD_INFO = "* on (instance, job) group_left(version, commit) last_over_time(build_info[1s])"
const val PROMETHEUS_URL = "http://localhost:9090"

object AutometricsPlugin : DynamicBundle(BUNDLE) {

    @Suppress("SpreadOperator")
    @JvmStatic
    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getMessage(key, *params)

    @Suppress("SpreadOperator", "unused")
    @JvmStatic
    fun messagePointer(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getLazyMessage(key, *params)


    fun getPackage(context: DataContext): String {
        val fileContent = context.getData(PlatformDataKeys.FILE_TEXT)
        val packageName = fileContent!!.substringAfter("package ").substringBefore("\n").removeSuffix(";")
        return packageName
    }

    fun makePrometheusUrl(url: String, query: String?, comment: String?): String {
        val stringBuilder = StringBuilder(url)
        if (!url.endsWith("/")) {
            stringBuilder.append('/')
        }
        stringBuilder.append("graph?g0.expr=")
        val commentAndQuery = String.format("# %s\n\n%s", comment, query)
        val encodedQuery: String = URLEncoder.encode(commentAndQuery, StandardCharsets.UTF_8)
        stringBuilder.append(encodedQuery)

        // Go straight to the graph tab
        stringBuilder.append("&g0.tab=0")
        return stringBuilder.toString()
    }
}
