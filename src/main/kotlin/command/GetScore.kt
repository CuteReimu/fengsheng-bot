package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object GetScore : CommandHandler {
    override val name = "查询"

    override fun showTips(groupCode: Long, senderId: Long) = "查询 名字"

    override fun checkAuth(groupCode: Long, senderId: Long) = true

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message {
        val name = content.trim()
        if (name.isEmpty()) return PlainText("命令格式：\n查询 名字")
        return PlainText(HttpUtil.getScore(name))
    }
}