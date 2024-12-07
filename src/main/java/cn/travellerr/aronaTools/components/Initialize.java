package cn.travellerr.aronaTools.components;

import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.ElectronicPetWorkShop;
import cn.travellerr.aronaTools.autoAcceptInvite.CheckInvite;
import cn.travellerr.aronaTools.autoAcceptInvite.SendMenu;
import cn.travellerr.aronaTools.broadcast.BroadCastManager;
import cn.travellerr.aronaTools.broadcast.command.BroadCastCommandListener;
import cn.travellerr.aronaTools.command.RegCommand;
import cn.travellerr.aronaTools.config.Config;
import cn.travellerr.aronaTools.config.MenuConfig;
import cn.travellerr.aronaTools.config.PetConfig;
import cn.travellerr.aronaTools.electronicPets.use.command.PetCommandListener;
import cn.travellerr.aronaTools.electronicPets.use.command.WorkShopCommandListener;
import cn.travellerr.aronaTools.permission.PermissionController;
import cn.travellerr.aronaTools.selectSong.SongCommandListener;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;

public class Initialize {
    public static void init() {
        initConfigs();
        initCommands();
        initListeners();
    }

    private static void initListeners() {
        EventChannel<Event> eventEventChannel = GlobalEventChannel.INSTANCE.parentScope(AronaTools.INSTANCE);
        eventEventChannel.registerListenerHost(new PetCommandListener());
        eventEventChannel.registerListenerHost(new WorkShopCommandListener());
        eventEventChannel.registerListenerHost(new SongCommandListener());
        eventEventChannel.registerListenerHost(new BroadCastCommandListener());
        eventEventChannel.registerListenerHost(new MenuListener());

        // 私用模块，请修改后使用
        GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, CheckInvite::check);
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendAddEvent.class, SendMenu::sendMenuToFriend);

        // 当机器人下线时，关闭所有的群广播线程
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOfflineEvent.class, event -> BroadCastManager.threadList.values().forEach(Thread::interrupt));

        // 收到好友申请
        GlobalEventChannel.INSTANCE.subscribeAlways(NewFriendRequestEvent.class, CheckInvite::checkFriendRequest);
        GlobalEventChannel.INSTANCE.subscribeAlways(BotJoinGroupEvent.class, SendMenu::sendMenuToGroup);
    }

    private static void initConfigs() {

        AronaTools.INSTANCE.reloadPluginConfig(Config.INSTANCE);
        AronaTools.INSTANCE.reloadPluginConfig(PetConfig.INSTANCE);
        AronaTools.INSTANCE.reloadPluginConfig(MenuConfig.INSTANCE);

        AronaTools.config = Config.INSTANCE;
        AronaTools.petConfig = PetConfig.INSTANCE;
        AronaTools.menuConfig = MenuConfig.INSTANCE;

        AronaTools.INSTANCE.reloadPluginData(ElectronicPetWorkShop.INSTANCE);
        AronaTools.electronicPetWorkShop = ElectronicPetWorkShop.INSTANCE;
    }

    private static void initCommands() {
        RegCommand.INSTANCE.registerCommand();
        PermissionController.INSTANCE.regPerm();
    }
}
