package cn.travellerr.aronaTools.electronicPets.use.shop;

import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.electronicPets.use.PetManager;
import cn.travellerr.aronaTools.entity.PetInfo;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;

public class ShopManager {
    public static void buyItem(Contact subject, MessageChain msg, User user, PetInfo petInfo, int itemId) {
        buyItem(subject, msg, user, petInfo, itemId, 1);
    }

    public static void buyItem(Contact subject, MessageChain msg, User user, PetInfo petInfo, int itemId, int count) {

        Item item = getItem(itemId);
        if (item == null) {
            subject.sendMessage(new QuoteReply(msg).plus("物品不存在\n请发送 \"#物品列表\" 查看物品列表"));
            return;
        }
        if (!item.getCode().contains("reborn") && petInfo.getIsDead()) {
            subject.sendMessage(new QuoteReply(msg).plus("宠物已死亡"));
            return;
        }
        double totalPrice = item.getPrice() * count;
        if (petInfo.getPetTechPoint() < totalPrice) {
            subject.sendMessage(new QuoteReply(msg).plus("技术点不足"));
            return;
        }

        petInfo.update();

        if (item.getCode().contains("reborn") && petInfo.getIsDead()) {
            petInfo.setIsDead(false);
            petInfo.setPetHp((double) petInfo.getPetMaxHp() /2);
        }

        petInfo.addPetCoin(-totalPrice);
        petInfo.addExp(item.getExp() * count);
        petInfo.addRelationship(item.getRelation() * count);
        petInfo.addHunger(item.getHunger() * count);
        petInfo.addHealth(item.getHealth() * count);
        petInfo.addMood(item.getMood() * count);
        petInfo.save();
        subject.sendMessage(new QuoteReply(msg).plus("购买成功：" + item.getName() + " x" + count));
        PetManager.checkPet(subject, msg, user.getId());
    }

    public static Item getItem(int itemId) {
        return AronaTools.electronicPetWorkShop.getItems().get(itemId);
    }
}
