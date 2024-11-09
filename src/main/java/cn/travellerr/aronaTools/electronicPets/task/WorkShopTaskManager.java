package cn.travellerr.aronaTools.electronicPets.task;

import cn.travellerr.aronaTools.AronaTools;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.ForwardMessage;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Task manager class providing CRUD operations for tasks.
 */
public class WorkShopTaskManager {

    public static void deleteTask(Task task) {
        Map<Integer, Task> tasks = AronaTools.electronicPetWorkShop.getTasks();
        tasks.values().removeIf(value -> value.equals(task));
        AronaTools.electronicPetWorkShop.setTasks(tasks);
    }

    public static Integer addTask(Task task) {
        Map<Integer, Task> tasks = AronaTools.electronicPetWorkShop.getTasks();
        int index = tasks.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
        tasks.put(index, task);
        AronaTools.electronicPetWorkShop.setTasks(tasks);
        return index;
    }

    public static ForwardMessage getTaskList(Contact subject) {
        Map<Integer, Task> tasks = AronaTools.electronicPetWorkShop.getTasks().entrySet().stream()
                .filter(entry -> entry.getValue().isVerified())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return buildTaskMessage(new ForwardMessageBuilder(subject), subject.getBot(), tasks);
    }

    public static void getUnverifiedTaskList(Contact subject) {
        Map<Integer, Task> tasks = AronaTools.electronicPetWorkShop.getTasks().entrySet().stream()
                .filter(entry -> !entry.getValue().isVerified())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (tasks.isEmpty()) {
            subject.sendMessage("没有未审核的任务");
        } else {
            subject.sendMessage(buildTaskMessage(new ForwardMessageBuilder(subject), subject.getBot(), tasks));
        }
    }

    public static void rejectTask(Contact subject, int index) {
        updateTaskVerification(subject, index, false, "已拒绝");
    }

    public static void approveTask(Contact subject, int index) {
        updateTaskVerification(subject, index, true, "已通过");
    }

    private static void updateTaskVerification(Contact subject, int index, boolean isVerified, String message) {
        Map<Integer, Task> tasks = AronaTools.electronicPetWorkShop.getTasks();
        Task task = tasks.get(index);
        task.setVerified(isVerified);
        tasks.put(index, task);
        AronaTools.electronicPetWorkShop.setTasks(tasks);
        subject.sendMessage(index + " 号任务" + message);
    }

    private static ForwardMessage buildTaskMessage(ForwardMessageBuilder builder, Bot bot, Map<Integer, Task> tasks) {
        tasks.forEach((index, task) -> {
            Message message = new PlainText("任务编号: " + index + "\n")
                    .plus("任务名称: " + task.getName() + "\n")
                    .plus("任务描述: " + task.getDescription() + "\n")
                    .plus("任务类型: " + task.getTaskType().getName() + "\n")
                    .plus("任务时长: " + task.getTakeTime() + "分钟\n")
                    .plus("金币奖励: " + task.getMoneyPerMin() + "金币/分钟\n")
                    .plus("经验奖励: " + task.getExpPerMin() + "经验/分钟\n")
                    .plus("心情奖励: " + task.getMoodPerMin() + "心情/分钟\n")
                    .plus("任务创建者: " + task.getCreatorName() + "(" + task.getCreatorId() + ")\n");
            if (index == 99 && tasks.size() > 100) {
                message = message.plus(new PlainText("任务过多，已截断"));
                builder.add(bot, message);
                return;
            }
            builder.add(bot, message);
        });
        return builder.build();
    }
}