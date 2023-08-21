package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object Unbind : CommandHandler {
    override val name = "解绑"

    override fun showTips(groupCode: Long, senderId: Long) = "解绑 QQ号"

    override fun checkAuth(groupCode: Long, senderId: Long) = PermData.isAdmin(senderId)

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message {
        val id = runCatching { content.toLong() }.getOrElse {
            return PlainText("命令格式：\n解绑 QQ号")
        }
        if (!PermData.playerMap.containsKey(id)) return PlainText("玩家没有绑定")
        synchronized(PermData) { PermData.playerMap -= id }
        return PlainText("解绑成功")
    }
}