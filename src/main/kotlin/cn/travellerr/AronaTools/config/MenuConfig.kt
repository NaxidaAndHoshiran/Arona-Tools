package cn.travellerr.aronaTools.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object MenuConfig : AutoSavePluginConfig("MenuConfig") {
    @ValueDescription("菜单图片路径(相对与插件data目录)，一个图片路径对应一个指令")
    var menus: MutableList<String> by value(mutableListOf(
    ))

    @ValueDescription("菜单指令，一个指令对应一个图片路径，可使用正则表达式")
    var commands: MutableList<String> by value(mutableListOf(
    ))
}