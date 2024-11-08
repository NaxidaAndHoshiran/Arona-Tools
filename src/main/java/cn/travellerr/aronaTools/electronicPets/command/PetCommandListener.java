package cn.travellerr.aronaTools.electronicPets.command;

import cn.travellerr.aronaTools.electronicPets.PetManager;
import cn.travellerr.aronaTools.electronicPets.type.PetType;
import cn.travellerr.aronaTools.shareTools.Log;
import kotlin.text.Regex;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.QuoteReply;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PetCommandListener extends SimpleListenerHost {

    private final Regex createPetCommand = BuildCommand.createCommand("创建宠物|领养宠物", String.class, String.class);

    private final Regex checkPetCommand = BuildCommand.createCommand("查看宠物|我的宠物|宠物信息|宠物详情");

    private final Regex deletePetCommand = BuildCommand.createCommand("删除宠物|抛弃宠物|遗弃宠物");

    private final Regex renamePetCommand = BuildCommand.createCommand("重命名宠物|修改宠物名", String.class);

    private final Regex sleepPetCommand = BuildCommand.createCommand("让宠物休息|让宠物睡觉|让宠物休息一下|宠物休息|宠物睡觉");

    private final Regex awakePetCommand = BuildCommand.createCommand("唤醒宠物|让宠物醒来|让宠物起床|宠物起床");

    private final Regex feedPetCommand = BuildCommand.createCommand("喂食宠物|给宠物喂食|宠物喂食|喂养宠物");

    private final Regex cleanPetCommand = BuildCommand.createCommand("清洁宠物|给宠物洗澡|宠物洗澡");

    private final Regex playPetCommand = BuildCommand.createCommand("给宠物玩耍|宠物玩耍|玩耍宠物|宠物玩耍");

    public PetCommandListener() {
        super();
    }

    @EventHandler
    public void onMessage(@NotNull MessageEvent event) {
        Contact subject = event.getSubject();
        User sender = event.getSender();
        String message = event.getMessage().contentToString();

        // 创建宠物 指令：(创建宠物 宠物名 宠物种类)
        if (createPetCommand.matches(message)) {
            List<String> key = BuildCommand.getEveryValue(createPetCommand, message);

            String petName = key.get(0);
            String petTypeString = key.get(1);

            Log.debug("petName: " + petName + " petTypeString: " + petTypeString);

            PetType petType = PetType.fromString(petTypeString);

            if (petType == null) {
                subject.sendMessage("宠物种类不存在！");
                return;
            }

            boolean success = PetManager.createPet(subject, sender, petName, petType);

            if (!success) {
                return;
            }

            subject.sendMessage(new QuoteReply(event.getMessage()).plus("宠物创建成功！\n名称：" + petName +
                    "\n种类：" + petType.getPetType() +
                    "\n消耗：" + petType.getCost()
                    + "金币"));

            return;
        }

        if (renamePetCommand.matches(message)) {
            List<String> key = BuildCommand.getEveryValue(renamePetCommand, message);

            String petName = key.get(0);

            PetManager.renamePet(subject, event.getMessage(), sender, petName);
            return;
        }

        if (checkPetCommand.matches(message)) {
            PetManager.checkPet(subject, event.getMessage(), sender.getId());
            return;
        }

        if (deletePetCommand.matches(message)) {
            PetManager.deletePet(subject, event.getMessage(), sender);
            return;
        }

        if (sleepPetCommand.matches(message)) {
            PetManager.sleepPet(subject, event.getMessage(), sender);
            return;
        }

        if (awakePetCommand.matches(message)) {
            PetManager.awakePet(subject, event.getMessage(), sender);
            return;
        }

        if (cleanPetCommand.matches(message)) {
            PetManager.cleanPet(subject, event.getMessage(), sender);
            return;
        }

        if (playPetCommand.matches(message)) {
            PetManager.playWithPet(subject, event.getMessage(), sender);
            return;
        }

        if (feedPetCommand.matches(message)) {
            PetManager.feedPet(subject, event.getMessage(), sender);
            return;
        }

    }

}
