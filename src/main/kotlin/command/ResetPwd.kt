package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object ResetPwd : CommandHandler {
    override val name = "重置密码"

    override fun showTips(groupCode: Long, senderId: Long) = "重置密码 名字"

    override fun checkAuth(groupCode: Long, senderId: Long) = PermData.isAdmin(senderId)

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message {
        val name = content.trim()
        if (name.isEmpty()) return PlainText("命令格式：\n重置密码 名字")
        HttpUtil.resetPwd(name)
        return PlainText("重置成功")
    }
}