package cn.travellerr.aronaTools;

import cn.travellerr.aronaTools.autoAcceptInvite.CheckInvite;
import cn.travellerr.aronaTools.autoAcceptInvite.SendMenu;
import cn.travellerr.aronaTools.command.RegCommand;
import cn.travellerr.aronaTools.config.Config;
import cn.travellerr.aronaTools.config.PetConfig;
import cn.travellerr.aronaTools.electronicPets.command.PetCommandListener;
import cn.travellerr.aronaTools.electronicPets.command.WorkShopCommandListener;
import cn.travellerr.aronaTools.permission.PermissionController;
import cn.travellerr.aronaTools.shareTools.HibernateUtil;
import cn.travellerr.aronaTools.shareTools.Log;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.BotJoinGroupEvent;
import net.mamoe.mirai.event.events.FriendAddEvent;

public final class AronaTools extends JavaPlugin {
    public static final AronaTools INSTANCE = new AronaTools();

    public static Config config;

    public static PetConfig petConfig;

    public static ElectronicPetWorkShop electronicPetWorkShop;

    private AronaTools() {
        super(new JvmPluginDescriptionBuilder("cn.travellerr.aronaTools.AronaTools", "0.2.0")
                .name("AronaTools")
                .author("Travellerr")
                .dependsOn("cn.chahuyun.HuYanEconomy", true)
                .dependsOn("cn.travellerr.Favorability", true)
                .build());
    }

    @Override
    public void onEnable() {
        reloadPluginConfig(Config.INSTANCE);
        reloadPluginConfig(PetConfig.INSTANCE);
        config = Config.INSTANCE;
        petConfig = PetConfig.INSTANCE;

        reloadPluginData(ElectronicPetWorkShop.INSTANCE);
        electronicPetWorkShop = ElectronicPetWorkShop.INSTANCE;

        HibernateUtil.init(this);

        EventChannel<Event> eventEventChannel = GlobalEventChannel.INSTANCE.parentScope(AronaTools.INSTANCE);
        eventEventChannel.registerListenerHost(new PetCommandListener());
        eventEventChannel.registerListenerHost(new WorkShopCommandListener());

        Log.info("插件已加载");
        RegCommand.INSTANCE.registerCommand();
        PermissionController.INSTANCE.regPerm();

        // 私用模块，请修改后使用
        GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, CheckInvite::check);
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendAddEvent.class, SendMenu::sendMenuToFriend);
        GlobalEventChannel.INSTANCE.subscribeAlways(BotJoinGroupEvent.class, SendMenu::sendMenuToGroup);


    }
}





