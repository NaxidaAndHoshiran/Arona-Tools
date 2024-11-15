package cn.travellerr.aronaTools.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object PetConfig : AutoSavePluginConfig("petConfig") {
    @ValueDescription("更改宠物名所需金币")
    var petRenameMoney: Int by value(50)

    @ValueDescription("创建工坊物品所需金币")
    var createWorkshopItemMoney: Int by value(200)

    @ValueDescription("每兑换一枚用户金币所需宠物金币(1:x)")
    var exchangePetMoney: Double by value(5.0)
}