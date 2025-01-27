package cn.travellerr.aronaTools.electronicPets.fight;

import cn.travellerr.aronaTools.electronicPets.use.PetManager;
import cn.travellerr.aronaTools.entity.PetInfo;
import cn.travellerr.aronaTools.shareTools.Log;
import cn.travellerr.aronaTools.shareTools.MessageUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FightManager {

    public static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    public static void startFight(Contact subject, MessageChain messages, User user1, Long user2) {
        threadPool.submit(() -> {
            PetInfo pet1 = PetManager.getPetInfo(user1.getId());
            PetInfo pet2 = PetManager.getPetInfo(user2);
            if (pet1 == null || pet2 == null) {
                subject.sendMessage(MessageUtil.quoteReply(messages, "对方或您没有宠物！"));
                return;
            }
            FightPet fightPet1 = new FightPet(pet1, user1);
            FightPet fightPet2 = new FightPet(pet2, user2);
            BattleGround battleGround = new BattleGround(fightPet1, fightPet2);

            String start = battleGround.startBattle();

            start = start.replace("%user1%", user1.getNick());
            start = start.replace("%user2%", user2.toString());

            start = start.replace("%@user1%", "[mirai:at:" + user1.getId() + "]");
            start = start.replace("%@user2%", "[mirai:at:" + user2 + "]");

            subject.sendMessage(MessageUtil.quoteReply(messages, MiraiCode.deserializeMiraiCode(start)));

            Log.info("开始战斗");

            ForwardMessageBuilder builder = new ForwardMessageBuilder(subject);
            Bot bot = subject.getBot();

            while(true) {
                // 切换到下一个宠物
                String nextOne = battleGround.nextOne();
                if (nextOne.contains("-1")) {
                    break;
                }

                nextOne = nextOne.replace("%@user1%", "[mirai:at:" + battleGround.getNowPet().getUser() + "]");

                // 检查是否被控制
                if (battleGround.getNowPet().isControlled()) {
                    builder.add(bot, new PlainText("[mirai:at:" + battleGround.getNowPet().getUser() + "] 被控制，跳过回合。\n"));
                    Log.debug("被控制，跳过回合");
                    battleGround.getNowPet().setControlled(false);
                } else {
                    // subject.sendMessage(MessageUtil.quoteReply(messages, "第" + battleGround.nextRound() + "回合\n").plus(MiraiCode.deserializeMiraiCode(nextOne)));
                    builder.add(bot, new PlainText("第" + battleGround.nextRound() + "回合").plus(MiraiCode.deserializeMiraiCode(nextOne)));
                    Log.debug("新的回合");

                    // 自动攻击
                    // subject.sendMessage(battleGround.autoAttack());
                    builder.add(bot, new PlainText(battleGround.autoAttack().replace("%user1%", battleGround.getNowPet().getUser().toString())).plus("\n"));
                    Log.debug("自动攻击");
                }

                // 切换到下一个宠物
                nextOne = battleGround.nextOne();
                if (nextOne.contains("-1")) {
                    break;
                }
                nextOne = nextOne.replace("%@user1%", "[mirai:at:" + battleGround.getNowPet().getUser() + "]");

                // 检查是否被控制
                if (battleGround.getNowPet().isControlled()) {
                    builder.add(bot, new PlainText("[mirai:at:" + battleGround.getNowPet().getUser() + "] 被控制，跳过回合。\n"));
                    Log.debug("被控制，跳过回合");
                    battleGround.getNowPet().setControlled(false);
                } else {
                    // subject.sendMessage(MessageUtil.quoteReply(messages, MiraiCode.deserializeMiraiCode(nextOne)));
                    builder.add(bot, MiraiCode.deserializeMiraiCode(nextOne));
                    Log.debug("对方回合");

                    // 自动攻击
                    // subject.sendMessage(battleGround.autoAttack());
                    builder.add(bot, new PlainText(battleGround.autoAttack().replace("%user1%", battleGround.getNowPet().getUser().toString())).plus("\n"));
                    Log.debug("自动攻击");
                }

                if (builder.size() >= 88) {
                    subject.sendMessage(builder.build());
                    builder = new ForwardMessageBuilder(subject);
                }
            }
            // 结束战斗并输出结果
            // subject.sendMessage(battleGround.endBattle(null));
            Log.debug("战斗结束");
                builder.add(bot, new PlainText(battleGround.endBattle(null)));
            Log.debug("发送消息");
            try {
                subject.sendMessage(builder.build());
            } catch (Exception e) {
                Log.error("发送消息时出现错误", e);
            }
        });
    }



}
