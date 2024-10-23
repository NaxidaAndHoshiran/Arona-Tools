package cn.travellerr.aronaTools.autoAcceptInvite;

import cn.chahuyun.economy.utils.EconomyUtil;
import cn.travellerr.aronaTools.permission.PermissionController;
import cn.travellerr.utils.SqlUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.permission.AbstractPermitteeId;
import net.mamoe.mirai.console.permission.PermissionService;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;

import java.util.ArrayList;
import java.util.Objects;

import static cn.travellerr.aronaTools.AronaTools.config;

public class CheckInvite {
    private static boolean checkMoney(User user) {
        return EconomyUtil.getMoneyByUser(user) >= config.getMoney();
    }

    private static boolean checkLove(User user) {
        try {
            SqlUtil.getExp(user.getId());
            return SqlUtil.getExp(user.getId()) >= config.getLove();
        } catch (Exception e) {
            return false;
        }
    }

    private static ArrayList<Boolean> checkIsInGroup(User user, Group ARONA, Group Hoshiran) {
        ArrayList<Boolean> result = new ArrayList<>();
        result.add(false);
        result.add(false);

        NormalMember inArona = ARONA.get(user.getId());
        NormalMember inHoshiran = Hoshiran.get(user.getId());

        if (inArona != null) {
            result.set(0, true);
        }

        if (inHoshiran != null) {
            result.set(1, true);
        }

        return result;
    }

    public static void check(BotInvitedJoinGroupRequestEvent event) {
        long userId = event.getInvitorId();
        Bot bot = event.getBot();
        User user = bot.getStranger(userId);
        boolean isAccept = false;
        if (user == null) {
            user = bot.getFriend(userId);
        }
        assert user != null;

        User owner = bot.getFriend(3132522039L);
        assert owner != null;

        AbstractPermitteeId.ExactUser exactUser = PermissionController.getUserPermittee(user);
        if (exactUser != null && PermissionService.hasPermission(exactUser, PermissionController.inviteBypassPermission)) {
            event.accept();

            owner.sendMessage("用户 " + user.getNick() + " (" + user.getId() + ") 邀请" + bot.getNick() +
                    "加入群聊" + event.getGroupName() + " (" + event.getGroupId() + ") " + "\n");

            user.sendMessage("你邀请" + bot.getNick() +
                    "加入群聊" + event.getGroupName() + " (" + event.getGroupId() + ") " + "\n");
            return;

        }
        boolean isMoneyEnough = checkMoney(user);
        boolean isLoveEnough = checkLove(user);

        Group ARONA = bot.getGroup(config.getGroup().get(0));
        Group Hoshiran = bot.getGroup(config.getGroup().get(1));

        assert ARONA != null && Hoshiran != null;

        ArrayList<Boolean> isInOneOfGroup = checkIsInGroup(user, ARONA, Hoshiran);

        boolean isInGroup = isInOneOfGroup.get(0) || isInOneOfGroup.get(1);


        if (isMoneyEnough && isLoveEnough && isInGroup) {
            EconomyUtil.plusMoneyToUser(user, -config.getMoney());
            event.accept();
            isAccept = true;
        } else {
            event.ignore();

        }


        String moneyMsg = "金币数量(使用#个人信息 查看) >= 300 (目前 %金币%)";

        String loveMsg = "好感经验(使用#好感度 查看) >= 380 (目前 %好感度% 经验)";

        String groupMsg = "尝试加入 什亭之匣群聊(626860767)";

        String tryMsg = (isMoneyEnough ? "" : (replaceVar(moneyMsg, "金币", EconomyUtil.getMoneyByUser(user)) + "\n"))
                + (isLoveEnough ? "" : (replaceVar(loveMsg, "好感度", SqlUtil.getExp(userId)) + "\n"))
                + (isInGroup ? "" : (groupMsg + "\n"));

        String BotVerifyMsg = (isAccept ? "通过" : ("未通过" + "审核"
                + "\n" + "原因: " + (isMoneyEnough ? "" : "余额不足 ") + (isLoveEnough ? "" : "好感不足 ") + (isInGroup ? "" : "不在群聊中 ")
                + "未通过审核\n请尝试以下方法: \n" + tryMsg));

        owner.sendMessage("用户 " + user.getNick() + " (" + user.getId() + ") 邀请" + bot.getNick() +
                "加入群聊" + event.getGroupName() + " (" + event.getGroupId() + ") " + "\n"
                + BotVerifyMsg);

        user.sendMessage("你邀请" + bot.getNick() +
                "加入群聊" + event.getGroupName() + " (" + event.getGroupId() + ") " + "\n"
                + BotVerifyMsg);

        Group group = isInOneOfGroup.get(0) ? ARONA : (isInOneOfGroup.get(1) ? Hoshiran : null);

        Objects.requireNonNullElse(group, Hoshiran).sendMessage("用户 " + user.getNick() + " (" + user.getId() + ") 邀请" + bot.getNick() +
                "加入群聊\n"
                + BotVerifyMsg);
    }

    public static String replaceVar(String msg, String varName, Object value) {
        return msg.replace("%" + varName + "%", value.toString());
    }
}
