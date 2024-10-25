package cn.travellerr.aronaTools.command

import cn.travellerr.aronaTools.AronaTools
import cn.travellerr.aronaTools.echoCaves.EchoManager
import cn.travellerr.aronaTools.subscribedChannel.Subscribed
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CommandOwner
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.permission.PermitteeId.Companion.permitteeId
import net.mamoe.mirai.contact.Group

object CheckKey  :
    SimpleCommand(AronaTools.INSTANCE as CommandOwner, "认证", "认证发电用户") {
    @Handler
    fun useKey(context: CommandContext, key: String?) {
        val subject = context.sender.subject!!
        val user = context.sender.user!!
        AronaTools.INSTANCE.logger.info("${user.permitteeId}")
        Subscribed.useKey(subject, user, key)
    }
    @Handler
    suspend fun useError(sender: CommandContext) {
        val subject = sender.sender.subject
        val originMsg = sender.originalMessage[1]
        val prefix = originMsg.toString().split(" ")[0]
        subject?.sendMessage("请使用 \"$prefix [认证码]\"兑换，不要加括号")
    }
}

object CreateEcho : SimpleCommand(AronaTools.INSTANCE,"createEcho",
    "添加回声", "添加回声洞",
    description = "添加回声") {
    @Handler
    suspend fun echo(context: CommandContext, vararg msg: String) {
        val subject = context.sender.subject!!
        val user = context.sender.user!!

        if (msg.isEmpty()) {
            subject.sendMessage("请输入回声内容!")
            return
        }

        val wholeMessage = msg.joinToString(" ")

        if (wholeMessage.length > 600) {
            subject.sendMessage("回声内容过长，请控制在600字以内")
            return
        }

        if (subject is Group) {
            EchoManager.createEcho(subject, user, wholeMessage, subject)
            return
        }

        EchoManager.createEcho(subject, user, wholeMessage)
    }
}

object GetEcho : SimpleCommand(AronaTools.INSTANCE,"getEcho",
    "回声", "回声洞" , "获取回声" ,"获取回声洞",
    description = "获取回声") {
    @Handler
    suspend fun echo(context: CommandContext, vararg args: String) {
        val subject = context.sender.subject!!
        val user = context.sender.user!!

        val originMessage = context.originalMessage

        val id = args.getOrNull(0)?.toLongOrNull()
        if (args.isEmpty()) {
            EchoManager.getRandomEcho(subject, user, originMessage)
            return
        }

        if (id is Long) {
            EchoManager.getDesignatedEcho(subject, user, id, originMessage)
            return
        }

        val originMsg = context.originalMessage[1]
        val prefix = originMsg.toString().split(" ")[0]
        subject.sendMessage("请使用 \"$prefix [回声ID]\"获取回声，不要加括号，ID为从1开始的正整数数字")
    }
}

object DeleteEcho : SimpleCommand(AronaTools.INSTANCE,"deleteEcho",
    "删除回声", "删除回声洞",
    description = "删除回声") {
    @Handler
    suspend fun echo(context: CommandContext, vararg args: String) {
        val subject = context.sender.subject!!

        val id = args.getOrNull(0)?.toLongOrNull()
        if (id is Long) {
            EchoManager.deleteEcho(context, id)
            return
        }

        val originMsg = context.originalMessage[1]
        val prefix = originMsg.toString().split(" ")[0]
        subject.sendMessage("请使用 \"$prefix [回声ID]\"删除回声，不要加括号，ID为从1开始的正整数数字")
    }
}

object ReportEcho : SimpleCommand(AronaTools.INSTANCE,"reportEcho",
    "举报回声", "举报回声洞",
    description = "举报回声") {
    @Handler
    suspend fun echo(context: CommandContext, vararg args: String) {
        val subject = context.sender.subject!!
        val user = context.sender.user!!

        val id = args.getOrNull(0)?.toLongOrNull()
        if (id is Long) {
            EchoManager.reportEcho(subject, user, id)
            return
        }

        val originMsg = context.originalMessage[1]
        val prefix = originMsg.toString().split(" ")[0]
        subject.sendMessage("请使用 \"$prefix [回声ID]\"举报回声，不要加括号，ID为从1开始的正整数数字")
    }
}

object GetReportEcho : SimpleCommand(AronaTools.INSTANCE,"getReportEcho",
    "获取举报回声", "获取举报回声洞",
    description = "获取举报回声") {
    @Handler
    fun echo(context: CommandContext) {
        val subject = context.sender.subject!!
        val user = context.sender.user!!
        EchoManager.getReportedEcho(subject, user)
    }
}

object GetMyEchoList : SimpleCommand(AronaTools.INSTANCE,"getMyEchoList",
    "回声列表", "回声洞列表",
    description = "获取回声列表") {
    @Handler
    fun echo(context: CommandContext) {
        val subject = context.sender.subject!!
        val user = context.sender.user!!
        EchoManager.getMyEchoList(subject, user)
    }
}