package cn.travellerr.aronaTools.shareTools;

import cn.travellerr.aronaTools.AronaTools;
import net.mamoe.mirai.utils.MiraiLogger;

import java.io.IOException;

public class Log {
    private static final MiraiLogger log = AronaTools.INSTANCE.getLogger();

    public static void info(Object msg) {
        log.info(String.valueOf(msg));
    }

    public static void warning(Object msg) {
        log.warning(String.valueOf(msg));
    }

    public static void error(Object msg, IOException e) {
        log.error(String.valueOf(msg), e);
    }

    public static void error(Object msg, Throwable e) {
        log.error(String.valueOf(msg), e);
    }

    public static void error(Object msg) {
        log.error(String.valueOf(msg));
    }

    public static void debug(Object msg) {
        log.debug(String.valueOf(msg));
    }
}
