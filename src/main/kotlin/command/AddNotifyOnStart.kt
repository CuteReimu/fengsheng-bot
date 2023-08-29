package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object AddNotifyOnStart : CommandHandler {
    override val name = "开了喊我"

    override fun showTips(groupCode: Long, senderId: Long) = "开了喊我"

    override fun checkAuth(groupCode: Long, senderId: Long) = true

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message? {
        content.isBlank() || return null
        return if (HttpUtil.addNotify(msg.sender.id, false)) PlainText("好的，开了喊你")
        else PlainText("太多人预约了，不能再添加了")
    }
}