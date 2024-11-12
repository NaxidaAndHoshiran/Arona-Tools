package cn.travellerr.aronaTools.electronicPets.command;

import cn.travellerr.aronaTools.electronicPets.PetManager;
import cn.travellerr.aronaTools.electronicPets.shop.ShopManager;
import cn.travellerr.aronaTools.electronicPets.task.TaskManager;
import cn.travellerr.aronaTools.electronicPets.type.PetType;
import cn.travellerr.aronaTools.entity.PetInfo;
import cn.travellerr.aronaTools.shareTools.Log;
import kotlin.coroutines.CoroutineContext;
import kotlin.text.Regex;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PetCommandListener extends SimpleListenerHost {

    private final Regex getPetListCommand = BuildCommand.createCommand("获取宠物列表|宠物列表|查看宠物列表|查看宠物种类");

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }
    private final Regex createPetCommand = BuildCommand.createCommand("创建宠物|领养宠物", String.class, String.class);
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

    @EventHandler
    public void onMessage(@NotNull MessageEvent event) {
        Contact subject = event.getSubject();
        User sender = event.getSender();
        MessageChain originalMessage = event.getMessage();
        String message = originalMessage.contentToString();

        if (getPetListCommand.matches(message)) {
            handleGetPetListCommand(subject);

        } else if (createPetWithDefaultNameCommand.matches(message)) {
            handleCreatePetCommand(subject, sender, message, originalMessage, false);
        } else if (createPetCommand.matches(message)) {
            handleCreatePetCommand(subject, sender, message, originalMessage, true);
        } else if (renamePetCommand.matches(message)) {
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
        }
    }

    private void handleGetPetListCommand(Contact subject) {
        PetManager.getPetList(subject);
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
            subject.sendMessage("宠物种类不存在！");
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
        TaskManager.startTask(subject, originalMessage, petInfo, taskId);
    }

    private void handleCheckTaskCommand(Contact subject, User sender, MessageChain originalMessage) {
        PetInfo petInfo = PetManager.getPetInfo(sender.getId());
        TaskManager.getTaskInfo(sender, subject, originalMessage, petInfo);
    }

    private void handleEndTaskCommand(Contact subject, User sender, MessageChain originalMessage) {
        PetInfo petInfo = PetManager.getPetInfo(sender.getId());
        TaskManager.endTask(sender, subject, originalMessage, petInfo);
    }
}