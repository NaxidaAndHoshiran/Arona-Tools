package cn.travellerr.aronaTools.totp;

import cn.chahuyun.hibernateplus.HibernateFactory;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.travellerr.aronaTools.entity.TotpInfo;
import cn.travellerr.aronaTools.shareTools.Log;
import cn.travellerr.aronaTools.shareTools.MessageUtil;
import kotlin.text.Regex;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class TotpManager {
    public static void use(User user, MessageChain messages, Contact subject) {
        try {
            subject.sendMessage(new QuoteReply(messages).plus("请在30秒内发送二维码图片或认证链接"));

            MessageEvent nextEvent = MessageUtil.getNextMessageEventFromUser(user, subject, 30, java.util.concurrent.TimeUnit.SECONDS);
            if (nextEvent == null) {
                subject.sendMessage(new QuoteReply(messages).plus("操作超时"));
                return;
            }

            // 判断是否为图片，获取图片实例
            Image image = nextEvent.getMessage().stream().filter(msg -> msg instanceof Image).map(msg -> (Image) msg).findFirst().orElse(null);

            String input = nextEvent.getMessage().contentToString();
            if (image == null && !new Regex(Totp.PATTERN).matches(input)) {
                subject.sendMessage(new QuoteReply(messages).plus("请发送二维码图片或认证链接"));
                return;
            }

            String content;

            if (image != null) {
                String url = Image.queryUrl(image);
                Log.info("图片id: " + image.getImageId());
                url = url.replace("appid=1407","appid=1406");
                Log.info("图片url: " + url);
                try (
                        InputStream stream = new URL(url).openStream()) {
                    content = QrCodeUtil.decode(stream);
                    Log.debug(content);
                }
            } else {
                content = input;
            }
            String msg = Totp.register(content, user.getId());
            subject.sendMessage(new QuoteReply(messages).plus(msg));
        } catch (Exception e) {
            Log.error("出错啦~", e);
            subject.sendMessage(new QuoteReply(messages).plus("出错啦~原因: "+e.getMessage()));
        }
    }

    public static void delete(User user, String name, MessageChain messages, Contact subject) {
        List<TotpInfo> totpInfoList = HibernateFactory.selectList(TotpInfo.class);
        TotpInfo info = (totpInfoList != null ? totpInfoList.stream().filter(i -> i.getUserId().equals(user.getId()) && i.getName().equals(name)).findFirst().orElse(null) : null);
        if (info == null) {
            subject.sendMessage(new QuoteReply(messages).plus("不存在该账户"));
            return;
        }
        HibernateFactory.delete(info);

        subject.sendMessage(new QuoteReply(messages).plus("删除成功"));
    }

    public static void list(User user, Contact subject) {

        List<TotpInfo> totpInfoList = HibernateFactory.selectList(TotpInfo.class, "userId", user.getId());

        ForwardMessageBuilder builder = new ForwardMessageBuilder(subject);

        Bot bot = subject.getBot();

        totpInfoList.forEach(totpInfo -> builder.add(bot, new PlainText(totpInfo.info())));

        subject.sendMessage(builder.build());
    }

    public static void get(User user, String name, MessageChain chain, Contact subject) {
        List<TotpInfo> totpInfoList = HibernateFactory.selectList(TotpInfo.class);
        TotpInfo info = (totpInfoList != null ? totpInfoList.stream().filter(i -> i.getUserId().equals(user.getId()) && i.getName().equals(name)).findFirst().orElse(null) : null);
        if (info == null) {
            subject.sendMessage(new QuoteReply(chain).plus("不存在该账户"));
            return;
        }
        subject.sendMessage(new QuoteReply(chain).plus(Totp.use(user.getId(), info.getName())));
    }

}
