package com.fengsheng.bot.storage

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object PermData : AutoSavePluginData("PermData") {
    @ValueDescription("管理员")
    var admin: List<Long> by value(listOf())

    fun isAdmin(qq: Long) =
        FengshengConfig.isSuperAdmin(qq) || qq in admin

    fun addAdmin(qq: Long): Boolean {
        synchronized(PermData) {
            if (qq in admin) return false
            admin += qq
            return true
        }
    }

    fun removeAdmin(qq: Long): Boolean {
        synchronized(PermData) {
            if (qq !in admin) return false
            admin -= qq
            return true
        }
    }

    @ValueDescription("注册状态")
    var playerMap: Map<Long, String> by value(mapOf())

    @ValueDescription("注册状态反查")
    var reversePlayerMap: Map<String, Long> by value(mapOf())

    fun initReverseMap() {
        reversePlayerMap = playerMap.map { (k, v) -> v to k }.toMap()
    }
}