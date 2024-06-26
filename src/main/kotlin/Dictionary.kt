package com.fengsheng.bot

import com.fengsheng.bot.storage.FengshengConfig
import com.fengsheng.bot.storage.ImageCache
import com.fengsheng.bot.storage.ImageCache.ImageData
import com.fengsheng.bot.storage.PermData
import com.fengsheng.bot.storage.QunDb
import com.fengsheng.bot.utils.HttpUtil
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChain.Companion.serializeToJsonString
import net.mamoe.mirai.message.data.MessageSource
import net.mamoe.mirai.message.data.toMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.MiraiLogger
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object Dictionary {
    private val addDbQQList = ConcurrentHashMap<Long, Pair<String, String>>()

    suspend fun handle(e: GroupMessageEvent) {
        if (e.group.id !in FengshengConfig.qq.qqGroup)
            return
        val perm = e.sender.id in PermData.dictModify
        val content = e.message.contentToString()
        if (perm && content == "查看过期词条") {
            val keys = QunDb.data.filter { (_, value) ->
                containsExpiredImage(MessageChain.deserializeFromJsonString(value))
            }.keys
            if (keys.isNotEmpty()) e.group.sendMessage(keys.joinToString(separator = "\n"))
            else e.group.sendMessage("没有过期词条")
        } else if (perm && content.startsWith("添加词条 ")) {
            val key = content.substring(4).trim().dealKey()
            if (CommandHandler.handlers.any { it.name == key }) {
                e.group.sendMessage("不能用${key}作为词条")
            }
            if (key.isNotEmpty()) {
                if (key in QunDb.data) {
                    e.group.sendMessage("词条已存在")
                } else {
                    e.group.sendMessage("请输入要添加的内容")
                    addDbQQList[e.sender.id] = key to "添加词条成功"
                }
            }
        } else if (perm && content.startsWith("修改词条 ")) {
            val key = content.substring(4).trim().dealKey()
            if (key.isNotEmpty()) {
                if (key !in QunDb.data) {
                    e.group.sendMessage("词条不存在")
                } else {
                    e.group.sendMessage("请输入要修改的内容")
                    addDbQQList[e.sender.id] = key to "修改词条成功"
                }
            }
        } else if (perm && content.startsWith("删除词条 ")) {
            val key = content.substring(4).trim().dealKey()
            if (key.isNotEmpty()) {
                if (key !in QunDb.data) {
                    e.group.sendMessage("词条不存在")
                } else {
                    QunDb.data -= key
                    e.group.sendMessage("删除词条成功")
                }
            }
        } else if (content.startsWith("查询词条 ") || content.startsWith("搜索词条 ")) {
            val key = content.substring(4).trim().dealKey()
            if (key.isNotEmpty()) {
                val res = QunDb.data.keys.filter { key in it }.sorted()
                if (res.isNotEmpty()) {
                    val res1 = res.withIndex().map { (i, v) -> "${i + 1}. $v" }
                    e.group.sendMessage(
                        res1.joinToString(
                            separator = "\n",
                            prefix = "搜索到以下词条：\n",
                            limit = 10,
                            truncated = "等${res1.size}个词条"
                        )
                    )
                } else {
                    e.group.sendMessage("搜索不到词条($key)")
                }
            }
        } else {
            val lastKey = addDbQQList.remove(e.sender.id)
            if (lastKey != null) { // 添加词条
                val message2 = e.message.filterNot { it is MessageSource }.toMessageChain()
                QunDb.data += lastKey.first to message2.serializeToJsonString()
                saveImage(message2)
                e.group.sendMessage(lastKey.second)
            } else { // 调用词条
                val value = QunDb.data[content.dealKey()]
                if (value != null) {
                    val mc1 = MessageChain.deserializeFromJsonString(value)
                    val mc2 = ensureImage(e.group, mc1)
                    if (mc1 !== mc2) QunDb.data += content to mc2.serializeToJsonString()
                    e.group.sendMessage(mc2)
                }
            }
        }
    }

    private suspend fun saveImage(mc: MessageChain) {
        for (m in mc) {
            if (m is Image) {
                try {
                    val buf = HttpUtil.getPic(m.queryUrl()).use { it.readAllBytes() }
                    File("dictionary-images").apply { if (!exists()) mkdirs() }
                    File("dictionary-images${File.separatorChar}${m.imageId}").writeBytes(buf)
                    ImageCache.data += m.imageId to ImageData(System.currentTimeMillis())
                } catch (e: Exception) {
                    logger.error("保存图片失败", e)
                }
            }
        }
    }

    private fun containsExpiredImage(mc: MessageChain): Boolean = mc.any {
        it is Image && !(File("dictionary-images").exists() &&
                File("dictionary-images${File.separatorChar}${it.imageId}").exists())
    }

    private suspend fun ensureImage(group: Group, ms: MessageChain): MessageChain {
        if (ms.all { it !is Image })
            return ms
        var changed = false
        val l = ms.map { m ->
            if (m is Image) {
                ImageCache.data[m.imageId]?.also { imageData ->
                    val now = System.currentTimeMillis()
                    if (now - FengshengConfig.imageExpireHours * 3600 * 1000 >= imageData.time) {
                        changed = true
                        val file = File("dictionary-images${File.separatorChar}${m.imageId}")
                        if (file.exists()) {
                            val buf = file.readBytes()
                            val image = buf.toExternalResource().use { group.uploadImage(it) }
                            File("dictionary-images").apply { if (!exists()) mkdirs() }
                            File("dictionary-images${File.separatorChar}${image.imageId}").writeBytes(buf)
                            ImageCache.data += image.imageId to ImageData(now)
                            return@map image
                        }
                    }
                }
            }
            m
        }
        return if (changed) l.toMessageChain() else ms
    }

    fun removeTimeoutImages() {
        val files = File("dictionary-images").list() ?: return
        val deleteFileSet = files.toMutableSet()
        val remainFileSet = HashSet<String>()
        QunDb.data.forEach { (_, v) ->
            val message = MessageChain.deserializeFromJsonString(v)
            message.forEach { m ->
                (m as? Image)?.imageId?.also {
                    deleteFileSet -= it
                    remainFileSet += it
                }
            }
        }
        deleteFileSet.forEach { File("dictionary-images${File.separatorChar}$it").delete() }
        ImageCache.data = ImageCache.data.filter { (imageId, _) -> imageId in remainFileSet }
    }

    private fun String.dealKey() =
        replace("零", "0").replace("一", "1").replace("二", "2").replace("三", "3")
            .replace("四", "4").replace("五", "5").replace("六", "6").replace("七", "7")
            .replace("八", "8").replace("九", "9").lowercase()

    private val logger: MiraiLogger by lazy {
        MiraiLogger.Factory.create(this::class, this::class.java.name)
    }
}