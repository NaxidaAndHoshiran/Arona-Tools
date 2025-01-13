package cn.travellerr.aronaTools.shareTools;


import cn.travellerr.aronaTools.AronaTools;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GFont {

    /**
     * 字体，默认黑体，大小45，粗体
     */
    public static Font font;

    static {
        init();
    }

    public static void init() {
            try {
                File file = AronaTools.INSTANCE.resolveDataFile("fonts/Maple UI.ttf");
                try (InputStream fontStream = new FileInputStream(file)) {
                    font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                }
            } catch (FontFormatException | IOException e) {
                throw new RuntimeException(e);
            }
            font = font.deriveFont(Font.BOLD, 45);
//            Log.info("字体 黑体.ttf 已加载");
    }
}

