package cn.travellerr.aronaTools.electronicPets.shop;

import cn.travellerr.aronaTools.AronaTools;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.ForwardMessage;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;

import java.util.Map;
import java.util.stream.Collectors;

public class WorkShopItemManager {
    public static Integer addItem(Item item) {
        Map<Integer, Item> items = AronaTools.electronicPetWorkShop.getItems();
        int index = items.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
        items.put(index, item);
        AronaTools.electronicPetWorkShop.setItems(items);
        return index;
    }



    public static ForwardMessage getItemList(Contact subject) {
    Map<Integer, Item> items = AronaTools.electronicPetWorkShop.getItems().entrySet().stream()
            .filter(entry -> entry.getValue().isVerified())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return buildItemMessage(new ForwardMessageBuilder(subject), subject.getBot(), items);
}

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

public static void rejectItem(Contact subject, int index) {
    updateItemVerification(subject, index, false, "已拒绝");
}

public static void approveItem(Contact subject, int index) {
    updateItemVerification(subject, index, true, "已通过");
}

private static void updateItemVerification(Contact subject, int index, boolean isVerified, String message) {
    Map<Integer, Item> items = AronaTools.electronicPetWorkShop.getItems();
    Item item = items.get(index);
    item.setVerified(isVerified);
    items.put(index, item);
    AronaTools.electronicPetWorkShop.setItems(items);
    subject.sendMessage(index + " 号物品" + message);
}

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
}
