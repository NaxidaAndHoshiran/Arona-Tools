package cn.travellerr.aronaTools.electronicPets.command;

import cn.travellerr.aronaTools.electronicPets.shop.Item;
import cn.travellerr.aronaTools.electronicPets.shop.ShopManager;
import cn.travellerr.aronaTools.electronicPets.shop.WorkShopItemManager;
import cn.travellerr.aronaTools.electronicPets.task.Task;
import cn.travellerr.aronaTools.electronicPets.task.TaskManager;
import cn.travellerr.aronaTools.electronicPets.task.WorkShopTaskManager;
import cn.travellerr.aronaTools.electronicPets.type.ItemType;
import cn.travellerr.aronaTools.electronicPets.type.TaskType;
import cn.travellerr.aronaTools.shareTools.Log;
import kotlin.coroutines.CoroutineContext;
import kotlin.text.Regex;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.QuoteReply;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 任务指令监听器
 * <p>用于处理创建任务和查看任务列表的指令</p>
 * @author Travellerr
 */
public class WorkShopCommandListener extends SimpleListenerHost {

    private static final Regex createTaskByStep = BuildCommand.createCommand("创建任务");

    /**
     * 创建任务指令的正则表达式
     * <p>参数: 任务编号, 任务名称, 任务描述, 任务类型, 任务时长, 金币奖励, 经验奖励, 心情奖励</p>
     */
    private static final Regex createTask = BuildCommand.createCommand("创建任务", String.class, String.class, String.class, String.class, Integer.class, Double.class, Double.class, Double.class);

    /**
     * 创建物品指令的正则表达式
     * <p>参数: 物品编号, 物品名称, 物品描述, 物品类型, 价格, 经验奖励, 饥饿度奖励, 心情奖励, 生命值奖励, 亲密度奖励</p>
     */
    private static final Regex createItem = BuildCommand.createCommand("创建物品", String.class, String.class, String.class, String.class, Integer.class, Long.class, Double.class, Double.class, Double.class, Double.class);
    private static final Regex createItemByStep = BuildCommand.createCommand("创建物品");
    private static final Regex deleteTask = BuildCommand.createCommand("删除任务", Integer.class);

    /**
     * 查看任务列表指令的正则表达式
     */
    private static final Regex checkTaskList = BuildCommand.createCommand("查看任务列表|任务列表|获取任务");

    private static final Regex checkItemList = BuildCommand.createCommand("查看物品列表|物品列表|获取物品");
    private static final Regex deleteItem = BuildCommand.createCommand("删除物品", Integer.class);

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }

    /**
     * 构造方法
     */
    public WorkShopCommandListener() {
        super();
    }

    /**
     * 处理消息事件
     * <p>根据消息内容匹配相应的指令并执行相应的操作</p>
     *
     * @param event 消息事件
     */
    @EventHandler
    public void onMessage(@NotNull MessageEvent event) {
        Contact subject = event.getSubject();
        User sender = event.getSender();
        String message = event.getMessage().contentToString();

        if (createTask.matches(message)) {
            Log.info("创建任务指令");
            List<String> key = BuildCommand.getEveryValue(createTask, message);

            String code = key.get(0);
            String name = key.get(1);
            String description = key.get(2);
            String taskTypeString = key.get(3);
            int takeTime = Integer.parseInt(key.get(4));
            double moneyPerMin = Double.parseDouble(key.get(5));
            double expPerMin = Double.parseDouble(key.get(6));
            double moodPerMin = Double.parseDouble(key.get(7));

            TaskType taskType;
            try {
                taskType = TaskType.fromString(taskTypeString);
            } catch (Exception e) {
                subject.sendMessage("未知的任务类型:" + taskTypeString);
                return;
            }

            Task task = new Task(code, name, description, false, taskType, takeTime, moneyPerMin, expPerMin, moodPerMin, sender.getNick(), sender.getId());

            int index = WorkShopTaskManager.addTask(task);

            subject.sendMessage("任务创建成功! 任务编号: " + index + "\n请等待审核，审核通过后将会在任务列表中显示");

        }

        if (checkTaskList.matches(message)) {
            Log.info("查看任务列表指令");

            subject.sendMessage(WorkShopTaskManager.getTaskList(subject));
        }

        // 物品指令

        if (createItem.matches(message)) {
            Log.info("创建物品指令");

            List<String> key = BuildCommand.getEveryValue(createItem, message);
            String code = key.get(0);
            String name = key.get(1);
            String description = key.get(2);
            String itemTypeString = key.get(3);
            int cost = Integer.parseInt(key.get(4));
            long addExp = Long.parseLong(key.get(5));
            double addHunger = Double.parseDouble(key.get(6));
            double addMood = Double.parseDouble(key.get(7));
            double addHealth = Double.parseDouble(key.get(8));
            double addRelationship = Double.parseDouble(key.get(9));

            ItemType itemType;

            try {
                itemType = ItemType.fromString(itemTypeString);
            } catch (Exception e) {
                subject.sendMessage("未知的任务类型:" + itemTypeString);
                return;
            }

            Item item = new Item(code, name, description, false, itemType, cost, addExp, addHunger, addMood, addHealth, addRelationship, sender.getNick(), sender.getId());

            int index = WorkShopItemManager.addItem(item);

            subject.sendMessage("物品创建成功! 任务编号: " + index + "\n请等待审核，审核通过后将会在物品列表中显示");
        }

        if (checkItemList.matches(message)) {
            Log.info("查看物品列表指令");

            subject.sendMessage(WorkShopItemManager.getItemList(subject));
        }

        if (createTaskByStep.matches(message)) {
            Log.info("创建任务指令");
            WorkShopTaskManager.createTaskByStep(subject, sender, event.getMessage());
        }

        if (createItemByStep.matches(message)) {
            Log.info("创建物品指令");
            WorkShopItemManager.createItemByStep(subject, sender, event.getMessage());
        }

        if (deleteTask.matches(message)) {
            Log.info("删除任务指令");
            List<String> key = BuildCommand.getEveryValue(deleteTask, message);
            int taskId = Integer.parseInt(key.get(0));
            Task task = TaskManager.getTask(taskId);
            if (task == null) {
                subject.sendMessage("任务不存在！");
                return;
            }
            if (WorkShopTaskManager.deleteTask(sender, task)) {
                subject.sendMessage(new QuoteReply(event.getMessage()).plus("任务删除成功！"));
            } else {
                subject.sendMessage("任务删除失败！");
            }
        }

        if (deleteItem.matches(message)) {
            Log.info("删除物品指令");
            List<String> key = BuildCommand.getEveryValue(deleteItem, message);
            int itemId = Integer.parseInt(key.get(0));
            Item item = ShopManager.getItem(itemId);
            if (item == null) {
                subject.sendMessage("物品不存在！");
                return;
            }
            if (WorkShopItemManager.deleteItem(sender, item)) {
                subject.sendMessage(new QuoteReply(event.getMessage()).plus("物品删除成功！"));
            } else {
                subject.sendMessage("物品删除失败！");
            }
        }
    }
}