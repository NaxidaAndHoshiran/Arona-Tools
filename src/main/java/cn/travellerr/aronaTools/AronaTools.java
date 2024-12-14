package cn.travellerr.aronaTools;

import cn.travellerr.aronaTools.broadcast.BroadCastManager;
import cn.travellerr.aronaTools.components.Initialize;
import cn.travellerr.aronaTools.config.Config;
import cn.travellerr.aronaTools.config.MenuConfig;
import cn.travellerr.aronaTools.config.PetConfig;
import cn.travellerr.aronaTools.shareTools.HibernateUtil;
import cn.travellerr.aronaTools.shareTools.Log;
import cn.travellerr.aronaTools.wordle.WordleManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

public final class AronaTools extends JavaPlugin {
    public static final AronaTools INSTANCE = new AronaTools();

    public static Config config;

    public static PetConfig petConfig;

    public static MenuConfig menuConfig;

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

        Initialize.init();

        HibernateUtil.init(this);


        Log.info("插件已加载");




    }

    @Override
    public void onDisable() {
        BroadCastManager.threadList.forEach((date, thread) -> thread.interrupt());
        WordleManager.executorService.shutdownNow();
        Log.info("插件已卸载");
    }
}





