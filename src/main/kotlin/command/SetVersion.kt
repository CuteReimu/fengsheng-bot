package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object SetVersion : CommandHandler {
    override val name = "修改版本号"

    override fun showTips(groupCode: Long, senderId: Long) = "修改版本号 版本号"

    override fun checkAuth(groupCode: Long, senderId: Long) = PermData.isAdmin(senderId)

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message {
        val version = runCatching { content.toInt() }.getOrElse { return PlainText("命令格式：\n修改版本号 版本号") }
        HttpUtil.setVersion(version)
        return PlainText("版本号已修改为$version")
    }
}