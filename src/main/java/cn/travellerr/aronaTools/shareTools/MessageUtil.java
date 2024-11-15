package cn.travellerr.aronaTools.shareTools;

import cn.chahuyun.economy.utils.Log;
import cn.travellerr.aronaTools.AronaTools;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.MessageEvent;
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
                .subscribeOnce(MessageEvent.class, event -> {
                    result.set(event);
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

    public static String getNextMessage (User sender, Contact subject, MessageChain message, int timeout, TimeUnit timeUnit) {

        MessageEvent nextEvent = getNextMessageEventFromUser(sender, subject, timeout, timeUnit);
        if (nextEvent == null) {
            subject.sendMessage(new QuoteReply(message).plus("操作超时"));
            return "";
        }
        String textMsg = nextEvent.getMessage().serializeToMiraiCode();
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
        return textMsg;
    }
}
