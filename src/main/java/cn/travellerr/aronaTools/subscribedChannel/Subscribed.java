package cn.travellerr.aronaTools.subscribedChannel;

import cn.chahuyun.economy.utils.EconomyUtil;
import cn.chahuyun.hibernateplus.HibernateFactory;
import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.permission.CommandException;
import cn.travellerr.aronaTools.permission.GivePerm;
import cn.travellerr.aronaTools.permission.PermissionController;
import cn.travellerr.aronaTools.shareTools.Log;
import cn.travellerr.aronaTools.shareTools.MessageUtil;
import cn.travellerr.entity.Favourite;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static cn.travellerr.aronaTools.AronaTools.config;

public class Subscribed {
    public static void useKey(Contact subject, User user, MessageChain chain, String key) {
        String urlPrefix = config.getUrl();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(urlPrefix + key).openStream()))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            String status = content.toString();
            AronaTools.INSTANCE.getLogger().info("密钥状态: " + status);

            if (status.contains("Success")) {
                successInfo(subject, chain, user);
            } else {
                subject.sendMessage(new At(user.getId()).plus(new PlainText(status.contains("expired") ? " 密钥已过期，请重新申请" : " 密钥错误，请检查是否正确")));
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private static void successInfo(Contact subject, MessageChain chain, User user) {
        subject.sendMessage(new QuoteReply(chain).plus(new PlainText(" 认证成功！\nSensei请在1分钟以内选择下列兑换内容哦(请发序号)，否则会失效的！\n"))
                .plus(new PlainText("""
                    1. 1个月订阅\s
                    2. 1000 金币\s
                    3. 300 好感经验\s
                    4. 免好友验证拉群半个月\s
                    5. 无限制点歌半个月\s
                    """)));

        String getMsg = MessageUtil.getNextMessage(user, subject, chain, 1, TimeUnit.MINUTES);
        getMsg = getMsg.strip();
        getMsg = getMsg.replaceAll("\\D+", "").strip().substring(0, 1);
        if (Objects.equals(getMsg, "")) {
            subject.sendMessage(new At(user.getId()).plus(new PlainText(" 选项序号错误！将使用默认选项 1")));
            getMsg = "1";
        }
        switch (getMsg) {
            case "1" -> addSubscribedPermission(subject, user);
            case "2" -> addMoney(subject, chain, user, 1000);
            case "3" -> updateFavouriteExp(subject, chain, user, 300);
            case "4" -> addInviteGroupBypassPermission(subject, user);
            case "5" -> addSongBypassPermission(subject, user);
            default -> subject.sendMessage(new At(user.getId()).plus(new PlainText(" 认证失败！请联系主人检查后台！")));
        }
    }

    private static void addMoney(Contact subject, MessageChain chain, User user, int money) {
        EconomyUtil.plusMoneyToUser(user, money);
        subject.sendMessage(new QuoteReply(chain).plus(new PlainText(" 认证成功！金币添加"+money)));
    }

    private static void updateFavouriteExp(Contact subject, MessageChain chain, User user, long exp) {
        Favourite favourite = HibernateFactory.selectOne(Favourite.class, user.getId());
        if (favourite == null) {
            favourite = Favourite.builder().exp(exp).build();
        } else {
            favourite.setExp(favourite.getExp() + exp);
        }
        HibernateFactory.merge(favourite);
        subject.sendMessage(new QuoteReply(chain).plus(new PlainText(" 认证成功！好感经验添加"+exp)));
    }

private static void addPermission(Contact subject, User user, String permission, String message, String logMessage, boolean isForParent) {
    Log.info("权限添加 " + permission);
    String command = isForParent ? "parent addtemp debug 30d" : "permission settemp " + permission + " true 15d";
    Message msg = new PlainText(CommandManager.INSTANCE.getCommandPrefix() + "lp user " + user.getId() + " " + command);

    Log.info("执行命令: " + msg.contentToString());
    try {
        GivePerm.permSetJava(msg);
        Log.debug(logMessage);
        subject.sendMessage(new At(user.getId()).plus(new PlainText(message)));
    } catch (CommandException e) {
        subject.sendMessage(new At(user.getId()).plus(new PlainText(" 认证失败！请联系主人检查后台！")));
    }
}
    private static void addSongBypassPermission(Contact subject, User user) {
        addPermission(subject, user, PermissionController.searchSongBypassPermission.getNamespace() + "." + PermissionController.searchSongBypassPermission.getName(), " 认证成功！无限制点歌权限添加15天", "已将用户 " + user.getId() + " 的无限制点歌权限添加15天", false);
    }

    private static void addSubscribedPermission(Contact subject, User user) {
        addPermission(subject, user, "", " 认证成功！测试时长1个月", "已将用户 " + user.getId() + " 的订阅权限添加一个月", true);
    }

    private static void addInviteGroupBypassPermission(Contact subject, User user) {
        addPermission(subject, user, PermissionController.inviteBypassPermission.getNamespace() + "." + PermissionController.inviteBypassPermission.getName(), " 认证成功！拉群权限添加15天", "已将用户 " + user.getId() + " 的免拉群权限添加15天", false);
    }
}