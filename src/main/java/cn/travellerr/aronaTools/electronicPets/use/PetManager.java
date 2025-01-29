package cn.travellerr.aronaTools.electronicPets.use;

import cn.chahuyun.economy.utils.EconomyUtil;
import cn.chahuyun.hibernateplus.HibernateFactory;
import cn.travellerr.aronaTools.electronicPets.use.type.PetType;
import cn.travellerr.aronaTools.entity.PetInfo;
import cn.travellerr.aronaTools.shareTools.Log;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.*;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

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
                .valueChangePerMin(petType.getValueChangePerMin())
                .build();

        Log.debug("petInfoMsg: " + petInfo.getPetName());

        savePetInfo(petInfo);

        return true;
    }

    public static void checkPet(Contact subject, MessageChain msg, Long userId) {
        PetInfo petInfo = HibernateFactory.selectOne(PetInfo.class, userId);
        if (petInfo == null) {
            subject.sendMessage(new QuoteReply(msg).plus("您还没有宠物哦！\n请发送 \"#领养宠物 宠物名 宠物种类\" 来创建一个吧！"));
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
            subject.sendMessage(new QuoteReply(msg).plus("您还没有宠物哦！\n请发送 \"#领养宠物 宠物名 宠物种类\" 来创建一个吧！"));
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
            subject.sendMessage(new QuoteReply(msg).plus("您还没有宠物哦！\n请发送 \"#领养宠物 宠物名 宠物种类\" 来创建一个吧！"));
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
            subject.sendMessage(new QuoteReply(message).plus("您还没有宠物哦！\n请发送 \"#领养宠物 宠物名 宠物种类\" 来创建一个吧！"));
            return;
        }
        petInfo.update();
        if (petInfo.getIsSleeping()) {
            subject.sendMessage(new QuoteReply(message).plus("宠物已经在睡觉了！\n请发送 \"#唤醒宠物\" 来唤醒它！"));
            return;
        }
        if (petInfo.getIsDead()) {
            subject.sendMessage(new QuoteReply(message).plus("宠物已经死亡！"));
            return;
        }
        if (petInfo.getTaskId() != 0) {
            subject.sendMessage(new QuoteReply(message).plus("宠物正在执行任务！请先发送 \"#结束任务\" 来结束任务！"));
            return;
        }

        petInfo.setIsSleeping(true);
        HibernateFactory.merge(petInfo);

        subject.sendMessage(new QuoteReply(message).plus("宠物睡着了……"));
    }

    public static void awakePet(Contact subject, MessageChain message, User sender) {
        PetInfo petInfo = HibernateFactory.selectOne(PetInfo.class, sender.getId());
        if (petInfo == null) {
            subject.sendMessage(new QuoteReply(message).plus("您还没有宠物哦！\n请发送 \"#领养宠物 宠物名 宠物种类\" 来创建一个吧！"));
            return;
        }

        petInfo.update();
        petInfo.setIsSleeping(false);
        HibernateFactory.merge(petInfo);

        subject.sendMessage(new QuoteReply(message).plus("宠物缓缓睁开眼睛……"));
    }

    public static PetInfo getPetInfo(long id) {
        return HibernateFactory.selectOne(PetInfo.class, id);
    }

    public static void getPetList(Contact subject, int page) {
        List<PetType> petTypes = Stream.of(PetType.values()).filter(petType -> !petType.getPetType().equals("CUSTOM")).toList();
        Bot bot = subject.getBot();

        ForwardMessageBuilder builder = new ForwardMessageBuilder(subject);

        builder.add(bot, new PlainText("--宠物种类列表(共"+petTypes.size()+"种，第"+page+"页)--"));

        int itemsPerPage = 99;
        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, petTypes.size());

        if (start >= petTypes.size() || start < 0) {
            subject.sendMessage(new PlainText("页码超出范围"));
            return;
        }

        for (int i = start; i < end; i++) {
            PetType petType = petTypes.get(i);
            Message message = new PlainText(petType.getInfo());
            builder.add(bot, message);
        }

        subject.sendMessage(builder.build());
    }

    public static void getPetList(Contact subject) {
        getPetList(subject, 1);
    }

    public static void userMoneyToPetCoin(Contact subject, MessageChain originalMessage, User sender, int money) {
        if (money <= 0) {
            subject.sendMessage(new QuoteReply(originalMessage).plus("转换金额必须大于0！"));
            return;
        }

        double userMoney = EconomyUtil.getMoneyByUser(sender);

        if (userMoney < money) {
            subject.sendMessage(new QuoteReply(originalMessage).plus("您的余额不足！"));
            return;
        }

        PetInfo petInfo = HibernateFactory.selectOne(PetInfo.class, sender.getId());
        if (petInfo == null) {
            subject.sendMessage(new QuoteReply(originalMessage).plus("您还没有宠物哦！\n请发送 \"#领养宠物 宠物名 宠物种类\" 来创建一个吧！"));
            return;
        }

        EconomyUtil.plusMoneyToUser(sender, -money);

        petInfo.update();
        petInfo.addPetCoin(money * petConfig.getExchangePetMoney());
        savePetInfo(petInfo);

        subject.sendMessage(new QuoteReply(originalMessage).plus("转换成功！共转换为技术点：" + money * petConfig.getExchangePetMoney() + "\n当前技术点：" + petInfo.getPetTechPoint()));
    }

    public static boolean addPetCoin(User sender, int petCoin) {
        PetInfo petInfo = HibernateFactory.selectOne(PetInfo.class, sender.getId());
        if (petInfo == null) {
            return false;
        }

        petInfo.addPetCoin(petCoin);
        savePetInfo(petInfo);

        return true;
    }

    public static void searchPet(Contact subject, String petName) {
        List<PetType> petTypeList = Stream.of(PetType.values()).filter(petType -> !petType.getPetType().equals("CUSTOM")).toList();
        for (PetType petType : petTypeList) {
            String lowerPetName = petName.toLowerCase(Locale.ROOT);
            if (petType.toString().toLowerCase(Locale.ROOT).equals(lowerPetName) ||
                petType.getPetType().equals(petName) ||
                Arrays.asList(petType.getAlias()).contains(petName)) {
                subject.sendMessage(new PlainText(petType.getInfo()));
                return;
            }
        }
        subject.sendMessage(new PlainText("未找到宠物：" + petName));
    }
}
