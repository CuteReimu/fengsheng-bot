package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object RemoveDictModify : CommandHandler {
    override val name = "删除词条权限"

    override fun showTips(groupCode: Long, senderId: Long) = "删除词条权限 对方QQ号"

    override fun checkAuth(groupCode: Long, senderId: Long) = PermData.isAdmin(senderId)

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message? {
        val qqNumbers = content.split(" ").map {
            runCatching { it.toLong() }.getOrNull() ?: return null
        }
        val (succeed, failed) = qqNumbers.partition { PermData.removeDictModify(it) }
        val qqNumberToString = { qqNumber: Long ->
            msg.group[qqNumber]?.nameCardOrNick?.let { name -> "${name}($qqNumber)" } ?: qqNumber.toString()
        }
        val result =
            if (succeed.isNotEmpty()) succeed.joinToString(prefix = "已删除词条权限：", transform = qqNumberToString)
            else failed.joinToString(postfix = "并没有词条权限", transform = qqNumberToString)
        return PlainText(result)
    }
}