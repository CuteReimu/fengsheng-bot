package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object ReleasePlayer : CommandHandler {
    override val name = "解封"

    override fun showTips(groupCode: Long, senderId: Long) = "解封 名字"

    override fun checkAuth(groupCode: Long, senderId: Long) = PermData.isAdmin(senderId)

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message {
        val name = content.trim()
        if (name.isEmpty()) return PlainText("命令格式：\n解封 名字")
        return PlainText(HttpUtil.releasePlayer(name))
    }
}