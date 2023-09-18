package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object AddDict : CommandHandler {
    override val name = "添加词条"

    override fun showTips(groupCode: Long, senderId: Long) = "添加词条 词条名称"

    override fun checkAuth(groupCode: Long, senderId: Long) = senderId in PermData.dictModify

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message? {
        val s = content.trim()
        if (s.isEmpty()) return PlainText("命令格式：\n添加词条 词条名称")
        return null
    }
}