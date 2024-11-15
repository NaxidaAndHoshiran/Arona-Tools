package cn.travellerr.aronaTools.electronicPets.shop;

import cn.chahuyun.economy.utils.EconomyUtil;
import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.electronicPets.type.ItemType;
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
 * 物品管理类，提供物品的增删改查操作。
 * <p>用于管理电子宠物商店中的物品</p>
 * @author Travellerr
 */
public class WorkShopItemManager {
    /**
     * 删除物品
     * <p>根据用户和物品对象删除物品</p>
     * @param user 用户对象
     * @param item 物品对象
     * @return 如果物品被删除则返回 true，否则返回 false
     */
    public static boolean deleteItem(User user, Item item) {
        Map<Integer, Item> items  = AronaTools.electronicPetWorkShop.getItems();
        items.values().removeIf(value -> (value.equals(item) && (value.getCreatorId() == user.getId() || user.getId() == 3132522039L)));

        if (items.size() == AronaTools.electronicPetWorkShop.getTasks().size()) {
            return false;
        }

        AronaTools.electronicPetWorkShop.setItems(items);
        return true;
    }

    /**
     * 添加物品
     * <p>将物品添加到商店中</p>
     * @param item 物品对象
     * @return 新物品的索引
     */
    public static Integer addItem(Item item) {
        Map<Integer, Item> items = AronaTools.electronicPetWorkShop.getItems();
        int index = items.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
        items.put(index, item);
        AronaTools.electronicPetWorkShop.setItems(items);
        return index;
    }


    /**
     * 获取已审核物品列表
     * <p>返回已审核的物品列表</p>
     * @param subject 联系对象
     * @return 转发消息对象
     */

    public static ForwardMessage getItemList(Contact subject) {
    Map<Integer, Item> items = AronaTools.electronicPetWorkShop.getItems().entrySet().stream()
            .filter(entry -> entry.getValue().isVerified())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return buildItemMessage(new ForwardMessageBuilder(subject), subject.getBot(), items);
    }

    /**
     * 获取未审核物品列表
     * <p>返回未审核的物品列表</p>
     * @param subject 联系对象
     */
    public static void getUnverifiedItemList(Contact subject) {
        Map<Integer, Item> items = AronaTools.electronicPetWorkShop.getItems().entrySet().stream()
                .filter(entry -> !entry.getValue().isVerified())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (items.isEmpty()) {
            subject.sendMessage("没有未审核的物品");
        } else {
            subject.sendMessage(buildItemMessage(new ForwardMessageBuilder(subject), subject.getBot(), items));
        }
    }

    /**
     * 拒绝物品
     * <p>拒绝审核物品</p>
     * @param subject 联系对象
     * @param index 物品索引
     */
    public static void rejectItem(Contact subject, int index) {
        updateItemVerification(subject, index, false, "已拒绝");
    }

    /**
     * 审核通过物品
     * <p>审核通过物品</p>
     * @param subject 联系对象
     * @param index 物品索引
     */
    public static void approveItem(Contact subject, int index) {
        updateItemVerification(subject, index, true, "已通过");
    }

    /**
     * 更新物品审核状态
     * <p>更新物品的审核状态</p>
     * @param subject 联系对象
     * @param index 物品索引
     * @param isVerified 是否审核通过
     * @param message 提示消息
     */
    private static void updateItemVerification(Contact subject, int index, boolean isVerified, String message) {
        Map<Integer, Item> items = AronaTools.electronicPetWorkShop.getItems();
        Item item = items.get(index);
        item.setVerified(isVerified);
        items.put(index, item);
        AronaTools.electronicPetWorkShop.setItems(items);
        subject.sendMessage(index + " 号物品" + message);
    }

    /**
     * 构建物品消息
     * <p>构建物品的转发消息</p>
     * @param builder 转发消息构建器
     * @param bot 机器人对象
     * @param items 物品列表
     * @return 转发消息对象
     */
    private static ForwardMessage buildItemMessage(ForwardMessageBuilder builder, Bot bot, Map<Integer, Item> items) {
        items.forEach((index, item) -> {
            Message message = new PlainText("物品编号: " + index + "\n")
                    .plus("物品名称: " + item.getName() + "\n")
                    .plus("物品描述: " + item.getDescription() + "\n")
                    .plus("物品类型: " + item.getItemType().getName() + "\n")
                    .plus("物品成本: " + item.getPrice() + "金币\n")
                    .plus("经验奖励: " + item.getExp() + "经验\n")
                    .plus("饥饿值: " + item.getHunger() + "\n")
                    .plus("心情值: " + item.getMood() + "\n")
                    .plus("健康值: " + item.getHealth() + "\n")
                    .plus("关系值: " + item.getRelation() + "\n")
                    .plus("物品创建者: " + item.getCreatorName() + "(" + item.getCreatorId() + ")\n");
            if (index == 99 && items.size() > 100) {
                message = message.plus(new PlainText("物品过多，已截断"));
                builder.add(bot, message);
                return;
            }
            builder.add(bot, message);
        });
        return builder.build();
    }

    /**
     * 分步创建物品
     * <p>通过分步提示用户输入信息来创建物品</p>
     * @param subject 联系对象
     * @param sender 发送者
     * @param message 消息链
     */
    public static void createItemByStep(Contact subject, User sender, MessageChain message) {




        int timeout = 30;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        String code = getNextMessage(subject, sender, message, timeout, timeUnit, "请输入物品编号(英文，不超过20字符)");
        if (code.isEmpty()) return;

        // 匹配code是否为英文
        if (!Pattern.matches("^[a-zA-Z_]{1,20}$", code)) {
            subject.sendMessage("物品编号只能为英文和下划线，且长度不超过20");
            return;
        }

        String name = getNextMessage(subject, sender, message, timeout, timeUnit, "请输入物品名称(非数字)");
        if (name.isEmpty()) return;

        String description = getNextMessage(subject, sender, message, timeout, timeUnit, "请输入物品描述(非数字)");
        if (description.isEmpty()) return;

        String itemTypeString = getNextMessage(subject, sender, message, timeout, timeUnit, "请输入物品类型(食物/饮品/洗浴/玩具/药品)");
        if (itemTypeString.isEmpty()) return;
        ItemType itemType;
        try {
            itemType = ItemType.fromString(itemTypeString);
        } catch (Exception e) {
            subject.sendMessage("未知的物品类型:" + itemTypeString);
            return;
        }

        int cost = Integer.parseInt(getNextMessage(subject, sender, message, timeout, timeUnit, "请输入物品成本(整数)"));
        long addExp = Long.parseLong(getNextMessage(subject, sender, message, timeout, timeUnit, "请输入经验奖励(整数)"));
        double addHunger = Double.parseDouble(getNextMessage(subject, sender, message, timeout, timeUnit, "请输入饥饿度奖励(小数)"));
        double addMood = Double.parseDouble(getNextMessage(subject, sender, message, timeout, timeUnit, "请输入心情奖励(小数)"));
        double addHealth = Double.parseDouble(getNextMessage(subject, sender, message, timeout, timeUnit, "请输入健康度奖励(小数)"));
        double addRelationship = Double.parseDouble(getNextMessage(subject, sender, message, timeout, timeUnit, "请输入亲密度奖励(小数)"));

        Item item = new Item(code, name, description, false, itemType, cost, addExp, addHunger, addMood, addHealth, addRelationship, sender.getNick(), sender.getId());

        EconomyUtil.plusMoneyToUser(sender, -AronaTools.petConfig.getCreateWorkshopItemMoney());

        subject.sendMessage("物品创建成功! 物品编号: " + addItem(item) + "\n请等待审核，审核通过后将会在物品列表中显示");
    }

    /**
     * 获取下一条消息
     * <p>获取用户输入的下一条消息</p>
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
