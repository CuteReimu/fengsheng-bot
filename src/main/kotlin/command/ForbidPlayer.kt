package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object ForbidPlayer : CommandHandler {
    override val name = "封号"

    override fun showTips(groupCode: Long, senderId: Long) = "封号 名字 小时"

    override fun checkAuth(groupCode: Long, senderId: Long) = PermData.isAdmin(senderId)

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message {
        val c = content.trim()
        if (c.isEmpty()) return PlainText("命令格式：\n封号 名字 小时")
        val spaceIndex = c.indexOfLast { it == ' ' }
        if (spaceIndex < 0) return PlainText("命令格式：\n封号 名字 小时")
        val name = c.substring(0, spaceIndex).trim()
        val hours = runCatching {
            c.substring(spaceIndex + 1).trim().toInt()
        }.getOrElse { return PlainText("命令格式：\n封号 名字 小时") }
        return PlainText(HttpUtil.forbidPlayer(name, hours))
    }
}