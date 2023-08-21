package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object ResetPwd : CommandHandler {
    override val name = "重置密码"

    override fun showTips(groupCode: Long, senderId: Long) =
        if (PermData.playerMap.containsKey(senderId)) "重置密码"
        else if (PermData.isAdmin(senderId)) "重置密码 名字"
        else null

    override fun checkAuth(groupCode: Long, senderId: Long) = true

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message? {
        val name = content.trim()
        if (name.isEmpty()) {
            val playerName = PermData.playerMap[msg.sender.id]
                ?: return if (!PermData.isAdmin(msg.sender.id)) null else PlainText("命令格式：\n重置密码 名字")
            return PlainText(HttpUtil.resetPwd(playerName))
        } else {
            if (!PermData.isAdmin(msg.sender.id)) return null
            return PlainText(HttpUtil.resetPwd(name))
        }
    }
}