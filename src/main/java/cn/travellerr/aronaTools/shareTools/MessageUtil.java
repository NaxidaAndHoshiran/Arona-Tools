package cn.travellerr.aronaTools.shareTools;

import cn.chahuyun.economy.utils.Log;
import cn.travellerr.aronaTools.AronaTools;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.ConcurrencyKind;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class MessageUtil {
    /**
     * 获取用户的下一次消息事件
     *
     * @param user    用户
     * @param subject 载体
     * @return MessageEvent or null
     * @author Moyuyanli
     */
    public static MessageEvent getNextMessageEventFromUser(User user, Contact subject, int timeout, TimeUnit timeUnit) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<MessageEvent> result = new AtomicReference<>();
        GlobalEventChannel.INSTANCE.parentScope(AronaTools.INSTANCE)
                .filterIsInstance(MessageEvent.class)
                .filter(filter -> filter.getSubject().getId() == subject.getId() && filter.getSender().getId() == user.getId())
                .subscribeOnce(MessageEvent.class, AronaTools.INSTANCE.getCoroutineContext(), ConcurrencyKind.CONCURRENT, EventPriority.HIGHEST, event -> {
                    result.set(event);
                    event.intercept();
                    latch.countDown();
                });
        try {
            if (latch.await(timeout, timeUnit)) {
                return result.get();
            } else {
                Log.debug("获取用户下一条消息超时");
                return null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("获取用户下一条消息失败");
        }
    }

    public static MessageEvent getNextMessageEventFromGroup(Group group, Contact subject, String prefix, int timeout, TimeUnit timeUnit) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<MessageEvent> result = new AtomicReference<>();
        GlobalEventChannel.INSTANCE.parentScope(AronaTools.INSTANCE)
                .filterIsInstance(GroupMessageEvent.class)
                .filter(filter -> filter.getSubject().getId() == subject.getId() && filter.getGroup().getId() == group.getId() && filter.getMessage().contentToString().startsWith(prefix))
                .subscribeOnce(MessageEvent.class, AronaTools.INSTANCE.getCoroutineContext(), ConcurrencyKind.CONCURRENT, EventPriority.HIGHEST, event -> {
                    result.set(event);
                    event.intercept();
                    latch.countDown();
                });
        try {
            if (latch.await(timeout, timeUnit)) {
                return result.get();
            } else {
                Log.debug("获取群聊下一条消息超时");
                return null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("获取群聊下一条消息失败");
        }
    }

    public static String getNextMessage (Contact sender, Contact subject, MessageChain message, int timeout, TimeUnit timeUnit) {

        MessageEvent nextEvent = sender instanceof Group ? getNextMessageEventFromGroup((Group)sender, subject, BuildCommand.COMMAND_PREFIX, timeout, timeUnit) : getNextMessageEventFromUser((User) sender, subject, timeout, timeUnit);
        if (nextEvent == null) {
            subject.sendMessage(new QuoteReply(message).plus("操作超时"));
            return "";
        }
        String textMsg = nextEvent.getMessage().serializeToMiraiCode();
        if (textMsg.startsWith(BuildCommand.COMMAND_PREFIX)) {
            textMsg = textMsg.replaceFirst(BuildCommand.COMMAND_PREFIX, "");
        }
        if (textMsg.contains("[mirai:")) {
            subject.sendMessage(new QuoteReply(message).plus("请勿发送引用、图片、at等非正常文本消息"));
            return "";
        }
        if (textMsg.length() > 100) {
            subject.sendMessage(new QuoteReply(message).plus("消息过长"));
            return "";
        }
        if (textMsg.isBlank()) {
            subject.sendMessage(new QuoteReply(message).plus("请勿发送空格"));
            return "";
        }
        if (textMsg.contains("\n")) {
            subject.sendMessage(new QuoteReply(message).plus("请勿发送换行"));
            return "";
        }
        if (textMsg.contains("exit") || textMsg.contains("退出")) {
            subject.sendMessage(new QuoteReply(message).plus("操作已取消"));
            return "";
        }
        return textMsg.strip();
    }

    public static Message quoteReply(MessageChain messageChain, String message) {
        return new QuoteReply(messageChain).plus(message);
    }

    public static Message quoteReply(MessageChain messageChain, MessageChain message) {
        return new QuoteReply(messageChain).plus(message);
    }

    public static Message quoteReply(MessageChain messageChain, Image message) {
        return new QuoteReply(messageChain).plus(message);
    }
}
