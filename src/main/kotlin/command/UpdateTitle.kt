package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.toPlainText

object UpdateTitle : CommandHandler {
    override val name = "修改称号"

    override fun showTips(groupCode: Long, senderId: Long) =
        if (!PermData.playerMap.containsKey(senderId)) "修改称号 称号"
        else null

    override fun checkAuth(groupCode: Long, senderId: Long) = true

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message? {
        val title = content.trim()
        if (title.isEmpty()) return PlainText("命令格式：\n修改称号 称号")
        val name = PermData.playerMap[msg.sender.id] ?: return PlainText("请先注册")
        return try {
            if (HttpUtil.updateTitle(name, title)) PlainText("修改称号成功")
            else PlainText("你的段位太低，请提升段位后再来使用此功能")
        } catch (e: Exception) {
            e.message?.toPlainText()
        }
    }
}