package com.fengsheng.bot.utils

import com.fengsheng.bot.storage.FengshengConfig
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.net.URLEncoder
import java.time.Duration

object HttpUtil {
    fun rankList(): String {
        val result = get("${FengshengConfig.fengshengUrl}/ranklist")
        val jsonObject = result.jsonObject
        jsonObject["error"]?.let { throw Exception(it.jsonPrimitive.content) }
        return jsonObject["result"]!!.jsonPrimitive.content
    }

    fun getScore(name: String): String {
        val result = get("${FengshengConfig.fengshengUrl}/getscore?name=${URLEncoder.encode(name, Charsets.UTF_8)}")
        val jsonObject = result.jsonObject
        jsonObject["error"]?.let { throw Exception(it.jsonPrimitive.content) }
        return jsonObject["result"]!!.jsonPrimitive.content
    }

    fun resetPwd(name: String): String {
        val result = get("${FengshengConfig.fengshengUrl}/resetpwd?name=${URLEncoder.encode(name, Charsets.UTF_8)}")
        val jsonObject = result.jsonObject
        jsonObject["error"]?.let { throw Exception(it.jsonPrimitive.content) }
        return jsonObject["result"]!!.jsonPrimitive.content
    }

    fun forbidRole(name: String): Boolean {
        val result = get("${FengshengConfig.fengshengUrl}/forbidrole?name=${URLEncoder.encode(name, Charsets.UTF_8)}")
        val jsonObject = result.jsonObject
        jsonObject["error"]?.let { throw Exception(it.jsonPrimitive.content) }
        return jsonObject["result"]!!.jsonPrimitive.boolean
    }

    fun releaseRole(name: String): Boolean {
        val result = get("${FengshengConfig.fengshengUrl}/releaserole?name=${URLEncoder.encode(name, Charsets.UTF_8)}")
        val jsonObject = result.jsonObject
        jsonObject["error"]?.let { throw Exception(it.jsonPrimitive.content) }
        return jsonObject["result"]!!.jsonPrimitive.boolean
    }

    fun setVersion(version: Int) {
        val result = get("${FengshengConfig.fengshengUrl}/setversion?version=$version")
        val jsonObject = result.jsonObject
        jsonObject["error"]?.let { throw Exception(it.jsonPrimitive.content) }
    }

    fun setNotice(notice: String) {
        val result =
            get("${FengshengConfig.fengshengUrl}/setnotice?notice=${URLEncoder.encode(notice, Charsets.UTF_8)}")
        val jsonObject = result.jsonObject
        jsonObject["error"]?.let { throw Exception(it.jsonPrimitive.content) }
    }

    fun register(name: String): Boolean {
        val result = get("${FengshengConfig.fengshengUrl}/register?name=${URLEncoder.encode(name, Charsets.UTF_8)}")
        val jsonObject = result.jsonObject
        jsonObject["error"]?.let { throw Exception(it.jsonPrimitive.content) }
        return jsonObject["result"]!!.jsonPrimitive.boolean
    }

    fun getPic(url: String): InputStream {
        val request = Request.Builder().url(url)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("user-agent", ua)
            .get().build()
        val resp = client.newCall(request).execute()
        if (resp.code != 200)
            throw Exception("请求错误，错误码：${resp.code}，返回内容：${resp.message}")
        return resp.body!!.byteStream()
    }

    private const val ua =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36 Edg/97.0.1072.69"
    private val client =
        OkHttpClient().newBuilder().followRedirects(false).connectTimeout(Duration.ofMillis(20000)).build()
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        allowStructuredMapKeys = true
    }

    private fun sendRequest(request: Request): JsonElement {
        val resp = client.newCall(request).execute()
        if (resp.code != 200) {
            resp.close()
            throw Exception("请求错误，错误码：${resp.code}，返回内容：${resp.message}")
        }
        val body = resp.body!!.string()
        return json.parseToJsonElement(body)
    }

    private fun get(url: String): JsonElement {
        val request = Request.Builder().url(url)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("user-agent", ua)
            .get().build()
        return sendRequest(request)
    }
}