package cn.travellerr.aronaTools.components;

import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.shareTools.Log;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.utils.ExternalResource;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class MenuListener extends SimpleListenerHost {
    @EventHandler
    public void onMessage(@NotNull MessageEvent event) {
        String message = event.getMessage().contentToString();
        Contact subject = event.getSubject();
        MessageChain messageChain = event.getMessage();

        int index = -1;

        for (String command : AronaTools.menuConfig.getCommands()) {
            if (message.matches(command)) {
                Log.info("匹配到菜单命令：" + command);
                index = AronaTools.menuConfig.getCommands().indexOf(command);
                break;
            }
        }

        if (index == -1) return;

        if (!AronaTools.INSTANCE.getDataFolderPath().resolve("menu").toFile().exists()) {
            Log.error("菜单文件夹不存在,正在创建……");
            Path.of(AronaTools.INSTANCE.getDataHolderName(), "menu").toFile().mkdir();
        }


        try (ExternalResource externalResource = ExternalResource.create(AronaTools.INSTANCE.getDataFolderPath().resolve(AronaTools.menuConfig.getMenus().get(index)).toFile())
        ) {
            Image image = subject.uploadImage(externalResource);

            subject.sendMessage(image);

        } catch (Exception e) {
            Log.error("出错啦~", e);
            subject.sendMessage(new QuoteReply(messageChain).plus("出错啦~,请联系主人检查后台哦"));
        }


    }
}
