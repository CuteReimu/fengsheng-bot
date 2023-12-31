package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource

object RankList : CommandHandler {
    override val name = "排行"

    override fun showTips(groupCode: Long, senderId: Long) = "排行"

    override fun checkAuth(groupCode: Long, senderId: Long) = true

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message? {
        content.isBlank() || return null
        return HttpUtil.rankList().use { `is` ->
            `is`.toExternalResource().use { msg.group.uploadImage(it) }
        }
    }
}