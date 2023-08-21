package com.fengsheng.bot.command

import com.fengsheng.bot.CommandHandler
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

object Bind : CommandHandler {
    var reversePlayerMap = mapOf<String, Long>()

    fun initReverseMap() {
        reversePlayerMap = PermData.playerMap.map { (k, v) -> v to k }.toMap()
    }

    override val name = "绑定"

    override fun showTips(groupCode: Long, senderId: Long) =
        if (!PermData.playerMap.containsKey(senderId)) "绑定 名字"
        else if (PermData.isAdmin(senderId)) "绑定 QQ号 名字"
        else null

    override fun checkAuth(groupCode: Long, senderId: Long) = true

    override suspend fun execute(msg: GroupMessageEvent, content: String): Message {
        var name = content.trim()
        var id = msg.sender.id
        if (PermData.isAdmin(id)) {
            val arr = name.split(" ", limit = 2)
            if (arr.size == 2) {
                runCatching {
                    id = arr[0].toLong()
                    name = arr[1].trim()
                    if (id !in msg.group) return PlainText("${id}不在群里")
                }
            }
        }
        if (name.isEmpty()) return PlainText("命令格式：\n绑定 名字")
        if (PermData.playerMap.containsKey(id)) return PlainText("不能重复绑定")
        val oldId = reversePlayerMap[name]
        if (oldId != null)
            return PlainText("该玩家已被$oldId(${msg.group[oldId]?.nameCardOrNick})绑定")
        val result = HttpUtil.getScore(name)
        if (result.endsWith("已身死道消")) return PlainText("不存在的玩家")
        synchronized(PermData) {
            PermData.playerMap += id to name
            reversePlayerMap += name to id
        }
        return PlainText("绑定成功")
    }
}