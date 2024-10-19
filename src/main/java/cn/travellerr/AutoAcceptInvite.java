package cn.travellerr;

import cn.travellerr.checkPerm.Check;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.data.RequestEventData;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;

public final class AutoAcceptInvite extends JavaPlugin {
    public static final AutoAcceptInvite INSTANCE = new AutoAcceptInvite();

    private AutoAcceptInvite() {
        super(new JvmPluginDescriptionBuilder("cn.travellerr.Arona-AutoAcceptInvite", "0.1.0")
                .name("Arona-AutoAcceptInvite")
                .author("Travellerr")
                .dependsOn("cn.chahuyun.HuYanEconomy", true)
                .dependsOn("cn.travellerr.Favorability", true)
                .build());
    }

    @Override
    public void onEnable() {
        GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, Check::check);
    }
}
