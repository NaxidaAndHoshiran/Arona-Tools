package cn.travellerr.aronaTools.selectSong;

import cn.chahuyun.economy.utils.EconomyUtil;
import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.permission.PermissionController;
import cn.travellerr.aronaTools.selectSong.netease.NeteaseApi;
import cn.travellerr.aronaTools.shareTools.BuildCommand;
import cn.travellerr.aronaTools.shareTools.Log;
import kotlin.coroutines.CoroutineContext;
import kotlin.text.Regex;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

public class SongCommandListener extends SimpleListenerHost {
    private static final Regex SEARCH_SONG = BuildCommand.createCommand("点歌", String.class);
    private static final Regex SELECT_SONG = BuildCommand.createCommand("选择|选曲|选取|选歌|听歌|听", Integer.class);

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }

    @EventHandler
    public void onMessage(@NotNull MessageEvent event) {
        String message = event.getMessage().contentToString();
        if (SEARCH_SONG.matches(message)) {
            Log.info("点歌指令");


            if (!PermissionController.hasPermission(event.getSender(), PermissionController.searchSongBypassPermission)) {
                double userMoney = EconomyUtil.getMoneyByUser(event.getSender());
                if (userMoney < AronaTools.config.getSongMoney()) {
                    event.getSubject().sendMessage("余额不足，无法点歌");
                    return;
                }

                EconomyUtil.plusMoneyToUser(event.getSender(), -AronaTools.config.getSongMoney());
            }

            String songName = BuildCommand.getEveryValue(SEARCH_SONG, message).get(0);
            NeteaseApi.SearchNeteaseSong(event.getSubject(), event.getMessage(), event.getSender(), songName, SELECT_SONG);
        }
    }

}
