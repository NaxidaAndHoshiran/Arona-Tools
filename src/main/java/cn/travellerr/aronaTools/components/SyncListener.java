package cn.travellerr.aronaTools.components;

import cn.travellerr.aronaTools.shareTools.BuildCommand;
import kotlin.coroutines.CoroutineContext;
import kotlin.text.Regex;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SyncListener extends SimpleListenerHost {
    private static final Regex syncCommand = BuildCommand.createCommand("同步数据|sync|syncData|数据同步", Long.class);

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }

    @EventHandler
    public void onMessage(@NotNull MessageEvent event) {
        String message = event.getMessage().contentToString().strip();
        if (syncCommand.matches(message)) {
            List<String> params = BuildCommand.getEveryValue(syncCommand, message);
            Long targetId = Long.parseLong(params.get(0));

            Sync.sync(event.getSubject(), event.getMessage(), event.getSender(), targetId);
        }
    }
}
