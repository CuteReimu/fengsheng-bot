package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object ForceEnd : CommandHandler {
    override val name = "强制结束所有游戏"

    override fun showTips(groupCode: Long, senderId: Long) = "强制结束所有游戏"

    override fun checkAuth(groupCode: Long, senderId: Long) = PermData.isAdmin(senderId)

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message? {
        content.isBlank() || return null
        HttpUtil.forceEnd()
        return PlainText("已执行")
    }
}