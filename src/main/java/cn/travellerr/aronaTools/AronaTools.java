package cn.travellerr.aronaTools;

import cn.travellerr.aronaTools.autoAcceptInvite.CheckInvite;
import cn.travellerr.aronaTools.command.RegCommand;
import cn.travellerr.aronaTools.config.Config;
import cn.travellerr.aronaTools.permission.PermissionController;
import cn.travellerr.aronaTools.shareTools.HibernateUtil;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;

public final class AronaTools extends JavaPlugin {
    public static final AronaTools INSTANCE = new AronaTools();

    public static Config config;

    private AronaTools() {
        super(new JvmPluginDescriptionBuilder("cn.travellerr.aronaTools.AronaTools", "0.1.0")
                .name("AronaTools")
                .author("Travellerr")
                .dependsOn("cn.chahuyun.HuYanEconomy", true)
                .dependsOn("cn.travellerr.Favorability", true)
                .build());
    }

    @Override
    public void onEnable() {
        GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, CheckInvite::check);
        reloadPluginConfig(Config.INSTANCE);
        config = Config.INSTANCE;

        HibernateUtil.init(this);

        getLogger().info("插件已加载");
        RegCommand.INSTANCE.registerCommand();
        PermissionController.INSTANCE.regPerm();

    }
}





