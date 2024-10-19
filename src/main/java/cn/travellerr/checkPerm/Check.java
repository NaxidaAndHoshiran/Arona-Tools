package cn.travellerr.checkPerm;

import cn.chahuyun.economy.utils.EconomyUtil;
import cn.travellerr.utils.SqlUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.data.RequestEventData;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.message.data.At;

import java.util.ArrayList;
import java.util.Objects;

public class Check {
    private static boolean checkMoney(User user) {
        return EconomyUtil.getMoneyByUser(user) >= 300;
    }

    private static boolean checkLove(User user) {
        SqlUtil.getExp(user.getId());
        return SqlUtil.getExp(user.getId()) >= 200;
    }

    private static ArrayList<Boolean> checkIsInGroup(Bot bot, User user, Group ARONA, Group Hoshiran) {
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
        User user = bot.getStranger(userId) == null ? bot.getFriend(userId) : null;
        assert user != null;
        boolean isMoneyEnough = checkMoney(user);
        boolean isLoveEnough = checkLove(user);

        Group ARONA = bot.getGroup(626860767L);
        Group Hoshiran = bot.getGroup(970271300L);

        assert ARONA != null && Hoshiran != null;

        ArrayList<Boolean> isInOneOfGroup = checkIsInGroup(bot, user, ARONA, Hoshiran);

        boolean isInGroup = isInOneOfGroup.get(0) || isInOneOfGroup.get(1);

        boolean isAccept = false;
        if (isMoneyEnough && isLoveEnough && isInGroup) {
            EconomyUtil.plusMoneyToUser(user, -300);
            event.accept();
            isAccept = true;
        } else {
            event.ignore();
        }

        User owner = bot.getFriend(3132522039L);
        assert owner != null;

        String BotVerifyMsg = (isAccept ? "通过" : ("未通过" + "审核"
                + "\n" + "原因: " + (isMoneyEnough ? "" : "余额不足 ") + (isLoveEnough ? "" : "好感不足 ") + (isInGroup ? "" : "不在群聊中 ")
                + "未通过审核"));

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
}
