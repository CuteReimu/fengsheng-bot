package com.fengsheng.bot.storage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value

object FengshengConfig : AutoSavePluginConfig("FengshengConfig") {
    @Serializable
    class QQConfig(
        @SerialName("super_admin_qq")
        /** 主管理员QQ号 */
        val superAdminQQ: Long,

        @SerialName("qq_group")
        /** 主要功能的QQ群 */
        val qqGroup: LongArray,
    )

    @ValueDescription("QQ相关配置")
    val qq: QQConfig by value(
        QQConfig(
            superAdminQQ = 12345678,
            qqGroup = longArrayOf(12345678)
        )
    )

    val fengshengUrl: String by value("http://127.0.0.1:9092")

    fun isSuperAdmin(qq: Long) =
        qq == this.qq.superAdminQQ

    @ValueDescription("图片超时时间（单位：小时）")
    @ValueName("image_expire_hours")
    val imageExpireHours: Long by value(72L)
}