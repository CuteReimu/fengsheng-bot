package com.fengsheng.bot.storage

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object QunDb : AutoSavePluginData("QunDb") {
    var data: Map<String, String> by value()
}