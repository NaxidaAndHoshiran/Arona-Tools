package cn.travellerr.aronaTools.electronicPets;

import cn.chahuyun.economy.utils.EconomyUtil;
import cn.chahuyun.hibernateplus.HibernateFactory;
import cn.travellerr.aronaTools.electronicPets.type.PetType;
import cn.travellerr.aronaTools.entity.PetInfo;
import cn.travellerr.aronaTools.shareTools.Log;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.*;

import java.util.List;

import static cn.travellerr.aronaTools.AronaTools.petConfig;

public class PetManager {
    public static Boolean createPet(Contact subject, User user, String petName, PetType petType) {
        PetInfo oldPetInfo = HibernateFactory.selectOne(PetInfo.class, user.getId());
        if (oldPetInfo != null) {
            subject.sendMessage(new At(user.getId()).plus("您已经有宠物了！"));
            return false;
        }

        long userId = user.getId();

        double userMoney = EconomyUtil.getMoneyByUser(user);

        if (userMoney < petType.getCost()) {
            subject.sendMessage(new At(userId).plus("您的余额不足！所需金额：" + petType.getCost()));
            return false;
        }

        EconomyUtil.plusMoneyToUser(user, -petType.getCost());

        PetInfo petInfo = PetInfo.builder()
                .userId(userId)
                .petName(petName)
                .petType(petType)
                .petMaxHp(petType.getDefaultMaxHp())
                .petHp(Double.valueOf(petType.getDefaultMaxHp()))

                .build();

        Log.debug("petInfoMsg: " + petInfo.getPetName());

        savePetInfo(petInfo);

        return true;
    }

    public static void checkPet(Contact subject, MessageChain msg, Long userId) {
        PetInfo petInfo = HibernateFactory.selectOne(PetInfo.class, userId);
        if (petInfo == null) {
            subject.sendMessage(new QuoteReply(msg).plus("您还没有宠物哦！"));
            return;
        }

        subject.sendMessage(new QuoteReply(msg).plus(petInfo.infoMessage()));
    }

    public static void renamePet(Contact subject, MessageChain msg, User user, String petName) {
        long userId = user.getId();

        double userMoney = EconomyUtil.getMoneyByUser(user);

        if (userMoney < petConfig.getPetRenameMoney()) {
            subject.sendMessage(new QuoteReply(msg).plus("您的余额不足！所需金额：" + petConfig.getPetRenameMoney()));
            return;
        }

        EconomyUtil.plusMoneyToUser(user, -petConfig.getPetRenameMoney());

        PetInfo petInfo = HibernateFactory.selectOne(PetInfo.class, userId);
        if (petInfo == null) {
            subject.sendMessage(new QuoteReply(msg).plus("您还没有宠物哦！"));
            return;
        }

        petInfo.update();
        petInfo.setPetName(petName);
        savePetInfo(petInfo);

        subject.sendMessage(new QuoteReply(msg).plus("宠物重命名成功！"));
    }

    public static void deletePet(Contact subject, MessageChain msg, User user) {
        long userId = user.getId();

        PetInfo petInfo = HibernateFactory.selectOne(PetInfo.class, userId);
        if (petInfo == null) {
            subject.sendMessage(new QuoteReply(msg).plus("您还没有宠物哦！"));
            return;
        }

        int cost = petInfo.getPetType().getCost() * 2;

        if (EconomyUtil.getMoneyByUser(user) < cost) {
            subject.sendMessage(new QuoteReply(msg).plus("您的余额不足！\n所需金额：" + cost));
            return;
        }

        EconomyUtil.plusMoneyToUser(user, -cost);

        HibernateFactory.delete(petInfo);

        subject.sendMessage(new QuoteReply(msg).plus("宠物已删除！"));
    }

    private static void savePetInfo(PetInfo petInfo) {
        HibernateFactory.merge(petInfo);
    }

    public static void sleepPet(Contact subject, MessageChain message, User sender) {
        PetInfo petInfo = HibernateFactory.selectOne(PetInfo.class, sender.getId());
        if (petInfo == null) {
            subject.sendMessage(new QuoteReply(message).plus("您还没有宠物哦！"));
            return;
        }

        petInfo.update();
        petInfo.setIsSleeping(true);
        HibernateFactory.merge(petInfo);

        subject.sendMessage(new QuoteReply(message).plus("宠物已经睡觉！"));
    }

    public static void awakePet(Contact subject, MessageChain message, User sender) {
        PetInfo petInfo = HibernateFactory.selectOne(PetInfo.class, sender.getId());
        if (petInfo == null) {
            subject.sendMessage(new QuoteReply(message).plus("您还没有宠物哦！"));
            return;
        }

        petInfo.setIsSleeping(false);
        HibernateFactory.merge(petInfo);

        subject.sendMessage(new QuoteReply(message).plus("宠物已经醒来！"));
    }

    public static void feedPet(Contact subject, MessageChain message, User sender) {
        PetInfo petInfo = HibernateFactory.selectOne(PetInfo.class, sender.getId());
        if (petInfo == null) {
            subject.sendMessage(new QuoteReply(message).plus("您还没有宠物哦！"));
            return;
        }

        petInfo.update();
        petInfo.setPetHunger(Double.valueOf(petInfo.getPetMaxHunger()));
        HibernateFactory.merge(petInfo);

        subject.sendMessage(new QuoteReply(message).plus("宠物已经吃饱！"));
    }

    public static void cleanPet(Contact subject, MessageChain message, User sender) {
        PetInfo petInfo = HibernateFactory.selectOne(PetInfo.class, sender.getId());
        if (petInfo == null) {
            subject.sendMessage(new QuoteReply(message).plus("您还没有宠物哦！"));
            return;
        }

        petInfo.update();
        petInfo.setPetHealth(Double.valueOf(petInfo.getPetMaxHealth()));
        HibernateFactory.merge(petInfo);

        subject.sendMessage(new QuoteReply(message).plus("宠物已经洗澡！"));
    }

    public static void playWithPet(Contact subject, MessageChain message, User sender) {
        PetInfo petInfo = HibernateFactory.selectOne(PetInfo.class, sender.getId());
        if (petInfo == null) {
            subject.sendMessage(new QuoteReply(message).plus("您还没有宠物哦！"));
            return;
        }

        petInfo.update();
        petInfo.setPetMood(Double.valueOf(petInfo.getPetMaxMood()));
        HibernateFactory.merge(petInfo);

        subject.sendMessage(new QuoteReply(message).plus("宠物玩得很开心！"));
    }

    public static PetInfo getPetInfo(long id) {
        return HibernateFactory.selectOne(PetInfo.class, id);
    }

    public static void getPetList(Contact subject) {
        List<PetType> petTypes = List.of(PetType.values());
        Bot bot = subject.getBot();

        ForwardMessageBuilder builder = new ForwardMessageBuilder(subject);

        for (PetType petType : petTypes) {
            Message message = new PlainText("宠物种类：" + petType.getPetType() + "\n")
                    .plus("价格：" + petType.getCost())
                    .plus("\n")
                    .plus("最大生命值：" + petType.getDefaultMaxHp())
                    .plus("\n")
                    .plus("每分钟变化：" + petType.getValueChangePerMin())
                    .plus("\n")
                    .plus("描述：" + petType.getDescription());
            builder.add(bot, message);
        }

        subject.sendMessage(builder.build());
    }
}
