package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object ReleaseRole : CommandHandler {
    override val name = "启用角色"

    override fun showTips(groupCode: Long, senderId: Long) = "启用角色 名字"

    override fun checkAuth(groupCode: Long, senderId: Long) = PermData.isAdmin(senderId)

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message {
        val name = content.trim()
        if (name.isEmpty()) return PlainText("命令格式：\n启用角色 名字")
        val succeed = HttpUtil.releaseRole(name)
        return PlainText("启用角色$name" + if (succeed) "成功" else "失败")
    }
}