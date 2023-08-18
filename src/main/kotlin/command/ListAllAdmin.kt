package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.FengshengConfig
import com.fengsheng.bot.storage.PermData
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object ListAllAdmin : CommandHandler {
    override val name = "查看管理员"

    override fun showTips(groupCode: Long, senderId: Long) = ""

    override fun checkAuth(groupCode: Long, senderId: Long) = true

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message {
        val result =
            (arrayOf(FengshengConfig.qq.superAdminQQ) + PermData.admin).map { msg.group[it]?.nameCardOrNick ?: it }
                .joinToString(separator = "\n", prefix = "管理员列表：\n")
        return PlainText(result)
    }
}