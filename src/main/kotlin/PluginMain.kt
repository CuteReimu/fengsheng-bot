package com.fengsheng.bot

import com.fengsheng.bot.command.Bind
import com.fengsheng.bot.storage.*
import com.fengsheng.bot.utils.HttpUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import kotlin.reflect.KClass

internal object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.fengsheng.bot",
        name = "Fengsheng Bot",
        version = "1.0.1"
    )
) {
    override fun onEnable() {
        FengshengConfig.reload()
        PermData.reload()
        QunDb.reload()
        ImageCache.reload()
        MuteCache.reload()
        Bind.initReverseMap()
        Dictionary.removeTimeoutImages()
        MuteCache.clearExpiredData()
        initHandler(GroupMessageEvent::class, CommandHandler::handle)
        initHandler(GroupMessageEvent::class, ::searchAt)
        initHandler(GroupMessageEvent::class, Dictionary::handle)
        initHandler(BotOnlineEvent::class, MuteHandler::handleOnline)
        initHandler(MemberMuteEvent::class, MuteHandler::handleMute)
        initHandler(MemberUnmuteEvent::class, MuteHandler::handleUnmute)
        initHandler(MemberJoinEvent::class, MuteHandler::handleJoinGroup)
    }

    private fun <E : Event> initHandler(eventClass: KClass<out E>, handler: suspend (E) -> Unit) {
        globalEventChannel().subscribeAlways(
            eventClass,
            CoroutineExceptionHandler { _, throwable ->
                logger.error(throwable)
            },
            priority = EventPriority.MONITOR,
        ) {
            launch { handler(this@subscribeAlways) }
        }
    }

    private suspend fun searchAt(e: GroupMessageEvent) {
        if (e.group.id !in FengshengConfig.qq.qqGroup)
            return
        val messages = e.message.filter { it is At || it is PlainText }
        if (messages.size >= 2) {
            val content = messages[0] as? PlainText ?: return
            if (content.content.trim() != "查询") return
            val at = messages[1] as? At ?: return
            val name = PermData.playerMap[at.target]
            e.group.sendMessage(if (name == null) "该玩家还未绑定" else HttpUtil.getScore(name))
        }
    }
}
