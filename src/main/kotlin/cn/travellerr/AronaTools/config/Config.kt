package cn.travellerr.aronaTools.config

import cn.chahuyun.hibernateplus.DriveType
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginConfig("config") {
    @ValueDescription("点歌所需金币")
    var songMoney: Int by value(10)

    @ValueDescription("回声洞冷却时间(秒)")
    var echoCoolDown: Long by value(60L)

    @ValueDescription("回声洞忽略限制用户列表")
    var ignoreUserEchoList: List<Long> by value(listOf(3132522039))

    @ValueDescription("自动审核所需金币")
    var money: Int by value(300)

    @ValueDescription("自动审核所需好感度")
    var love: Int by value(380)

    @ValueDescription("自动审核所需群组")
    var group: List<Long> by value(listOf(626860767, 970271300))

    @ValueDescription("兑换码验证链接")
    var url: String by value()

    @ValueDescription("添加回声所需好感等级")
    var echoFavorLevel: Int by value(3)

    @ValueDescription("添加回声所需金币")
    var echoMoney: Int by value(50)

    @ValueDescription("数据库类型(H2,MYSQL,SQLITE)")
    var dataType: DriveType by value(DriveType.H2)

    @ValueDescription("mysql 连接地址")
    var mysqlUrl: String by value("localhost:3306/test")

    @ValueDescription("mysql 用户名")
    var mysqlUser: String by value("root")

    @ValueDescription("mysql 密码")
    var mysqlPassword: String by value("123456")
}