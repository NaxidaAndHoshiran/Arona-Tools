package cn.travellerr.aronaTools

import cn.travellerr.aronaTools.electronicPets.use.shop.Item
import cn.travellerr.aronaTools.electronicPets.use.task.Task
import cn.travellerr.aronaTools.electronicPets.use.type.ItemType
import cn.travellerr.aronaTools.electronicPets.use.type.TaskType
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object ElectronicPetWorkShop : AutoSavePluginData("WorkShop") {
    @ValueDescription("电子宠物任务工坊")
    var tasks: MutableMap<Int, Task> by value(mutableMapOf(
        1 to Task(
            "coding",
            "编程",
            "编程是一种创造性的工作",
            true,
            TaskType.WORK,
            10,
            5.0,
            2.0,
            1.0,
            "SYSTEM",
            0
        ),
        2 to Task(
            "streaming",
            "直播",
            "Description 2",
            true,
            TaskType.WORK,
            20,
            10.0,
            4.0,
            2.0,
            "SYSTEM",
            0
        )
    ))

    @ValueDescription("电子宠物道具工坊")
    var items: MutableMap<Int, Item> by value(mutableMapOf(
        1 to Item(
            "salad",
            "沙拉",
            "沙拉沙拉啦啦啦",
            true,
            ItemType.FOOD,
            49,
            120,
            80.0,
            12.0,
            10.0,
            0.0,
            "SYSTEM",
            0
        ),
        2 to Item(
            "Sirloin Steak",
            "西冷牛排",
            "开瓶拉菲吧，上流",
            true,
            ItemType.FOOD,
            150,
            200,
            140.0,
            100.0,
            15.0,
            1.0,
            "SYSTEM",
            0
        ),
        3 to Item(
            "ball",
            "球",
            "我弹弹弹弹……啊，飞了……",
            true,
            ItemType.TOY,
            30,
            250,
            0.0,
            30.0,
            0.0,
            2.0,
            "SYSTEM",
            0
        ),
        4 to Item(
            "towel",
            "浴巾",
            "船新的浴巾，柔软舒适",
            true,
            ItemType.WASH,
            50,
            50,
            0.0,
            10.0,
            50.0,
            0.0,
            "SYSTEM",
            0
        ),
        5 to Item(
            "coke",
            "可乐",
            "冰冰凉凉的可乐",
            true,
            ItemType.DRINK,
            10,
            4,
            2.0,
            50.0,
            -1.0,
            0.0,
            "SYSTEM",
            0

        )
    ))
}