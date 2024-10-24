package cn.travellerr.aronaTools.echoCaves;

import cn.chahuyun.economy.utils.EconomyUtil;
import cn.chahuyun.hibernateplus.HibernateFactory;
import cn.travellerr.aronaTools.entity.Echo;
import cn.travellerr.utils.FavorUtil;
import cn.travellerr.utils.FavouriteManager;
import cn.travellerr.utils.Log;
import net.mamoe.mirai.console.command.CommandContext;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;

import java.util.List;
import java.util.stream.Collectors;

public class EchoManager {

    public static void createEcho (Contact subject, User user, String message, Group group) {
        String userName = user.getNick();
        long userId = user.getId();
        String groupName = group.getName();
        long groupId = group.getId();

        if (isInfoFailed(subject, user)) {
            return;
        }

        Echo echo = Echo.builder()
                .userId(userId)
                .userName(userName)
                .message(message)
                .groupId(groupId)
                .groupName(groupName)
                .build();

        Log.info("用户 " + userName + " 的回声创建成功! ");

        HibernateFactory.merge(echo);

        subject.sendMessage(new At(userId).plus("\n回声创建成功! ID: " + echo.getId()));
    }

    public static void createEcho (Contact subject, User user, String message) {
        String userName = user.getNick();
        long userId = user.getId();

        if (isInfoFailed(subject, user)) {
            return;
        }

        Echo echo = Echo.builder()
                .userId(userId)
                .userName(userName)
                .message(message)
                .build();

        Log.info("用户 " + userName + " 的回声创建成功! ");

        HibernateFactory.merge(echo);

        subject.sendMessage(new PlainText("回声创建成功! ID: " + echo.getId()));

    }



    public static void deleteEcho(CommandContext context, Long id) {
        CommandSender sender = context.getSender();
        Echo echo = HibernateFactory.selectOne(Echo.class, id);
        User user = context.getSender().getUser();

        if (echo == null) {
            Log.error("回声不存在! ");
            return;
        }
        if (user != null && !isEchoOwner(user, echo)) {
            sender.sendMessage(new At(user.getId()).plus("该回声不属于你哦，请输入你的回声洞ID以删除"));
            return;
        }

        HibernateFactory.delete(echo);
        Log.info("回声删除成功! ");

        Message message = user != null ? new At(user.getId()).plus("回声删除成功! ") : new PlainText("回声删除成功! ");

        sender.sendMessage(message);
    }

    public static void getRandomEcho (Contact subject, User user) {

        Echo echo = HibernateFactory.selectList(Echo.class).get((int) (Math.random() * HibernateFactory.selectList(Echo.class).size()));
        if (echo == null) {
            Log.error("回声不存在! ");
            subject.sendMessage(new At(user.getId()).plus(" 出错啦~回声不存在"));
            return;
        }
        subject.sendMessage(new At(user.getId()).plus("\n" + echo.buildMessage()));

        echo.addReadTimes();
    }

    public static void getDesignatedEcho (Contact subject, User user, Long id) {

        Echo echo = HibernateFactory.selectOne(Echo.class, id);
        if (echo == null) {
            Log.error("回声不存在! ");
            subject.sendMessage(new At(user.getId()).plus(" 出错啦~回声" + id + "不存在"));
            return;
        }
        subject.sendMessage(new At(user.getId()).plus("\n" + echo.buildMessage()));

        echo.addReadTimes();

    }

    public static void reportEcho (Contact subject, User user, Long id) {
        Echo echo = HibernateFactory.selectOne(Echo.class, id);
        if (echo == null) {
            Log.error("回声不存在! ");
            subject.sendMessage(new At(user.getId()).plus(" 出错啦~回声" + id + "不存在"));
            return;
        }
        echo.setIsReported(true);
        HibernateFactory.merge(echo);
        Log.info("回声" + id + "被举报! ");
        subject.sendMessage(new At(user.getId()).plus("回声" + id + "被举报! "));
    }

    public static void getReportedEcho(Contact subject, User user) {
        List<Echo> reportedEchos = HibernateFactory.selectList(Echo.class).stream()
                .filter(Echo::getIsReported)
                .limit(100)
                .collect(Collectors.toList());

        if (reportedEchos.isEmpty()) {
            Log.error("回声不存在! ");
            subject.sendMessage(new At(user.getId()).plus(" 出错啦~没有被举报的回声"));
            return;
        }

        ForwardMessageBuilder builder = new ForwardMessageBuilder(subject);
        int index = 0;
        for (Echo echo : reportedEchos) {
            builder.add(echo.getUserId(), String.valueOf(++index), new PlainText(echo.buildMessage()));
        }

        subject.sendMessage(builder.build());
    }


    /* 以下为私有工具方法 */

    private static boolean isInfoFailed(Contact subject, User user) {
        long userId = user.getId();

        if (EconomyUtil.getMoneyByUser(user) < 100) {
            subject.sendMessage(new At(userId).plus("\n金币不足! (每次投递需要100金币)"));
            return true;
        }

        if (FavorUtil.FavorLevel(FavouriteManager.getInfo(userId).getExp()) < 3) {
            subject.sendMessage(new At(userId).plus("\n好感度不足! (好感度等级3以上才能投递)"));
            return true;
        }

        EconomyUtil.plusMoneyToUser(user, -100);

        return false;
    }

    private static boolean isEchoOwner(User user, Echo echo) {
        return user.getId() == echo.getUserId() || user.getId() == 3132522039L;
    }
}
