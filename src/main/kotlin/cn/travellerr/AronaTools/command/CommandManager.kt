package cn.travellerr.aronaTools.command

import cn.chahuyun.hibernateplus.HibernateFactory
import cn.travellerr.aronaTools.AronaTools
import cn.travellerr.aronaTools.broadcast.BroadCastManager
import cn.travellerr.aronaTools.echoCaves.EchoManager
import cn.travellerr.aronaTools.electronicPets.use.PetManager
import cn.travellerr.aronaTools.electronicPets.use.shop.WorkShopItemManager
import cn.travellerr.aronaTools.electronicPets.use.task.WorkShopTaskManager
import cn.travellerr.aronaTools.entity.PetInfo
import cn.travellerr.aronaTools.subscribedChannel.Subscribed
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.permission.PermitteeId.Companion.permitteeId
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.*

object CheckKey  :
    SimpleCommand(AronaTools.INSTANCE as CommandOwner, "认证", "认证发电用户") {
    @Handler
    fun useKey(context: CommandContext, key: String?) {
        val subject = context.sender.subject!!
        val user = context.sender.user!!
        AronaTools.INSTANCE.logger.info("${user.permitteeId}")
        Subscribed.useKey(subject, user, context.originalMessage, key)
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

        var originMsg : String = context.originalMessage.content.trim()
        val prefix = originMsg.split(" ")[0]
        originMsg = originMsg.removePrefix(prefix).trim()

        if (originMsg.matches("\\d+".toRegex())) {
            subject.sendMessage("回声内容不能全为数字!\n你是否要使用 \"" + CommandManager.commandPrefix + "回声洞 [回声ID]\"?")
            return
        }

        if (originMsg.replace(Regex("[\\p{Punct}\\s]"), "").matches("\\d{4,}".toRegex())) {
            subject.sendMessage("为了避免宣传可能，回声内容不能包含4位以上的数字!\n你是否要使用 \"" + CommandManager.commandPrefix + "回声洞 [回声ID]\"?")
            return
        }

        val image = context.originalMessage.filterIsInstance<Image>().firstOrNull()
        if (image != null) {
            subject.sendMessage(QuoteReply(context.originalMessage).plus("回声洞目前不支持图片消息哦~\n").plus(image))
            return
        }

        if (!AronaTools.config.ignoreUserEchoList.contains(user.id) && originMsg.length > 600) {
            subject.sendMessage("回声内容过长，请控制在600字以内")
            return
        }

        if (subject is Group) {
            EchoManager.createEcho(subject, user, originMsg, subject)
            return
        }

        EchoManager.createEcho(subject, user, originMsg)
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
        subject.sendMessage("请使用 \"$prefix [回声ID]\"获取回声，不要加括号，ID为从1开始的正整数数字\n你是否要使用 \""
            + CommandManager.commandPrefix + "添加回声 [回声内容]\"?")
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
        subject.sendMessage("请使用 \"$prefix [回声ID]\"删除回声，不要加括号，ID为从1开始的正整数数字\n你是否要使用 \""
            + CommandManager.commandPrefix + "添加回声 [回声内容]\"?")
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
        subject.sendMessage("请使用 \"$prefix [回声ID]\"举报回声，不要加括号，ID为从1开始的正整数数字\n你是否要使用 \""
            + CommandManager.commandPrefix + "添加回声 [回声内容]\"?")
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

object VerifyEcho :CompositeCommand(AronaTools.INSTANCE,"verifyEcho",
    "审核回声", "审核回声洞", "审核",
    description = "审核回声") {

    @SubCommand("通过", "通过回声", "通过回声洞")
    fun approve(context: CommandContext, id: Long) {
        val subject = context.sender.subject!!
        val user = context.sender.user!!

        EchoManager.approveEcho(subject, user, id)
        return
    }

    @SubCommand("拒绝", "拒绝回声", "拒绝回声洞")
    fun reject(context: CommandContext, id: Long) {
        val subject = context.sender.subject!!
        val user = context.sender.user!!

        EchoManager.rejectEcho(subject, user, id)
        return
    }

    @SubCommand("获取", "获取回声", "获取回声洞", "获取被举报回声", "获取被举报回声洞")
    fun getReportedEcho(context: CommandContext) {
        val subject = context.sender.subject!!
        val user = context.sender.user!!
        EchoManager.getReportedEcho(subject, user)
    }


}

object VerifyPetTaskWorkShop : CompositeCommand(AronaTools.INSTANCE,"verifyPetTaskWorkShop",
    "审核电子宠物任务工坊", "审核任务工坊", "审核任务",
    description = "审核电子宠物工坊任务") {

    @SubCommand("通过", "通过任务", "通过电子宠物工坊任务")
    fun approve(context: CommandContext, id: Int) {
        val subject = context.sender.subject!!
        WorkShopTaskManager.approveTask(subject, id)
        return
    }

    @SubCommand("拒绝", "拒绝任务", "拒绝电子宠物工坊任务")
    fun reject(context: CommandContext, id: Int) {
        val subject = context.sender.subject!!
        WorkShopTaskManager.rejectTask(subject, id)

        return
    }

    @SubCommand("获取", "获取任务", "获取电子宠物工坊任务", "获取待审核任务", "获取待审核电子宠物工坊任务")
    fun getUnverifiedTasks(context: CommandContext) {
        val subject = context.sender.subject!!

        WorkShopTaskManager.getUnverifiedTaskList(subject)

        return
    }
}

object VerifyPetItemWorkShop : CompositeCommand(AronaTools.INSTANCE,"verifyPetItemWorkShop",
    "审核电子宠物工坊", "审核物品工坊", "审核物品",
    description = "审核电子宠物工坊物品") {

    @SubCommand("通过", "通过物品", "通过电子宠物工坊物品")
    fun approve(context: CommandContext, id: Int) {
        val subject = context.sender.subject!!
        WorkShopItemManager.approveItem(subject, id)
        return
    }

    @SubCommand("拒绝", "拒绝物品", "拒绝电子宠物工坊物品")
    fun reject(context: CommandContext, id: Int) {
        val subject = context.sender.subject!!
        WorkShopItemManager.rejectItem(subject, id)

        return
    }

    @SubCommand("获取", "获取物品", "获取电子宠物工坊物品", "获取待审核物品", "获取待审核电子宠物工坊物品")
    fun getUnverifiedTasks(context: CommandContext) {
        val subject = context.sender.subject!!

        WorkShopItemManager.getUnverifiedItemList(subject)

        return
    }
}

object Tester : CompositeCommand(AronaTools.INSTANCE, "tester",
    "测试", "测试命令", "测试指令",
    description = "测试命令") {

    @SubCommand("增加宠物经验")
    suspend fun test(context: CommandContext, exp: Long) {
        val subject = context.sender.subject!!
        val user = context.sender.user!!

        val petInfo = PetManager.getPetInfo(user.id)

        if (petInfo == null) {
            subject.sendMessage("你还没有宠物哦")
            return
        }

        petInfo.addExp(exp)
        petInfo.save()

        subject.sendMessage("增加了${exp}点经验")
    }
}

object Pet : CompositeCommand(AronaTools.INSTANCE, "pet", "宠物", "宠物系统", "宠物系统管理", "宠物管理", description = "宠物系统管理指令") {
    @SubCommand("获取", "获取宠物", "获取宠物列表")
    suspend fun getPet(context: CommandContext) {
        val subject = context.sender.subject!!
        val bot = context.sender.bot!!

        val petList = HibernateFactory.selectList(PetInfo::class.java)

        if (petList.isEmpty()) {
            subject.sendMessage(QuoteReply(context.originalMessage).plus("系统中还没有宠物"))
            return
        }

        val forwardMsg = ForwardMessageBuilder(subject)


        for (pet in petList) {
            val message = PlainText("宠物所有者: ${pet.userId}\n").plus(pet.safeInfoMessage())
            forwardMsg.add(bot, message)
        }

            subject.sendMessage(forwardMsg.build())
    }

    @SubCommand("重新计算")
    suspend fun recalculate(context: CommandContext) {
        val subject = context.sender.subject!!

        val petList = HibernateFactory.selectList(PetInfo::class.java)

        if (petList.isEmpty()) {
            subject.sendMessage(QuoteReply(context.originalMessage).plus("系统中还没有宠物"))
            return
        }

        for (pet in petList) {
            pet.reCalculate()
            pet.save()
        }

        subject.sendMessage("已重新计算所有宠物数值~")
    }

    @SubCommand("重新计算")
    suspend fun recalculate(context: CommandContext, user: At) {
        val subject = context.sender.subject!!
        val userId = user.target

        val petInfo = HibernateFactory.selectOne(PetInfo::class.java, userId)

        if (petInfo == null) {
            subject.sendMessage(QuoteReply(context.originalMessage).plus("该用户没有宠物"))
            return
        }

        petInfo.reCalculate()
        petInfo.save()

        subject.sendMessage("已重新计算该用户宠物数值~")
        subject.sendMessage(petInfo.safeInfoMessage())
    }
}

object BroadCast : SimpleCommand(AronaTools.INSTANCE, "broadcast", "广播", "全局广播", "全局消息", "发送公告", description = "全局广播") {

    @Handler
    suspend fun broadcast(context: CommandContext, vararg msg: String) {
        val subject = context.sender.subject!!
        val user = context.sender.user!!

        val originMsg : String = context.originalMessage.content.trim()
        val prefix = originMsg.split(" ")[0]
        val message = originMsg.removePrefix(prefix).trim()

        if (message.isEmpty()) {
            subject.sendMessage("请输入广播内容!")
            return
        }

        if (user.id != 3132522039L) {
            subject.sendMessage("你没有权限发送广播")
            return
        }

        BroadCastManager.sendBroadCast(subject, message)


        val replyMessage = MessageChainBuilder();

        replyMessage.add(PlainText("广播开始发送!\n"))



        subject.sendMessage(replyMessage.build())
    }
}

object BroadCastManager : CompositeCommand(AronaTools.INSTANCE, "broadcastManager", "广播管理", "全局广播管理", "全局消息管理", "发送公告管理", description = "全局广播管理") {
    @SubCommand("获取", "获取广播", "获取广播列表")
    fun getBroadCast(context: CommandContext) {
        val subject = context.sender.subject!!

        BroadCastManager.BroadCastList(subject, context.originalMessage)


    }

/*    @SubCommand("删除", "删除广播", "删除广播列表")
    suspend fun deleteBroadCast(context: CommandContext, id: Int) {
        val subject = context.sender.subject!!

        val broadCast = HibernateFactory.select(cn.travellerr.aronaTools.broadcast.BroadCast::class.java, id)

        if (broadCast == null) {
            subject.sendMessage("广播不存在")
            return
        }

        broadCast.delete()

        subject.sendMessage("广播已删除")
    }*/
}