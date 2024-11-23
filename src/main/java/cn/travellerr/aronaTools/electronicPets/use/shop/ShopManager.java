package cn.travellerr.aronaTools.electronicPets.use.shop;

import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.electronicPets.use.PetManager;
import cn.travellerr.aronaTools.entity.PetInfo;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;

public class ShopManager {
    public static void buyItem (Contact subject, MessageChain msg, User user, PetInfo petInfo, int itemId) {
        if (petInfo.getIsDead()) {
            subject.sendMessage(new QuoteReply(msg).plus("宠物已死亡"));
            return;
        }
        Item item = getItem(itemId);

        double userMoney = petInfo.getPetTechPoint();

        if (item == null) {
            subject.sendMessage(new QuoteReply(msg).plus("物品不存在\n请发送 \"#物品列表\" 查看物品列表"));
            return;
        }

        if (userMoney < item.getPrice()) {
            subject.sendMessage(new QuoteReply(msg).plus("技术点不足"));
            return;
        }

        petInfo.update();
        petInfo.addPetCoin(-item.getPrice());
        petInfo.addExp(item.getExp());
        petInfo.addRelationship(item.getRelation());
        petInfo.addHunger(item.getHunger());
        petInfo.addHealth(item.getHealth());
        petInfo.addMood(item.getMood());
        petInfo.save();


        subject.sendMessage(new QuoteReply(msg).plus("购买成功：" + item.getName()));


        PetManager.checkPet(subject, msg, user.getId());
    }

    public static Item getItem(int itemId) {
        return AronaTools.electronicPetWorkShop.getItems().get(itemId);
    }
}
