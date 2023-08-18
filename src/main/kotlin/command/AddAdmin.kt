package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.FengshengConfig
import com.fengsheng.bot.storage.PermData
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object AddAdmin : CommandHandler {
    override val name = "增加管理员"

    override fun showTips(groupCode: Long, senderId: Long) = "增加管理员 对方QQ号"

    override fun checkAuth(groupCode: Long, senderId: Long) = FengshengConfig.isSuperAdmin(senderId)

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message? {
        val qqNumbers = content.split(" ").map {
            runCatching { it.toLong() }.getOrNull() ?: return null
        }
        if (qqNumbers.isEmpty()) return null
        qqNumbers.forEach { if (it !in msg.group) return PlainText("${it}不是群成员") }
        val (succeed, failed) = qqNumbers.partition { !FengshengConfig.isSuperAdmin(it) && PermData.addAdmin(it) }
        val qqNumberToString = { qqNumber: Long ->
            msg.group[qqNumber]?.nameCardOrNick?.let { name -> "${name}($qqNumber)" } ?: qqNumber.toString()
        }
        val result =
            if (succeed.isNotEmpty()) succeed.joinToString(prefix = "已增加管理员：", transform = qqNumberToString)
            else failed.joinToString(postfix = "已经是管理员了", transform = qqNumberToString)
        return PlainText(result)
    }
}