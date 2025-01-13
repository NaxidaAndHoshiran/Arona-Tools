package cn.travellerr.aronaTools;

import cn.travellerr.aronaTools.broadcast.BroadCastManager;
import cn.travellerr.aronaTools.components.ForwardMessageTransformer;
import cn.travellerr.aronaTools.components.Initialize;
import cn.travellerr.aronaTools.config.Config;
import cn.travellerr.aronaTools.config.MenuConfig;
import cn.travellerr.aronaTools.config.PetConfig;
import cn.travellerr.aronaTools.config.ServiceConfig;
import cn.travellerr.aronaTools.shareTools.HibernateUtil;
import cn.travellerr.aronaTools.shareTools.Log;
import cn.travellerr.aronaTools.wordle.WordleManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.MessagePreSendEvent;
import net.mamoe.mirai.message.data.ForwardMessage;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class AronaTools extends JavaPlugin {
    public static final AronaTools INSTANCE = new AronaTools();

    public static Config config;

    public static PetConfig petConfig;

    public static MenuConfig menuConfig;

    public static ServiceConfig serviceConfig;

    public static ElectronicPetWorkShop electronicPetWorkShop;

    private AronaTools() {
        super(new JvmPluginDescriptionBuilder("cn.travellerr.aronaTools.AronaTools", "0.2.0")
                .name("AronaTools")
                .author("Travellerr")
                .dependsOn("cn.chahuyun.HuYanEconomy", true)
                .dependsOn("cn.travellerr.Favorability", true)
                .dependsOn("cn.travellerr.qzone.Qzone", true)
                .build());
    }

    @Override
    public void onEnable() {

        Initialize.init();

        HibernateUtil.init(this);

        Log.info("插件已加载");

        GlobalEventChannel.INSTANCE.subscribeAlways(MessagePreSendEvent.class, event -> {
            Log.info("消息种类: " + event.getMessage().getClass().getSimpleName());

            //event.setMessage(event.getMessage().plus(new PlainText("\n阿洛娜即将停止服务，若有需要请加入 什亭之匣 QQ群，详情请见阿洛娜QQ空间")));
            if (event.getMessage() instanceof ForwardMessage forwardMessage) {

                BufferedImage bufferedImage = ForwardMessageTransformer.drawForwardMessage(forwardMessage);


                try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {


                    ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                    ImageIO.write(ImageIO.read(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())), "png", AronaTools.INSTANCE.resolveDataFile("forwardMessage.png"));

                    try (InputStream stream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());ExternalResource resource = ExternalResource.create(stream)) {
                        Image image = event.getTarget().uploadImage(resource);
                        event.cancel();

                        event.getTarget().sendMessage(image);

                    }
                    Log.info("转发消息已转换为图片");

                } catch (IOException e) {
                    Log.error("转发消息转换为图片失败", e);
                    throw new RuntimeException(e);
                }

            }

/*            if (event.getMessage() instanceof MessageChain messages) {

                String str = messages.serializeToMiraiCode().replaceAll("\\[mirai:at:(\\d+)]", "<qqbot-at-user id=\"$1\" />");
                event.setMessage(MessageChain.deserializeFromMiraiCode(str, event.getTarget()));
            }*/
        });




    }

    @Override
    public void onDisable() {
        BroadCastManager.threadList.forEach((date, thread) -> thread.interrupt());
        WordleManager.threadPoolExecutor.shutdownNow();
        Log.info("插件已卸载");
    }
}





