package com.fengsheng.bot.storage

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object ImageCache : AutoSavePluginData("ImageCache") {
    @Serializable
    class ImageData(
        val time: Long,
    )

    var data: Map<String, ImageData> by value()
}