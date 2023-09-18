package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object SearchDict : CommandHandler {
    override val name = "搜索词条"

    override fun showTips(groupCode: Long, senderId: Long) = "搜索词条 关键词"

    override fun checkAuth(groupCode: Long, senderId: Long) = true

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message? {
        val s = content.trim()
        if (s.isEmpty()) return PlainText("命令格式：\n搜索词条 关键词")
        return null
    }
}