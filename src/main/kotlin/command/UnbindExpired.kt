package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.FengshengConfig
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object UnbindExpired : CommandHandler {
    override val name = "解绑所有0分玩家"

    override fun showTips(groupCode: Long, senderId: Long) = "解绑所有0分玩家"

    override fun checkAuth(groupCode: Long, senderId: Long) = FengshengConfig.isSuperAdmin(senderId)

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message? {
        content.isBlank() || return null
        for (name in PermData.playerMap.values.toList()) {
            val s = HttpUtil.getScore(name)
            if ("·0，总场次：0，" in s || s.endsWith("已身死道消")) {
                val id = Bind.reversePlayerMap[name] ?: continue
                synchronized(PermData) {
                    PermData.playerMap -= id
                    Bind.reversePlayerMap -= name
                }
            }
        }
        return PlainText("解绑成功")
    }
}