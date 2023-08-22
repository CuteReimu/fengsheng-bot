package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object AddDictModify : CommandHandler {
    override val name = "增加词条权限"

    override fun showTips(groupCode: Long, senderId: Long) = "增加词条权限 对方QQ号"

    override fun checkAuth(groupCode: Long, senderId: Long) = PermData.isAdmin(senderId)

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message? {
        val qqNumbers = content.split(" ").map {
            runCatching { it.toLong() }.getOrNull() ?: return null
        }
        if (qqNumbers.isEmpty()) return null
        qqNumbers.forEach { if (it !in msg.group) return PlainText("${it}不是群成员") }
        val (succeed, failed) = qqNumbers.partition { PermData.addDictModify(it) }
        val qqNumberToString = { qqNumber: Long ->
            msg.group[qqNumber]?.nameCardOrNick?.let { name -> "${name}($qqNumber)" } ?: qqNumber.toString()
        }
        val result =
            if (succeed.isNotEmpty()) succeed.joinToString(prefix = "已增加词条权限：", transform = qqNumberToString)
            else failed.joinToString(postfix = "已经有词条权限了", transform = qqNumberToString)
        return PlainText(result)
    }
}