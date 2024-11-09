package cn.travellerr.aronaTools.electronicPets.task;

import cn.chahuyun.economy.utils.EconomyUtil;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.entity.PetInfo;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;

import java.util.Date;

public class TaskManager {
    public static void startTask(Contact subject, MessageChain msg, PetInfo petInfo, int taskId) {
        if (petInfo.getTaskId() != 0) {
            subject.sendMessage(new QuoteReply(msg).plus("当前已有任务! 请先结束任务"));
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

    public static void getTaskInfo(User user, Contact subject, MessageChain message, PetInfo petInfo) {
        Task task = getTask(petInfo.getTaskId());
        if (task == null) {
            subject.sendMessage(new QuoteReply(message).plus("任务不存在"));
            return;
        }

        Date doneTime = DateUtil.offsetMinute(petInfo.getTaskStartTime(), task.getTakeTime());
        long needTime = DateUtil.between(doneTime, new Date(), DateUnit.SECOND);
        boolean isDone = DateUtil.compare(doneTime, new Date()) <= 0;

        String isDoneMsg = isDone ? "任务已完成" : "任务未完成，还需" + DateUtil.formatBetween(needTime * 1000, BetweenFormatter.Level.SECOND);
        String isTasking = petInfo.getTaskId() == 0 ? "当前没有任务" : "任务名称：" + task.getName() + "\n任务描述：" + task.getDescription() + "\n任务时长：" + task.getTakeTime() + "分钟\n" + isDoneMsg;

        subject.sendMessage(new QuoteReply(message).plus(isTasking));

        if (isDone) {
            endTask(user, subject, message, petInfo);
        }
    }

    public static void endTask(User user, Contact subject, MessageChain message, PetInfo petInfo) {
        Task task = getTask(petInfo.getTaskId());
        if (task == null) {
            subject.sendMessage(new QuoteReply(message).plus("任务不存在"));
            return;
        }

        long needTime = DateUtil.between(DateUtil.offsetMinute(petInfo.getTaskStartTime(), task.getTakeTime()), new Date(), DateUnit.MINUTE);
        long usedTime = task.getTakeTime() - needTime;
        if (usedTime < 2) usedTime = 0;

        long taskTime = needTime > 0 ? usedTime : task.getTakeTime();

        EconomyUtil.plusMoneyToUser(user, task.getMoneyPerMin() * taskTime);


        petInfo.addExp((long) (task.getExpPerMin() * taskTime));
        petInfo.addMood(task.getMoodPerMin() * taskTime);
        petInfo.setTaskId(0);
        petInfo.update();
        petInfo.save();

        subject.sendMessage(new QuoteReply(message).plus("任务结束：" + task.getName() + "\n任务时长：" + taskTime + "分钟\n金币奖励：" + task.getMoneyPerMin() * taskTime + "\n经验奖励：" + task.getExpPerMin() * taskTime + "\n心情奖励：" + task.getMoodPerMin() * taskTime));
    }
}