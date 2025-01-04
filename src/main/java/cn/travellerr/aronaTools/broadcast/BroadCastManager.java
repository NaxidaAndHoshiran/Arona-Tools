package cn.travellerr.aronaTools.broadcast;

import cn.chahuyun.hibernateplus.HibernateFactory;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.travellerr.aronaTools.entity.GroupBroadCastInfo;
import cn.travellerr.aronaTools.shareTools.Log;
import cn.travellerr.aronaTools.shareTools.MessageUtil;
import cn.travellerr.qzone.contact.QzoneBot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BroadCastManager {

    public static Map<Date, Thread> threadList = new ConcurrentHashMap<>();

    private static boolean hasPermission(Member member) {
        return member.getPermission().getLevel() >= 1;
    }

    private static GroupBroadCastInfo getGroupBroadCastInfo(Group group) {
        return HibernateFactory.selectOne(GroupBroadCastInfo.class, group.getId());
    }

    private static void sendMessage(Contact subject, MessageChain messages, String content) {
        subject.sendMessage(MessageUtil.quoteReply(messages, content));
    }

    public static void addGroupBroadCastInfo(Contact subject, MessageChain messages, Member member, Group group) {
        if (!hasPermission(member)) {
            sendMessage(subject, messages, "你的权限不足哦！");
            return;
        }

        if (getGroupBroadCastInfo(group) != null) {
            sendMessage(subject, messages, "该群已经绑定过了哦！请尝试在其他群中绑定");
            return;
        }

        GroupBroadCastInfo groupBroadCastInfo = GroupBroadCastInfo.builder()
                .id(group.getId())
                .groupName(group.getName())
                .build();

        HibernateFactory.merge(groupBroadCastInfo);
        sendMessage(subject, messages, "绑定成功！未来将会在该群中进行更新公告广播");
    }

    public static void removeGroupBroadCastInfo(Contact subject, MessageChain messages, Member member, Group group) {
        if (!hasPermission(member)) {
            sendMessage(subject, messages, "你的权限不足哦！");
            return;
        }

        GroupBroadCastInfo groupBroadCastInfo = getGroupBroadCastInfo(group);
        if (groupBroadCastInfo == null) {
            sendMessage(subject, messages, "该群还未绑定过哦！");
            return;
        }

        HibernateFactory.delete(groupBroadCastInfo);
        sendMessage(subject, messages, "解绑成功！");
    }

    public static void turnOnGroupBroadCast(Contact subject, MessageChain messages, NormalMember member, Group group) {
        if (!hasPermission(member)) {
            sendMessage(subject, messages, "你的权限不足哦！");
            return;
        }

        GroupBroadCastInfo groupBroadCastInfo = getGroupBroadCastInfo(group);
        if (groupBroadCastInfo == null) {
            sendMessage(subject, messages, "该群还未绑定过哦！");
            return;
        }

        groupBroadCastInfo.setTurnedOnService(true);
        HibernateFactory.merge(groupBroadCastInfo);
        sendMessage(subject, messages, "开启成功！");
    }

    public static void turnOffGroupBroadCast(Contact subject, MessageChain messages, NormalMember member, Group group) {
        if (!hasPermission(member)) {
            sendMessage(subject, messages, "你的权限不足哦！");
            return;
        }

        GroupBroadCastInfo groupBroadCastInfo = getGroupBroadCastInfo(group);
        if (groupBroadCastInfo == null) {
            sendMessage(subject, messages, "该群还未绑定过哦！");
            return;
        }

        groupBroadCastInfo.setTurnedOnService(false);
        HibernateFactory.merge(groupBroadCastInfo);
        sendMessage(subject, messages, "关闭成功！");
    }

    public static Map<Long, Group> sendBroadCast(Contact subject, String message) {
        List<GroupBroadCastInfo> groupBroadCastInfoList = HibernateFactory.selectList(GroupBroadCastInfo.class);
        if (groupBroadCastInfoList == null) {
            Log.error("没有绑定的群！");
            return null;
        }

        Map<Long, Group> failedGroup = new HashMap<>();

        Thread thread = new Thread(() -> {
            QzoneBot qZoneBot = new QzoneBot(subject.getBot());
            qZoneBot.sendMessage(message);

            for (GroupBroadCastInfo groupBroadCastInfo : groupBroadCastInfoList) {
                Group group = subject.getBot().getGroup(groupBroadCastInfo.getId());
                if (group == null) {
                    failedGroup.put(groupBroadCastInfo.getId(), null);
                    Log.error("群 " + groupBroadCastInfo.getGroupName() + " 不存在！");
                    continue;
                }

                if (!groupBroadCastInfo.isTurnedOnService()) {
                    failedGroup.put(groupBroadCastInfo.getId(), group);
                    Log.debug("群 " + groupBroadCastInfo.getGroupName() + " 未开启服务！");
                    continue;
                }

                String broadcastMessage = message;

                if (!groupBroadCastInfo.isApplyUrl()) {
                    broadcastMessage = broadcastMessage.replaceAll("http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?", "[链接已隐藏]");
                }

                group.sendMessage(broadcastMessage);
                groupBroadCastInfo.updateLastBroadCastTime();
                HibernateFactory.merge(groupBroadCastInfo);
                try {

                    // 线程休眠 10-60 秒
                    Thread.sleep(RandomUtil.randomLong(10000, 60000));
                } catch (InterruptedException e) {
                    Log.error("线程休眠失败！", e);
                }
            }

            Log.info("广播结束！");

            MessageChainBuilder replyMessage = new MessageChainBuilder();
            if (!failedGroup.isEmpty()) {
                replyMessage.add(new PlainText("以下群组发送失败:\n"));
                failedGroup.forEach((key, group) -> {
                    replyMessage.add(new PlainText(group != null ? group.getName() + " (" + key + ")" : key.toString()));
                    replyMessage.add(new PlainText("\n"));
                });
                subject.sendMessage(replyMessage.build());
            } else {
                replyMessage.add(new PlainText("所有群组已发送"));
                subject.sendMessage(replyMessage.build());
            }
        });
        thread.setName(message);
        thread.start();
        threadList.put(new Date(), thread);

        return failedGroup;
    }

    public static void BroadCastList(Contact subject, MessageChain messages) {
        if (threadList.isEmpty()) {
            subject.sendMessage(MessageUtil.quoteReply(messages, "没有正在进行的广播！"));
        }


        ForwardMessageBuilder replyMessage = new ForwardMessageBuilder(subject);
        Iterator<Map.Entry<Date, Thread>> iterator = threadList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Date, Thread> entry = iterator.next();
            Thread thread = entry.getValue();
            if (!thread.isAlive()) {
                iterator.remove();
                continue;
            }
            replyMessage.add(subject.getBot(), new PlainText("广播线程ID: " + thread.getId() + "\n广播内容: " + thread.getName()
                    + "\n广播开始时间: " + DateUtil.format(entry.getKey(), "yyyy-MM-dd HH:mm:ss")));
        }
        if (replyMessage.isEmpty()) {
            sendMessage(subject, messages, "没有正在进行的广播！");
            return;
        }

        subject.sendMessage(replyMessage.build());
    }

    public static void turnOnUrlApply(Contact subject, MessageChain message, Member sender, Group group) {
        if (!hasPermission(sender)) {
            sendMessage(subject, message, "你的权限不足哦！");
            return;
        }

        GroupBroadCastInfo groupBroadCastInfo = getGroupBroadCastInfo(group);
        if (groupBroadCastInfo == null) {
            sendMessage(subject, message, "该群还未绑定过哦！");
            return;
        }

        groupBroadCastInfo.setApplyUrl(true);
        HibernateFactory.merge(groupBroadCastInfo);
        sendMessage(subject, message, "开启成功！");
    }

    public static void turnOffUrlApply(Contact subject, MessageChain message, Member sender, Group group) {
        if (!hasPermission(sender)) {
            sendMessage(subject, message, "你的权限不足哦！");
            return;
        }

        GroupBroadCastInfo groupBroadCastInfo = getGroupBroadCastInfo(group);
        if (groupBroadCastInfo == null) {
            sendMessage(subject, message, "该群还未绑定过哦！");
            return;
        }

        groupBroadCastInfo.setApplyUrl(false);
        HibernateFactory.merge(groupBroadCastInfo);
        sendMessage(subject, message, "关闭成功！");
    }
}