package cn.travellerr.aronaTools.autoAcceptInvite;

import cn.travellerr.aronaTools.AronaTools;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.BotJoinGroupEvent;
import net.mamoe.mirai.event.events.FriendAddEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.io.IOException;

public class SendMenu {
    public static void sendMenuToFriend(FriendAddEvent event) {
        // 启用QQ
        if (event.getBot().getId() == 896603204) {
            Contact subject = event.getUser();

            // 菜单图片路径（相对路径）
            File menu = AronaTools.INSTANCE.resolveDataFile(AronaTools.menuConfig.getMenus().get(0));
            try (ExternalResource resource = ExternalResource.create(menu)) {
                Image image = subject.uploadImage(resource);

                // 发送消息
                subject.sendMessage(new PlainText(event.getUser().getNick()
                        + """
                        Sensei您好！
                        欢迎使用由 Travellerr 制作的 "蔚蓝档案-阿洛娜"机器人！
                        请在使用之前仔细查看菜单！
                        """)
                        .plus(image));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void sendMenuToGroup(BotJoinGroupEvent event) {

        // 启用QQ
        if (event.getBot().getId() == 896603204) {
            Contact subject = event.getGroup();

            // 菜单图片路径（相对路径）
            File menu = AronaTools.INSTANCE.resolveDataFile(AronaTools.menuConfig.getMenus().get(0));
            try (ExternalResource resource = ExternalResource.create(menu)) {
                Image image = subject.uploadImage(resource);

                // 发送消息
                subject.sendMessage(new PlainText("来自 "
                        + event.getGroup().getName()
                        + """
                         的Sensei们好！
                         欢迎使用由 Travellerr 制作的 "蔚蓝档案-阿洛娜"机器人！
                         请在使用之前仔细查看菜单！
                         若想订阅本群公告，请发送 "#添加公告"。
                         请注意，拉入本阿洛娜即视为同意以下内容: https://doc.hoshiran.tech/documentation/eula.html
                         """
                    )
                        .plus(image));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
