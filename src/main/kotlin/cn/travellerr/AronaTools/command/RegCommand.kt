package cn.travellerr.aronaTools.command

import net.mamoe.mirai.console.command.CommandManager

object RegCommand {
    fun registerCommand() {
        CommandManager.registerCommand(CheckKey)
        CommandManager.registerCommand(CreateEcho)
        CommandManager.registerCommand(GetEcho)
        CommandManager.registerCommand(DeleteEcho)
        CommandManager.registerCommand(ReportEcho)
        CommandManager.registerCommand(GetMyEchoList)
        CommandManager.registerCommand(VerifyEcho)
        CommandManager.registerCommand(VerifyPetTaskWorkShop)
        CommandManager.registerCommand(VerifyPetItemWorkShop)
        CommandManager.registerCommand(Tester)
        CommandManager.registerCommand(Pet)
        CommandManager.registerCommand(BroadCast)
        CommandManager.registerCommand(BroadCastManager)
        CommandManager.registerCommand(Totp)
    }
}