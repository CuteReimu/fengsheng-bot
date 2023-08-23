package com.fengsheng.bot.storage

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object MuteCache : AutoSavePluginData("MuteCache") {
    @ValueDescription("在哪个群被禁言结束时间的列表")
    var data: Map<Long, Map<Long, Long>> by value(mapOf())

    fun addMuteData(group: Long, qq: Long, endTimestamp: Long) {
        synchronized(MuteCache) {
            val mutableData = data.toMutableMap()
            mutableData.compute(group) { _, v ->
                if (v == null) mapOf(qq to endTimestamp)
                else v + (qq to endTimestamp)
            }
            data = mutableData
        }
    }

    fun removeMuteData(group: Long, qq: Long) {
        synchronized(MuteCache) {
            val mutableData = data.toMutableMap()
            mutableData.computeIfPresent(group) { _, v ->
                (v - qq).ifEmpty { null }
            }
            data = mutableData
        }
    }

    fun clearExpiredData() {
        val now = System.currentTimeMillis()
        synchronized(MuteCache) {
            data = data.mapNotNull { (k1, v1) ->
                k1 to v1.filter { (_, v2) -> now < v2 }.ifEmpty { return@mapNotNull null }
            }.toMap()
        }
    }
}