package cn.travellerr.aronaTools.components;

import cn.chahuyun.economy.utils.EconomyUtil;
import cn.chahuyun.hibernateplus.HibernateFactory;
import cn.travellerr.aronaTools.electronicPets.use.PetManager;
import cn.travellerr.aronaTools.entity.GiftInfo;
import cn.travellerr.favourite.FavouriteManager;
import net.mamoe.mirai.contact.User;

import java.util.HashMap;
import java.util.Map;

public class GiftPackage {

    /**
     * 创建新的礼包
     * <br>
     * 指令：/xx 名称 描述 领取信息 物品1 数量1 物品2 数量2 ...
     * 参数永远为奇数个
     * @param info 礼包信息
     */
    public static String CreateGiftPackage(String info) {
        String[] args = info.split(" ");
        String name = args[0];
        String description = args[1];
        String receiveMessage = args[2];
        Map<String, Double> contain = new HashMap<>();
        for (int i = 3; i < args.length; i += 2) {
            contain.put(args[i], Double.parseDouble(args[i + 1]));
        }

        int index = HibernateFactory.selectList(GiftInfo.class).stream().map(GiftInfo::getGiftId).max(Integer::compareTo).orElse(0) + 1;

        GiftInfo giftInfo = GiftInfo.builder()
                .giftId(index)
                .giftName(name)
                .giftDescription(description)
                .receivedGiftMessage(receiveMessage)
                .contain(GiftInfo.mapToString(contain))
                .build();

        HibernateFactory.merge(giftInfo);

        return "创建礼包 " + index + " 成功";
    }

    /**
     * 删除礼包
     * @param giftId 礼包id
     */
    public static String DeleteGiftPackage(int giftId) {
        GiftInfo info = HibernateFactory.selectOne(GiftInfo.class, giftId);
        if (info == null) {
            return "礼包不存在";
        }
        HibernateFactory.delete(info);
        return "删除礼包 "+giftId+" 成功";
    }

    public static String ReceiveGiftPackage(User user) {
        int giftId = HibernateFactory.selectList(GiftInfo.class).stream().map(GiftInfo::getGiftId).max(Integer::compareTo).orElse(0);
        return ReceiveGiftPackage(user, giftId);
    }

    /**
     * 领取礼包
     * @param giftId 礼包id
     */
    public static String ReceiveGiftPackage(User user, int giftId) {
        GiftInfo info = HibernateFactory.selectOne(GiftInfo.class, giftId);
        if (info == null) {
            return "礼包不存在";
        }

        if (info.getReceivers().contains(user.getId())) {
            return "您已经领取过该礼包啦~";
        }

        StringBuilder sb = new StringBuilder();

        Map<String, Double> contain = info.getContain();
        for (String key : contain.keySet()) {
            if (key.contains("economy") || key.contains("金币")) {
                EconomyUtil.plusMoneyToUser(user, contain.get(key));
                sb.append("金币 ").append(contain.get(key)).append("; ");
                continue;
            }
            if (key.contains("exp") || key.contains("经验") || key.contains("好感")) {
                FavouriteManager.addLove(user, contain.get(key).intValue());
                sb.append("好感 ").append(contain.get(key)).append("; ");
                continue;
            }
            if (key.contains("tech") || key.contains("科技")) {
                sb.append("科技 ").append(contain.get(key)).append(" ");
                if (!PetManager.addPetCoin(user, contain.get(key).intValue())) {
                    sb.append("(由于未拥有宠物，无法领取); ");
                } else {
                    sb.append("; ");
                }

            }
        }

        info.addReceiver(user.getId());

        sb.append("\n").append(info.getReceivedGiftMessage());


        return "领取礼包 " + giftId + " 成功\n" + sb;
    }

}
