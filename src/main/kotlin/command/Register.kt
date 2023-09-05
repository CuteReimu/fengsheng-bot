package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.toPlainText

object Register : CommandHandler {
    override val name = "注册"

    override fun showTips(groupCode: Long, senderId: Long) =
        if (!PermData.playerMap.containsKey(senderId)) "注册 名字"
        else null

    override fun checkAuth(groupCode: Long, senderId: Long) = true

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message? {
        val name = content.trim()
        if (name.isEmpty()) return PlainText("命令格式：\n注册 名字")
        val oldName = PermData.playerMap[msg.sender.id]
        if (oldName != null) return PlainText("你已经注册过：$oldName")
        try {
            if (!HttpUtil.register(name)) return PlainText("用户名重复")
        } catch (e: Exception) {
            return e.message?.toPlainText()
        }
        synchronized(PermData) {
            PermData.playerMap += msg.sender.id to name
            Bind.reversePlayerMap += name to msg.sender.id
        }
        return PlainText("注册成功")
    }
}