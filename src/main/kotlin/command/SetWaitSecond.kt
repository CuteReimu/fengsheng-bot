package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object SetWaitSecond : CommandHandler {
    override val name = "修改出牌时间"

    override fun showTips(groupCode: Long, senderId: Long) = "修改出牌时间 秒数"

    override fun checkAuth(groupCode: Long, senderId: Long) = PermData.isAdmin(senderId)

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message {
        val second = runCatching { content.toInt() }.getOrElse { return PlainText("命令格式：\n修改出牌时间 秒数") }
        if (second <= 0) return PlainText("出牌时间必须大于0")
        if (!HttpUtil.setWaitSecond(second)) PlainText("修改默认出牌时间失败")
        return PlainText("默认出牌时间已修改为${second}秒")
    }
}