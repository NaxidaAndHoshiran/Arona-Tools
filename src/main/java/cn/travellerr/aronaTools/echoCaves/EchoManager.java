package cn.travellerr.aronaTools.echoCaves;

import cn.chahuyun.economy.utils.EconomyUtil;
import cn.chahuyun.hibernateplus.HibernateFactory;
import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.entity.Echo;
import cn.travellerr.utils.FavorUtil;
import cn.travellerr.utils.FavouriteManager;
import cn.travellerr.utils.Log;
import net.mamoe.mirai.console.command.CommandContext;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.*;

import java.util.List;
import java.util.stream.Collectors;

public class EchoManager {

    /**
     * 创建回声并保存到数据库。
     *
     * @param subject 发送消息的对象
     * @param user 用户对象
     * @param message 回声内容
     * @param group 群聊对象
     * @author Travellerr
     */
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

        echo = HibernateFactory.merge(echo);

        subject.sendMessage(new At(userId).plus("\n回声创建成功! ID: " + echo.getId()));
    }

    /**
     * 创建回声并保存到数据库（不指定群组）。
     *
     * @param subject 发送消息的对象
     * @param user 用户对象
     * @param message 回声内容
     * @author Travellerr
     */
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

    /**
     * 删除指定 ID 的回声。
     *
     * @param context 命令上下文
     * @param id 回声 ID
     * @author Travellerr
     */
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

    /**
     * 获取随机回声并发送。
     *
     * @param subject 发送消息的对象
     * @param user 用户对象
     * @param originalMessage 原始消息链
     * @author Travellerr
     */
    public static void getRandomEcho (Contact subject, User user, MessageChain originalMessage) {

        Echo echo = HibernateFactory.selectList(Echo.class).get((int) (Math.random() * HibernateFactory.selectList(Echo.class).size()));
        if (echo == null) {
            Log.error("回声不存在! ");
            subject.sendMessage(new At(user.getId()).plus(" 出错啦~回声不存在"));
            return;
        }

        echo.addReadTimes();
        subject.sendMessage(new QuoteReply(originalMessage).plus("\n" + echo.buildMessage()));
    }

    /**
     * 获取指定 ID 的回声并发送。
     *
     * @param subject 发送消息的对象
     * @param user 用户对象
     * @param id 回声 ID
     * @param originalMessage 原始消息链
     * @author Travellerr
     */
    public static void getDesignatedEcho (Contact subject, User user, Long id, MessageChain originalMessage) {

        Echo echo = HibernateFactory.selectOne(Echo.class, id);
        if (echo == null) {
            Log.error("回声不存在! ");
            subject.sendMessage(new At(user.getId()).plus(" 出错啦~回声" + id + "不存在"));
            return;
        }

        echo.addReadTimes();
        subject.sendMessage(new QuoteReply(originalMessage).plus("\n" + echo.buildMessage()));
    }

    /**
     * 举报指定 ID 的回声。
     *
     * @param subject 发送消息的对象
     * @param user 用户对象
     * @param id 回声 ID
     * @author Travellerr
     */
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

    /**
     * 获取所有被举报的回声并发送。
     *
     * @param subject 发送消息的对象
     * @param user 用户对象
     * @author Travellerr
     */
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

    /**
     * 获取用户的回声列表并发送。
     *
     * @param subject 发送消息的对象
     * @param user 用户对象
     * @author Travellerr
     */
    public static void getMyEchoList(Contact subject, User user) {
        List<Long> echos = HibernateFactory.selectList(Echo.class).stream()
                .limit(100)
                .filter(echo -> echo.getUserId() == user.getId())
                .map(Echo::getId)
                .collect(Collectors.toList());

        if (echos.isEmpty()) {
            Log.error("回声不存在! ");
            subject.sendMessage(new At(user.getId()).plus(" 出错啦~你还没有回声"));
            return;
        }

        Message message = new At(user.getId()).plus("\n你的回声列表(ID): \n" + echos);

        subject.sendMessage(message);
    }

    /* 以下为私有工具方法 */

    /**
     * 检查用户信息是否符合创建回声的条件。
     *
     * @param subject 发送消息的对象
     * @param user 用户对象
     * @return 如果信息不符合条件，返回 true；否则返回 false
     */
    private static boolean isInfoFailed(Contact subject, User user) {
        long userId = user.getId();

        int needMoney = AronaTools.config.getEchoMoney();
        int needFavorLevel = AronaTools.config.getEchoFavorLevel();

        if (EconomyUtil.getMoneyByUser(user) < needMoney) {
            subject.sendMessage(new At(userId).plus(String.format("\n金币不足! (每次投递需要%d金币)", needMoney)));
            return true;
        }

        if (FavorUtil.FavorLevel(FavouriteManager.getInfo(userId).getExp()) < AronaTools.config.getEchoFavorLevel()) {
            subject.sendMessage(new At(userId).plus(String.format("\n好感度不足! (好感度等级%d以上才能投递)", needFavorLevel)));
            return true;
        }

        EconomyUtil.plusMoneyToUser(user, -needMoney);

        return false;
    }

    /**
     * 检查用户是否为回声的所有者。
     *
     * @param user 用户对象
     * @param echo 回声对象
     * @return 如果用户是回声的所有者，返回 true；否则返回 false
     */
    private static boolean isEchoOwner(User user, Echo echo) {
        return user.getId() == echo.getUserId() || user.getId() == 3132522039L;
    }
}
