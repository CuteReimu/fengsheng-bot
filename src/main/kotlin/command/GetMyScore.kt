package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object GetMyScore : CommandHandler {
    override val name = "查询我"

    override fun showTips(groupCode: Long, senderId: Long) =
        if (PermData.playerMap.containsKey(senderId)) "查询我"
        else null

    override fun checkAuth(groupCode: Long, senderId: Long) = true

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message? {
        if (content.isNotBlank()) return null
        val name = PermData.playerMap[msg.sender.id] ?: return PlainText("请先绑定")
        return PlainText(HttpUtil.getScore(name))
    }
}