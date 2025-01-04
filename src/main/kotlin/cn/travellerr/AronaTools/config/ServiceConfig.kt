package cn.travellerr.aronaTools.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object ServiceConfig : AutoSavePluginConfig("ServiceConfig") {
    @ValueDescription("启用拉群自动发送菜单功能")
    var enableAutoSendMenu: Boolean by value(true)

    @ValueDescription("启用加好友自动审核")
    var enableAutoAddFriend: Boolean by value(true)

    @ValueDescription("启用加群自动审核")
    var enableAutoJoinGroup: Boolean by value(true)
}