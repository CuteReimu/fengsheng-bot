package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object AtPlayer : CommandHandler {
    override val name = "艾特"

    override fun showTips(groupCode: Long, senderId: Long) = "艾特 游戏内的名字"

    override fun checkAuth(groupCode: Long, senderId: Long) = true

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message {
        val name = content.trim()
        if (name.isEmpty()) return PlainText("命令格式：\n艾特 游戏内的名字")
        val id = PermData.reversePlayerMap[name] ?: return PlainText("没能找到此玩家，可能还未绑定")
        val member = msg.group[id] ?: return PlainText("${id}不在群里")
        return At(member)
    }
}