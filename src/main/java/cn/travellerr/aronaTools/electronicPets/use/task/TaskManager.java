package cn.travellerr.aronaTools.electronicPets.use.task;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.entity.PetInfo;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;

import java.util.Date;

public class TaskManager {
    public static void startTask(Contact subject, MessageChain msg, PetInfo petInfo, int taskId) {
        if (petInfo.getIsDead()) {
            subject.sendMessage(new QuoteReply(msg).plus("宠物已死亡"));
            return;
        }
        if (petInfo.getTaskId() != 0) {
            subject.sendMessage(new QuoteReply(msg).plus("当前已有任务! 请先结束任务"));
            return;
        }
        if (petInfo.getIsSick()) {
            subject.sendMessage(new QuoteReply(msg).plus("宠物生病了，无法开始任务"));
            return;
        }
        if (petInfo.getIsSleeping()) {
            subject.sendMessage(new QuoteReply(msg).plus("宠物正在睡觉，先唤醒他吧……"));
            return;
        }

        Task task = getTask(taskId);
        if (task == null) {
            subject.sendMessage(new QuoteReply(msg).plus("任务不存在"));
            return;
        }

        petInfo.setTaskId(taskId);
        petInfo.setTaskStartTime(new Date());
        petInfo.save();

        subject.sendMessage(new QuoteReply(msg).plus("任务开始：" + task.getName() + "\n任务时长：" + task.getTakeTime() + "分钟"));
    }

    public static Task getTask(int taskId) {
        return AronaTools.electronicPetWorkShop.getTasks().get(taskId);
    }

    public static void getTaskInfo(Contact subject, MessageChain message, PetInfo petInfo) {
        if (petInfo.getIsDead()) {
            subject.sendMessage(new QuoteReply(message).plus("宠物已死亡"));
            return;
        }
        Task task = getTask(petInfo.getTaskId());
        if (task == null) {
            subject.sendMessage(new QuoteReply(message).plus("任务不存在"));
            return;
        }
        if (petInfo.getIsSick()) {
            subject.sendMessage(new QuoteReply(message).plus("宠物生病了，先好好养伤啊"));
            return;
        }
        if (petInfo.getIsSleeping()) {
            subject.sendMessage(new QuoteReply(message).plus("宠物正在睡觉，先唤醒他吧……"));
            return;
        }

        Date doneTime = DateUtil.offsetMinute(petInfo.getTaskStartTime(), task.getTakeTime());
        long needTime = DateUtil.between(doneTime, new Date(), DateUnit.SECOND);
        boolean isDone = DateUtil.compare(doneTime, new Date()) <= 0;

        String isDoneMsg = isDone ? "任务已完成" : "任务未完成，还需" + DateUtil.formatBetween(needTime * 1000, BetweenFormatter.Level.SECOND);
        String isTasking = petInfo.getTaskId() == 0 ? "当前没有任务" : "任务名称：" + task.getName() + "\n任务描述：" + task.getDescription() + "\n任务时长：" + task.getTakeTime() + "分钟\n" + isDoneMsg;

        subject.sendMessage(new QuoteReply(message).plus(isTasking));

        if (isDone) {
            endTask(subject, message, petInfo);
        }
    }

    public static void endTask(Contact subject, MessageChain message, PetInfo petInfo) {
        if (petInfo.getIsDead()) {
            subject.sendMessage(new QuoteReply(message).plus("宠物已死亡"));
            return;
        }
        Task task = getTask(petInfo.getTaskId());
        if (task == null) {
            subject.sendMessage(new QuoteReply(message).plus("任务不存在\n请发送 \"#任务列表\" 查看任务列表"));
            return;
        }
        if (petInfo.getIsSick()) {
            petInfo.setTaskId(0);
            petInfo.save();
            subject.sendMessage(new QuoteReply(message).plus("宠物生病了，无法结束任务……\n任务已自动取消"));
            return;
        }
        if (petInfo.getIsSleeping()) {
            petInfo.setTaskId(0);
            petInfo.save();
            subject.sendMessage(new QuoteReply(message).plus("宠物正在睡觉，先唤醒他吧……"));
            return;
        }

        long usedTime = DateUtil.between(petInfo.getTaskStartTime(), new Date(), DateUnit.MINUTE);
        if (usedTime > task.getTakeTime()) usedTime = task.getTakeTime();
        if (usedTime < 2) usedTime = 0;


        petInfo.update();
        petInfo.addPetCoin(task.getMoneyPerMin() * usedTime);
        petInfo.addExp((long) (task.getExpPerMin() * usedTime));
        petInfo.addMood(task.getMoodPerMin() * usedTime);
        petInfo.setTaskId(0);
        petInfo.save();

        subject.sendMessage(new QuoteReply(message).plus("任务结束：" + task.getName() + "\n任务时长：" + usedTime + "分钟\n技术点奖励：" + task.getMoneyPerMin() * usedTime + "\n经验奖励：" + task.getExpPerMin() * usedTime + "\n心情奖励：" + task.getMoodPerMin() * usedTime));
    }
}