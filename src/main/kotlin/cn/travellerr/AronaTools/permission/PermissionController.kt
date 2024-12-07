package cn.travellerr.aronaTools.permission

import net.mamoe.mirai.console.permission.AbstractPermitteeId
import net.mamoe.mirai.console.permission.PermissionId
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.console.permission.PermitteeId.Companion.permitteeId
import net.mamoe.mirai.contact.User


object PermissionController {
    @JvmField
    var subscribedPermission : PermissionId = PermissionId("arona", "subscribed")

    @JvmField
    var inviteBypassPermission : PermissionId = PermissionId("arona", "inviteBypass")

    @JvmField
    var addFriendBypassPermission : PermissionId = PermissionId("arona", "addFriendBypass")

    @JvmField
    var searchSongBypassPermission : PermissionId = PermissionId("arona", "searchSongBypass")

    fun regPerm() {
        PermissionService.INSTANCE.register(subscribedPermission, "阿洛娜订阅")
        PermissionService.INSTANCE.register(inviteBypassPermission, "阿洛娜邀请白名单")
        PermissionService.INSTANCE.register(addFriendBypassPermission, "阿洛娜添加好友白名单")
        PermissionService.INSTANCE.register(searchSongBypassPermission, "阿洛娜点歌白名单")
    }

    @JvmStatic
    fun getUserPermittee(user: User?): AbstractPermitteeId.ExactUser? {
        return user?.permitteeId
    }

    @JvmStatic
    fun hasPermission(user: User?, permission: PermissionId): Boolean {
        val exactUser: AbstractPermitteeId.ExactUser? = getUserPermittee(user)
        return exactUser != null && exactUser.hasPermission(permission)
    }

    @JvmStatic
    fun hasPermission(id: Long?, permission: PermissionId): Boolean {
        val exactUser: AbstractPermitteeId.ExactUser? = AbstractPermitteeId.parseFromString("u"+id.toString()) as AbstractPermitteeId.ExactUser?
        return exactUser != null && exactUser.hasPermission(permission)
    }

}