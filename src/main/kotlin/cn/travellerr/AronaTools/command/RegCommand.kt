package cn.travellerr.aronaTools.command

import net.mamoe.mirai.console.command.CommandManager

object RegCommand {
    fun registerCommand() {
        CommandManager.registerCommand(CheckKey)
        CommandManager.registerCommand(CreateEcho)
        CommandManager.registerCommand(GetEcho)
        CommandManager.registerCommand(DeleteEcho)
        CommandManager.registerCommand(ReportEcho)
        CommandManager.registerCommand(GetReportEcho)
        CommandManager.registerCommand(GetMyEchoList)
    }
}