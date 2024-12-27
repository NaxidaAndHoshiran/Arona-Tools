package cn.travellerr.aronaTools.autoAcceptInvite;

import cn.chahuyun.economy.utils.EconomyUtil;
import cn.travellerr.aronaTools.permission.PermissionController;
import cn.travellerr.aronaTools.shareTools.Log;
import cn.travellerr.entity.Favourite;
import cn.travellerr.utils.FavouriteManager;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;

import java.util.ArrayList;
import java.util.List;

import static cn.travellerr.aronaTools.AronaTools.config;

public class CheckInvite {

    private static boolean checkMoney(User user) {
        return EconomyUtil.getMoneyByUser(user) >= config.getMoney();
    }

    private static boolean checkLove(long user) {
        try {
            Favourite favourite = FavouriteManager.getInfo(user);
            return favourite != null && favourite.getExp() >= config.getLove();
        } catch (Exception e) {
            return false;
        }
    }

    private static List<Boolean> checkIsInGroup(long user, Group... groups) {
        List<Boolean> result = new ArrayList<>();
        for (Group group : groups) {
            result.add(group.get(user) != null);
        }
        return result;
    }

    public static void check(BotInvitedJoinGroupRequestEvent event) {
        long userId = event.getInvitorId();
        Bot bot = event.getBot();
        User owner = bot.getFriend(3132522039L);
        assert owner != null;

        User user = bot.getFriend(userId);
        if (user == null) {
            user = bot.getStranger(userId);
            notifyOwnerAndUser(owner, user, event, "请先添加我为好友再邀请我加入群聊哦~");
            return;
        }

        if (PermissionController.hasPermission(user, PermissionController.addFriendBypassPermission)) {
            acceptInvite(event, owner, user);
            return;
        }

        boolean isMoneyEnough = checkMoney(user);
        List<Boolean> isInOneOfGroup = checkIsInGroup(userId, getGroups(bot));
        boolean isInGroup = isInOneOfGroup.contains(true);

        if (isMoneyEnough && isInGroup) {
            EconomyUtil.plusMoneyToUser(user, -config.getMoney());
            event.accept();
            notifyOwnerAndUser(owner, user, event, "通过");
        } else {
            event.ignore();
            notifyOwnerAndUser(owner, user, event, "未通过审核\n请尝试以下方法: \n" + getTryInviteGroupMsg(isMoneyEnough, isInGroup, user));
        }
    }

    public static void checkFriendRequest(NewFriendRequestEvent event) {
        long userId = event.getFromId();
        Bot bot = event.getBot();
        User owner = bot.getFriend(3132522039L);
        assert owner != null;


        if (PermissionController.hasPermission(userId, PermissionController.inviteBypassPermission)) {
            acceptFriendRequest(event, owner);
            return;
        }

        boolean isLoveEnough = checkLove(userId);
        List<Boolean> isInOneOfGroup = checkIsInGroup(userId, getGroups(bot));
        boolean isInGroup = isInOneOfGroup.contains(true);

        if (isLoveEnough && isInGroup) {
            event.accept();
            notifyOwnerAndUser(owner, event, "通过");
        } else {
            event.reject(false);
            notifyOwnerAndUser(owner, event, "未通过审核\n请尝试以下方法: \n" + getTryAddFriendMsg(isLoveEnough, isInGroup, userId));
        }
    }

    private static Group[] getGroups(Bot bot) {
        return new Group[]{bot.getGroup(config.getGroup().get(0)), bot.getGroup(config.getGroup().get(1))};
    }



    private static void acceptInvite(BotInvitedJoinGroupRequestEvent event, User owner, User user) {
        event.accept();
        notifyOwnerAndUser(owner, user, event, "通过");
    }

    private static void acceptFriendRequest(NewFriendRequestEvent event, User owner) {
        event.accept();
        notifyOwnerAndUser(owner, event, "通过");
    }

    private static void notifyOwnerAndUser(User owner, User user, BotInvitedJoinGroupRequestEvent event, String message) {
        String msg = "用户 " + event.getInvitorNick() + " (" + event.getInvitorId() + ") 邀请" + event.getBot().getNick() + "加入群聊" + event.getGroupName() + " (" + event.getGroupId() + ") " + "\n" + message;
        Log.info(msg);
        owner.sendMessage(msg);
        if (user != null) user.sendMessage(msg);
    }

    private static void notifyOwnerAndUser(User owner, NewFriendRequestEvent event, String message) {
        String msg = "用户 " + event.getFromNick() + " (" + event.getFromId() + ") 申请添加" + event.getBot().getNick() + "为好友\n" + message;
        Log.info(msg);
        owner.sendMessage(msg);
        User user = event.getBot().getFriend(event.getFromId());
        if (user == null) {
            for (Group group : getGroups(event.getBot())) {
                user = group.get(event.getFromId());
                if (user != null) {
                    group.sendMessage(msg);
                    break;
                }
            }
        }
        if (user != null) user.sendMessage(msg);
    }

    private static String getTryInviteGroupMsg(boolean isEnough, boolean isInGroup, User user) {
        String moneyMsg = "金币数量(使用#个人信息 查看) >= 300 (目前 %金币%)";
        String groupMsg = "尝试加入 什亭之匣群聊(626860767)";
        return (isEnough ? "" : (replaceVar(moneyMsg, "金币", EconomyUtil.getMoneyByUser(user)) + "\n"))
                + (isInGroup ? "" : (groupMsg + "\n"));
    }

    private static String getTryAddFriendMsg(boolean isLoveEnough, boolean isInGroup, long userId) {
        String loveMsg = "好感经验(使用#好感度 查看) >= " + config.getLove() + " (目前 %好感度% 经验)";
        String groupMsg = "尝试加入 什亭之匣群聊(626860767)";
        Favourite favourite = FavouriteManager.getInfo(userId);
        if (favourite == null) {
            favourite = Favourite.builder().exp(0L).build();
        }
        return (isLoveEnough ? "" : (replaceVar(loveMsg, "好感度", favourite.getExp()) + "\n"))
                + (isInGroup ? "" : (groupMsg + "\n"));
    }

    public static String replaceVar(String msg, String varName, Object value) {
        return msg.replace("%" + varName + "%", value.toString());
    }
}