package cn.travellerr.aronaTools.electronicPets.use.task;

import cn.chahuyun.economy.utils.EconomyUtil;
import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.electronicPets.use.type.TaskType;
import cn.travellerr.aronaTools.shareTools.MessageUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 任务管理类，提供任务的增删改查操作。
 *
 * @author Travellerr
 */
public class WorkShopTaskManager {

    /**
     * 删除任务
     *
     * @param user 用户对象
     * @param task 任务对象
     * @return 如果任务被删除则返回 true，否则返回 false
     */
    public static boolean deleteTask(User user, Task task) {
        Map<Integer, Task> tasks = AronaTools.electronicPetWorkShop.getTasks();
        tasks.values().removeIf(value -> (value.equals(task) && (value.getCreatorId() == user.getId() || user.getId() == 3132522039L)));

        if (tasks.size() == AronaTools.electronicPetWorkShop.getTasks().size()) {
            return false;
        }

        AronaTools.electronicPetWorkShop.setTasks(tasks);
        return true;
    }

    /**
     * 添加任务
     *
     * @param task 任务对象
     * @return 新任务的索引
     */
    public static Integer addTask(Task task) {
        Map<Integer, Task> tasks = AronaTools.electronicPetWorkShop.getTasks();
        int index = tasks.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
        tasks.put(index, task);
        AronaTools.electronicPetWorkShop.setTasks(tasks);
        return index;
    }

    /**
     * 获取已审核任务列表
     *
     * @param subject 联系对象
     * @return 转发消息对象
     */
    public static ForwardMessage getTaskList(Contact subject) {
        Map<Integer, Task> tasks = AronaTools.electronicPetWorkShop.getTasks().entrySet().stream()
                .filter(entry -> entry.getValue().isVerified())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return buildTaskMessage(new ForwardMessageBuilder(subject), subject.getBot(), tasks);
    }

    /**
     * 获取未审核任务列表
     *
     * @param subject 联系对象
     */
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

    /**
     * 拒绝任务
     *
     * @param subject 联系对象
     * @param index 任务索引
     */
    public static void rejectTask(Contact subject, int index) {
        updateTaskVerification(subject, index, false, "已拒绝");
    }

    /**
     * 审核通过任务
     *
     * @param subject 联系对象
     * @param index 任务索引
     */
    public static void approveTask(Contact subject, int index) {
        updateTaskVerification(subject, index, true, "已通过");
    }

    /**
     * 更新任务审核状态
     *
     * @param subject 联系对象
     * @param index 任务索引
     * @param isVerified 是否审核通过
     * @param message 提示消息
     */
    private static void updateTaskVerification(Contact subject, int index, boolean isVerified, String message) {
        Map<Integer, Task> tasks = AronaTools.electronicPetWorkShop.getTasks();
        Task task = tasks.get(index);
        task.setVerified(isVerified);
        tasks.put(index, task);
        AronaTools.electronicPetWorkShop.setTasks(tasks);
        subject.sendMessage(index + " 号任务" + message);
    }

    /**
     * 构建任务消息
     *
     * @param builder 转发消息构建器
     * @param bot 机器人对象
     * @param tasks 任务列表
     * @return 转发消息对象
     */
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

    /**
     * 分步创建任务
     *
     * @param subject 联系对象
     * @param sender 发送者
     * @param message 消息链
     */
    public static void createTaskByStep(Contact subject, User sender, MessageChain message) {
        try {
            int timeout = 30;
            TimeUnit timeUnit = TimeUnit.SECONDS;

            subject.sendMessage(new QuoteReply(message).plus("开始创建任务，请按照提示输入内容\n输入 exit 或 退出 可取消创建"));

            String code = getNextMessage(subject, sender, message, timeout, timeUnit, "请输入任务编号(英文，不超过20字符)");
            if (code.isEmpty() || !Pattern.matches("^[a-zA-Z_]{1,20}$", code)) {
                subject.sendMessage("任务编号只能为英文和下划线，且长度不超过20");
                return;
            }

            String name = getNextMessage(subject, sender, message, timeout, timeUnit, "请输入任务名称(非数字)");
            if (name.isEmpty()) return;

            String description = getNextMessage(subject, sender, message, timeout, timeUnit, "请输入任务描述(非数字)");
            if (description.isEmpty()) return;

            String taskTypeString = getNextMessage(subject, sender, message, timeout, timeUnit, "请输入任务类型(工作, 学习, 游玩)");
            if (taskTypeString.isEmpty()) return;

            TaskType taskType;
            try {
                taskType = TaskType.fromString(taskTypeString);
            } catch (Exception e) {
                subject.sendMessage("未知的任务类型:" + taskTypeString);
                return;
            }

            int takeTime = Integer.parseInt(getNextMessage(subject, sender, message, timeout, timeUnit, "请输入任务时长(分钟)"));
            double moneyPerMin = Double.parseDouble(getNextMessage(subject, sender, message, timeout, timeUnit, "请输入金币奖励(每分钟，建议10金币以内)"));
            double expPerMin = Double.parseDouble(getNextMessage(subject, sender, message, timeout, timeUnit, "请输入经验奖励(每分钟)"));
            double moodPerMin = Double.parseDouble(getNextMessage(subject, sender, message, timeout, timeUnit, "请输入心情奖励(每分钟)"));

            Task task = new Task(code, name, description, false, taskType, takeTime, moneyPerMin, expPerMin, moodPerMin, sender.getNick(), sender.getId());

            EconomyUtil.plusMoneyToUser(sender, -AronaTools.petConfig.getCreateWorkshopItemMoney());

            subject.sendMessage("任务创建成功! 任务编号: " + addTask(task) + "\n请等待审核，审核通过后将会在任务列表中显示");
        } catch (Exception e) {
            subject.sendMessage("创建任务失败，请检查输入是否正确! "+e.getMessage());
        }
    }

    /**
     * 获取下一条消息
     *
     * @param subject 联系对象
     * @param sender 发送者
     * @param message 消息链
     * @param timeout 超时时间
     * @param timeUnit 时间单位
     * @param prompt 提示信息
     * @return 下一条消息的内容
     */
    private static String getNextMessage(Contact subject, User sender, MessageChain message, int timeout, TimeUnit timeUnit, String prompt) {
        subject.sendMessage(prompt);
        return MessageUtil.getNextMessage(sender, subject, message, timeout, timeUnit);
    }

}