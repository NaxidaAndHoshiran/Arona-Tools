package cn.travellerr.aronaTools.broadcast.command;

import cn.travellerr.aronaTools.broadcast.BroadCastManager;
import cn.travellerr.aronaTools.shareTools.BuildCommand;
import kotlin.text.Regex;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

public class BroadCastCommandListener extends SimpleListenerHost {
    private final Regex addGroup = BuildCommand.createCommand("添加群广播|添加群绑定|添加广播|添加公告|添加群公告|绑定公告|绑定群公告|绑定群聊|添加群聊");
    private final Regex removeGroup = BuildCommand.createCommand("移除群广播|移除群绑定|移除广播|移除公告|移除群公告|解绑公告|解绑群公告");
    private final Regex turnOnUrlApply = BuildCommand.createCommand("打开链接|开启链接|打开发送链接|打开链接发送|开启发送链接|开启链接发送");
    private final Regex turnOffUrlApply = BuildCommand.createCommand("关闭链接|关闭发送链接|关闭链接发送");


    @EventHandler
    public void onMessage(GroupMessageEvent event) {
        MessageChain message = event.getMessage();
        Contact subject = event.getSubject();

        if (addGroup.containsMatchIn(message.contentToString())) {
            BroadCastManager.addGroupBroadCastInfo(subject, message, event.getSender(), event.getGroup());
        } else if (removeGroup.containsMatchIn(message.contentToString())) {
            BroadCastManager.removeGroupBroadCastInfo(subject, message, event.getSender(), event.getGroup());
        } else if (turnOnUrlApply.containsMatchIn(message.contentToString())) {
            BroadCastManager.turnOnUrlApply(subject, message, event.getSender(), event.getGroup());
        } else if (turnOffUrlApply.containsMatchIn(message.contentToString())) {
            BroadCastManager.turnOffUrlApply(subject, message, event.getSender(), event.getGroup());
        }

    }
}
