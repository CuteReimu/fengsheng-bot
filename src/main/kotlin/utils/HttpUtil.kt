package com.fengsheng.bot.utils

import com.fengsheng.bot.storage.FengshengConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Duration

object HttpUtil {
    fun rankList(): String {
        val result = get("${FengshengConfig.fengshengUrl}/ranklist")
        val jsonObject = result.jsonObject
        jsonObject["error"]?.let { throw Exception(it.jsonPrimitive.content) }
        return jsonObject["result"]!!.jsonPrimitive.content
    }

    fun getScore(name: String): String {
        val result = get("${FengshengConfig.fengshengUrl}/getscore?name=$name")
        val jsonObject = result.jsonObject
        jsonObject["error"]?.let { throw Exception(it.jsonPrimitive.content) }
        return jsonObject["result"]!!.jsonPrimitive.content
    }

    fun resetPwd(name: String) {
        val result = get("${FengshengConfig.fengshengUrl}/resetpwd?name=$name")
        val jsonObject = result.jsonObject
        jsonObject["error"]?.let { throw Exception(it.jsonPrimitive.content) }
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