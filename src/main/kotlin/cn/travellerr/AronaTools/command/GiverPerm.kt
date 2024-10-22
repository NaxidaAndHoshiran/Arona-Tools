package cn.travellerr.aronaTools.command

import cn.travellerr.aronaTools.AronaTools
import cn.travellerr.aronaTools.subscribedChannel.Subscribed
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.permission.PermitteeId.Companion.permitteeId
import net.mamoe.mirai.message.data.Message

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

object GivePerm {

    @OptIn(ExperimentalCommandDescriptors::class)
    @JvmStatic
    suspend fun permSet(msg: Message): CommandExecuteResult {
        AronaTools.INSTANCE.logger.debug("kotlin转化指令$msg")
        return CommandManager.executeCommand(ConsoleCommandSender, msg, false)
    }

    @OptIn(ExperimentalCommandDescriptors::class)
    @JvmStatic
    fun permSetJava(msg: Message) {
        runBlocking {
            val result = permSet(msg)
            if (result.isFailure()) {
                AronaTools.INSTANCE.logger.error("指令执行失败")
                throw CommandException()
            }
            AronaTools.INSTANCE.logger.info("指令执行成功")
        }
    }
}

class CommandException : IllegalArgumentException("error")