package cn.travellerr.aronaTools.permission

import net.mamoe.mirai.console.permission.PermissionId
import net.mamoe.mirai.console.permission.PermissionService



object PermissionController {
    @JvmField
    var subscribedPermission : PermissionId = PermissionId("arona", "subscribed")

    fun regPerm() {
        PermissionService.INSTANCE.register(subscribedPermission, "阿洛娜订阅")
    }

}