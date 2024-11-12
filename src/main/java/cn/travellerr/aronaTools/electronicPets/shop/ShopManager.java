package cn.travellerr.aronaTools.electronicPets.shop;

import cn.chahuyun.economy.utils.EconomyUtil;
import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.electronicPets.PetManager;
import cn.travellerr.aronaTools.entity.PetInfo;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;

public class ShopManager {
    public static void buyItem (Contact subject, MessageChain msg, User user, PetInfo petInfo, int itemId) {
        Item item = getItem(itemId);

        double userMoney = EconomyUtil.getMoneyByUser(user);

        if (item == null) {
            subject.sendMessage(new QuoteReply(msg).plus("物品不存在"));
            return;
        }

        if (userMoney < item.getPrice()) {
            subject.sendMessage(new QuoteReply(msg).plus("金币不足"));
            return;
        }

        EconomyUtil.plusMoneyToUser(user, -item.getPrice());

        petInfo.addExp(item.getExp());
        petInfo.addRelationship(item.getRelation());
        petInfo.addHunger(item.getHunger());
        petInfo.addHealth(item.getHealth());
        petInfo.addMood(item.getMood());
        petInfo.update();
        petInfo.save();


        subject.sendMessage(new QuoteReply(msg).plus("购买成功：" + item.getName()));


        PetManager.checkPet(subject, msg, user.getId());
    }

    public static Item getItem(int itemId) {
        return AronaTools.electronicPetWorkShop.getItems().get(itemId);
    }
}