package com.fengsheng.bot

import com.fengsheng.bot.storage.FengshengConfig
import com.fengsheng.bot.storage.MuteCache
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.events.MemberMuteEvent
import net.mamoe.mirai.event.events.MemberUnmuteEvent

object MuteHandler {
    fun handleOnline(e: BotOnlineEvent) {
        val now = System.currentTimeMillis()
        val bot = e.bot
        for (groupId in FengshengConfig.qq.qqGroup) {
            val group = bot.getGroup(groupId) ?: continue
            group.members.forEach {
                if (it.isMuted)
                    MuteCache.addMuteData(groupId, it.id, now + it.muteTimeRemaining * 1000)
            }
        }
    }

    fun handleMute(e: MemberMuteEvent) {
        val now = System.currentTimeMillis()
        MuteCache.addMuteData(e.groupId, e.member.id, now + e.durationSeconds * 1000)
    }

    fun handleUnmute(e: MemberUnmuteEvent) {
        MuteCache.removeMuteData(e.groupId, e.member.id)
    }

    suspend fun handleJoinGroup(e: MemberJoinEvent) {
        val groupData = MuteCache.data[e.groupId] ?: return
        val muteEndTimestamp = groupData[e.member.id] ?: return
        val muteMinutesRemaining = (muteEndTimestamp - System.currentTimeMillis()) / 1000 / 60
        if (muteMinutesRemaining <= 0) {
            MuteCache.removeMuteData(e.groupId, e.member.id)
            return
        }
        val day = muteMinutesRemaining / (24 * 60)
        val hour = muteMinutesRemaining % (24 * 60) / 60
        val minute = muteMinutesRemaining % 60
        val time = StringBuilder()
        if (day > 0) time.append(day).append("天")
        if (hour > 0) time.append(hour).append("小时")
        if (minute > 0) time.append(minute).append("分")
        val name = "${e.member.nameCard}(${e.member.id})"
        e.group.sendMessage("请注意，${name}曾在禁言过程中退群，目前剩余禁言时间$time")
    }
}