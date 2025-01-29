package cn.travellerr.aronaTools.electronicPets.use.command;

import cn.travellerr.aronaTools.electronicPets.use.PetManager;
import cn.travellerr.aronaTools.electronicPets.use.shop.ShopManager;
import cn.travellerr.aronaTools.electronicPets.use.task.TaskManager;
import cn.travellerr.aronaTools.electronicPets.use.type.PetType;
import cn.travellerr.aronaTools.entity.PetInfo;
import cn.travellerr.aronaTools.shareTools.BuildCommand;
import cn.travellerr.aronaTools.shareTools.Log;
import cn.travellerr.aronaTools.shareTools.MessageUtil;
import kotlin.coroutines.CoroutineContext;
import kotlin.text.Regex;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PetCommandListener extends SimpleListenerHost {

    private final Regex getPetListCommand = BuildCommand.createCommand("获取宠物列表|宠物列表|查看宠物列表|查看宠物种类");
    private final Regex getPetListByPageCommand = BuildCommand.createCommand("获取宠物列表|宠物列表|查看宠物列表|查看宠物种类", Integer.class);
    private final Regex searchPetCommand = BuildCommand.createCommand("搜索宠物|查找宠物", String.class);
    private final Regex createPetCommand = BuildCommand.createCommand("创建宠物|领养宠物", String.class, String.class);
    private final Regex createPetWithMemberCommand = BuildCommand.createCommand("创建宠物|领养宠物", At.class);
    private final Regex createPetWithDefaultNameCommand = BuildCommand.createCommand("创建宠物|领养宠物", String.class);
    private final Regex checkPetCommand = BuildCommand.createCommand("查看宠物|我的宠物|宠物信息|宠物详情");
    private final Regex deletePetCommand = BuildCommand.createCommand("删除宠物|抛弃宠物|遗弃宠物");
    private final Regex renamePetCommand = BuildCommand.createCommand("重命名宠物|修改宠物名", String.class);
    private final Regex sleepPetCommand = BuildCommand.createCommand("让宠物休息|让宠物睡觉|让宠物休息一下|宠物休息|宠物睡觉");
    private final Regex awakePetCommand = BuildCommand.createCommand("唤醒宠物|让宠物醒来|让宠物起床|宠物起床");
    private final Regex startTaskCommand = BuildCommand.createCommand("开始任务|接受任务", Integer.class);
    private final Regex checkTaskCommand = BuildCommand.createCommand("查看任务|任务详情|任务信息");
    private final Regex endTaskCommand = BuildCommand.createCommand("结束任务|放弃任务|取消任务");
    private final Regex buyItemCommand = BuildCommand.createCommand("购买物品|购买道具", Integer.class);
    private final Regex buyItemsOneTimeCommand = BuildCommand.createCommand("购买物品|购买道具", Integer.class, Integer.class);

    private final Regex userMoneyToPetCoinCommand = BuildCommand.createCommand("金币兑换|兑换科技点|兑换|转金币", Integer.class);


    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }

    @EventHandler
    public void onMessage(@NotNull MessageEvent event) {
        Contact subject = event.getSubject();
        User sender = event.getSender();
        MessageChain originalMessage = event.getMessage();
        String message = originalMessage.contentToString().strip();

        if (getPetListCommand.matches(message)) {
            handleGetPetListCommand(subject);
        } else if (getPetListByPageCommand.matches(message)) {
            handleGetPetListByPageCommand(subject, message);
        } else if (searchPetCommand.matches(message)) {
            List<String> key = BuildCommand.getEveryValue(searchPetCommand, message);
            PetManager.searchPet(subject, key.get(0));
        } else if (createPetWithMemberCommand.matches(message)) {
            At at = originalMessage.stream().filter(At.class::isInstance).map(At.class::cast).findFirst().orElse(null);
            if (!(event instanceof GroupMessageEvent groupMessageEvent)) {
                subject.sendMessage(MessageUtil.quoteReply(originalMessage, "只能在群聊中使用@功能哦"));
                return;
            }
            if (at == null) {
                subject.sendMessage(MessageUtil.quoteReply(originalMessage, "请@一个成员"));
                return;
            }

            String petName = at.getDisplay(groupMessageEvent.getGroup());
            boolean success = PetManager.createPet(subject, sender, petName, PetType.CUSTOM);

            if (!success) return;

            subject.sendMessage(new QuoteReply(originalMessage).plus("宠物创建成功！\n名称：" + petName +
                    "\n消耗：1000 金币"));

        } else if (createPetWithDefaultNameCommand.matches(message)) {
            handleCreatePetCommand(subject, sender, message, originalMessage, false);
        } else if (createPetCommand.matches(message)) {
            handleCreatePetCommand(subject, sender, message, originalMessage, true);
        }  else if (renamePetCommand.matches(message)) {
            handleRenamePetCommand(subject, sender, message, originalMessage);
        } else if (checkPetCommand.matches(message)) {
            PetManager.checkPet(subject, originalMessage, sender.getId());
        } else if (deletePetCommand.matches(message)) {
            PetManager.deletePet(subject, originalMessage, sender);
        } else if (sleepPetCommand.matches(message)) {
            PetManager.sleepPet(subject, originalMessage, sender);
        } else if (awakePetCommand.matches(message)) {
            PetManager.awakePet(subject, originalMessage, sender);
        } else if (startTaskCommand.matches(message)) {
            handleStartTaskCommand(subject, sender, message, originalMessage);
        } else if (checkTaskCommand.matches(message)) {
            handleCheckTaskCommand(subject, sender, originalMessage);
        } else if (endTaskCommand.matches(message)) {
            handleEndTaskCommand(subject, sender, originalMessage);
        } else if (buyItemCommand.matches(message)) {
            handleBuyItemCommand(subject, sender, message, originalMessage);
        } else if (buyItemsOneTimeCommand.matches(message)) {
            List<String> key = BuildCommand.getEveryValue(buyItemsOneTimeCommand, message);
            int itemId = Integer.parseInt(key.get(0));
            int count = Integer.parseInt(key.get(1));
            PetInfo petInfo = PetManager.getPetInfo(sender.getId());
            ShopManager.buyItem(subject, originalMessage, sender, petInfo, itemId, count);
        }else if (userMoneyToPetCoinCommand.matches(message)) {
            List<String> key = BuildCommand.getEveryValue(userMoneyToPetCoinCommand, message);
            int money = Integer.parseInt(key.get(0));
            PetManager.userMoneyToPetCoin(subject, originalMessage, sender, money);
        }
    }

    private void handleGetPetListCommand(Contact subject) {
        PetManager.getPetList(subject);
    }

    private void handleGetPetListByPageCommand(Contact subject, String messages) {
        List<String> key = BuildCommand.getEveryValue(getPetListByPageCommand, messages);
        int page = Integer.parseInt(key.get(0));
        PetManager.getPetList(subject, page);
    }

    private void handleBuyItemCommand(Contact subject, User sender, String message, MessageChain originalMessage) {
        List<String> key = BuildCommand.getEveryValue(buyItemCommand, message);
        int itemId = Integer.parseInt(key.get(0));
        PetInfo petInfo = PetManager.getPetInfo(sender.getId());
        ShopManager.buyItem(subject, originalMessage, sender, petInfo, itemId);
    }

    private void handleCreatePetCommand(Contact subject, User sender, String message, MessageChain originalMessage, boolean hasName) {
        List<String> key = BuildCommand.getEveryValue(hasName ? createPetCommand : createPetWithDefaultNameCommand, message);
        String petTypeString = hasName ? key.get(1) : key.get(0);
        String petName = hasName ? key.get(0) : petTypeString;
        Log.debug("petName: " + petName + " petTypeString: " + petTypeString);
        PetType petType = PetType.fromString(petTypeString);

        if (petType == null) {
            subject.sendMessage("宠物种类不存在！\n请发送 \"#宠物列表\" 查看宠物种类");
            return;
        }

        boolean success = PetManager.createPet(subject, sender, petName, petType);
        if (!success) return;

        subject.sendMessage(new QuoteReply(originalMessage).plus("宠物创建成功！\n名称：" + petName +
                "\n种类：" + petType.getPetType() +
                "\n消耗：" + petType.getCost() + "金币"));
    }

    private void handleRenamePetCommand(Contact subject, User sender, String message, MessageChain originalMessage) {
        List<String> key = BuildCommand.getEveryValue(renamePetCommand, message);
        String petName = key.get(0);
        PetManager.renamePet(subject, originalMessage, sender, petName);
    }

    private void handleStartTaskCommand(Contact subject, User sender, String message, MessageChain originalMessage) {
        List<String> key = BuildCommand.getEveryValue(startTaskCommand, message);
        int taskId = Integer.parseInt(key.get(0));
        PetInfo petInfo = PetManager.getPetInfo(sender.getId());
        if (checkHasNotPet(subject, sender, originalMessage)) return;
        TaskManager.startTask(subject, originalMessage, petInfo, taskId);
    }

    private void handleCheckTaskCommand(Contact subject, User sender, MessageChain originalMessage) {
        PetInfo petInfo = PetManager.getPetInfo(sender.getId());
        if (checkHasNotPet(subject, sender, originalMessage)) return;
        TaskManager.getTaskInfo(subject, originalMessage, petInfo);
    }

    private void handleEndTaskCommand(Contact subject, User sender, MessageChain originalMessage) {
        PetInfo petInfo = PetManager.getPetInfo(sender.getId());
        if (checkHasNotPet(subject, sender, originalMessage)) return;
        TaskManager.endTask(subject, originalMessage, petInfo);
    }

    private boolean checkHasNotPet(Contact subject, User sender, MessageChain originalMessage) {
        PetInfo petInfo = PetManager.getPetInfo(sender.getId());
        if (petInfo == null) {
            subject.sendMessage(new QuoteReply(originalMessage).plus("您还没有宠物哦！"));
            return true;
        }
        return false;
    }
}