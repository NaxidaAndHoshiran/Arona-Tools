package cn.travellerr.aronaTools.permission

import net.mamoe.mirai.console.permission.AbstractPermitteeId
import net.mamoe.mirai.console.permission.PermissionId
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.permission.PermitteeId.Companion.permitteeId
import net.mamoe.mirai.contact.User


object PermissionController {
    @JvmField
    var subscribedPermission : PermissionId = PermissionId("arona", "subscribed")

    @JvmField
    var inviteBypassPermission : PermissionId = PermissionId("arona", "inviteBypass")

    fun regPerm() {
        PermissionService.INSTANCE.register(subscribedPermission, "阿洛娜订阅")
        PermissionService.INSTANCE.register(inviteBypassPermission, "阿洛娜邀请白名单")
    }

    @JvmStatic
    fun getUserPermittee(user: User?): AbstractPermitteeId.ExactUser? {
        return user?.permitteeId
    }

}