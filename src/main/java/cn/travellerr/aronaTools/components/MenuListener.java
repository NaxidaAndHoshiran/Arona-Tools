package cn.travellerr.aronaTools.components;

import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.shareTools.Log;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.utils.ExternalResource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class MenuListener extends SimpleListenerHost {
    @EventHandler
    public void onMessage(@NotNull MessageEvent event) {
        String message = event.getMessage().contentToString().toLowerCase(Locale.ROOT);
        Contact subject = event.getSubject();
        MessageChain messageChain = event.getMessage();

        int index = AronaTools.menuConfig.getCommands().stream()
                .filter(message::matches)
                .findFirst()
                .map(AronaTools.menuConfig.getCommands()::indexOf)
                .orElse(-1);

        if (index == -1) return;

        Path menuPath = AronaTools.INSTANCE.getDataFolderPath().resolve("menu");
        if (!menuPath.toFile().exists()) {
            Log.error("菜单文件夹不存在,正在创建……");
            menuPath.toFile().mkdir();
        }

        File file = AronaTools.INSTANCE.getDataFolderPath().resolve(AronaTools.menuConfig.getMenus().get(index)).toFile();

        if (file.isDirectory()) {
        Log.info("该菜单为文件夹，正在随机选取图片……");
        File[] files = Arrays.stream(Optional.ofNullable(file.listFiles()).orElse(new File[0]))
            .filter(f -> f != null && f.isFile() && isValidImageFile(f))
            .toArray(File[]::new);

        if (files.length == 0) {
            Log.error("文件夹为空，无法发送图片");
            subject.sendMessage(new QuoteReply(messageChain).plus("文件夹为空，无法发送图片"));
            return;
        }
        file = files[(int) (Math.random() * files.length)];
    }

    try (ExternalResource externalResource = ExternalResource.create(file)) {
        Image image = subject.uploadImage(externalResource);
        subject.sendMessage(image);
    } catch (Exception e) {
        Log.error("出错啦~", e);
        subject.sendMessage(new QuoteReply(messageChain).plus("出错啦~,请联系主人检查后台哦"));
    }
}

private boolean isValidImageFile(File file) {
    try (FileInputStream fis = new FileInputStream(file)) {
        byte[] header = new byte[8];
        fis.read(header, 0, header.length);
        String fileType = getFileType(header);
        return fileType.equals("jpg") || fileType.equals("jpeg") || fileType.equals("png") || fileType.equals("gif");
    } catch (IOException e) {
        Log.error("Error reading file header", e);
        return false;
    }
}

    private String getFileType(byte[] header) {
        if (header.length < 8) return "";
        if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) return "jpg";
        if (header[0] == (byte) 0x89 && header[1] == (byte) 0x50 && header[2] == (byte) 0x4E && header[3] == (byte) 0x47) return "png";
        if (header[0] == (byte) 0x47 && header[1] == (byte) 0x49 && header[2] == (byte) 0x46 && header[3] == (byte) 0x38) return "gif";
        return "";
    }
}
