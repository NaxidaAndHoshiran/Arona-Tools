package cn.travellerr.aronaTools.permission

import cn.travellerr.aronaTools.AronaTools
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.console.command.CommandExecuteResult
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.ConsoleCommandSender
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.command.isFailure
import net.mamoe.mirai.message.data.Message


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