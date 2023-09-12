package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.toPlainText

object RemoveTitle : CommandHandler {
    override val name = "删除称号"

    override fun showTips(groupCode: Long, senderId: Long) =
        if (!PermData.playerMap.containsKey(senderId)) "删除称号"
        else null

    override fun checkAuth(groupCode: Long, senderId: Long) = true

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message? {
        if (content.trim().isNotEmpty()) return null
        val name = PermData.playerMap[msg.sender.id] ?: return PlainText("请先注册")
        return try {
            if (HttpUtil.updateTitle(name, "")) PlainText("称号已删除")
            else PlainText("你的段位太低，请提升段位后再来使用此功能")
        } catch (e: Exception) {
            e.message?.toPlainText()
        }
    }
}