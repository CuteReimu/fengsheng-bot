package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object SetNotice : CommandHandler {
    override val name = "修改公告"

    override fun showTips(groupCode: Long, senderId: Long) = "修改公告 公告内容"

    override fun checkAuth(groupCode: Long, senderId: Long) = PermData.isAdmin(senderId)

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message {
        val s = content.trim()
        if (s.isEmpty()) return PlainText("命令格式：\n修改公告 公告内容")
        HttpUtil.setNotice(s)
        return PlainText("公告已变更")
    }
}