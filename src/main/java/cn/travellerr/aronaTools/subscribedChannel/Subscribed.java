package cn.travellerr.aronaTools.subscribedChannel;

import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.command.CommandException;
import cn.travellerr.aronaTools.command.GivePerm;
import cn.travellerr.aronaTools.permission.PermissionController;
import net.mamoe.mirai.console.command.*;
import net.mamoe.mirai.console.permission.*;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

import static cn.travellerr.aronaTools.AronaTools.config;


public class Subscribed {
    public static void useKey(Contact subject, User user, String key) {

        String urlPrefix = config.getUrl();

        try {

            URL url = new URL(urlPrefix + key);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
                String status = content.toString();
                AronaTools.INSTANCE.getLogger().info("密钥状态: " + status);


                if (status.contains("Success")) {
                    successInfo(subject, user);
                } else if (status.contains("expired")) {
                    subject.sendMessage(new At(user.getId())
                            .plus(new PlainText(" 密钥已过期，请重新申请")));
                } else {
                    subject.sendMessage(new At(user.getId())
                            .plus(new PlainText(" 密钥错误，请检查是否正确")));

                }
            }

        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private static void successInfo(Contact subject, User user) {
        AronaTools.INSTANCE.getLogger().info("用户 " + user.getId() + " 订阅认证成功");
        AronaTools.INSTANCE.getLogger().info("权限添加 "
                + Objects.requireNonNull(PermissionService.getInstance().get(PermissionController.subscribedPermission)).getId());
        Message msg = new PlainText(CommandManager.INSTANCE.getCommandPrefix() +"lp user " + user.getId() + " parent addtemp debug 30d");
        //Message msg = new PlainText("#status");
        AronaTools.INSTANCE.getLogger().debug("生成的指令"+ msg);

        try {
            GivePerm.permSetJava(msg);

            AronaTools.INSTANCE.getLogger().debug("已将用户 " + user.getId() + " 的订阅权限添加一个月");
            subject.sendMessage(new At(user.getId())
                    .plus(new PlainText(" 认证成功！测试时长1个月")));
        } catch (CommandException e) {
            subject.sendMessage(new At(user.getId())
                    .plus(new PlainText(" 认证失败！请联系主人检查后台！")));
        }

    }
}

