package cn.travellerr.aronaTools.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginConfig("config") {
    @ValueDescription("自动审核所需金币")
    var money: Int by value(300)

    @ValueDescription("自动审核所需好感度")
    var love: Int by value(380)

    @ValueDescription("自动审核所需群组")
    var group: List<Long> by value(listOf(626860767, 970271300))

    @ValueDescription("兑换码验证链接")
    var url: String by value()
}