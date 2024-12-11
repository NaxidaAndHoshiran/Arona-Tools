package cn.travellerr.aronaTools.totp;

import cn.chahuyun.hibernateplus.HibernateFactory;
import cn.travellerr.aronaTools.entity.TotpInfo;
import cn.travellerr.aronaTools.shareTools.Log;
import kotlin.text.Regex;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Totp {
    public static final Pattern PATTERN = Pattern.compile("otpauth://totp/(\\S+)\\?secret=(\\S+)&issuer=(\\S+)");

    public static String use(long userId, String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("name", name);
        TotpInfo info = HibernateFactory.selectOne(TotpInfo.class, map);
        return "你的 " + name + " OTP 为：" + info.generateTotp();
    }

    public static String register(String content, long userId) throws Exception {
        Regex regex = new Regex(PATTERN);

        if (regex.matches(content)) {
            Log.info("链接有效！");
            List<String> values = TotpInfo.getEveryValue(regex, content);

            List<TotpInfo> totpInfoList = HibernateFactory.selectList(TotpInfo.class);
            TotpInfo info = (totpInfoList != null ? totpInfoList.stream().filter(i -> i.getUserId().equals(userId) && i.getName().equals(values.get(0))).findFirst().orElse(null) : null);
            if (info != null) {
                Log.info("已存在！");
                return "你的 " + info.getName() + " 账户已存在！";
            }

            SecretKey secretKey = EncryptionUtil.generateKey();
            String keyStr = EncryptionUtil.keyToString(secretKey);
            String encrypted = EncryptionUtil.encrypt(values.get(1).toUpperCase(), secretKey);
            Log.debug("加密后: " + encrypted);
            Thread.sleep(1000);
            SecretKey sameKey = EncryptionUtil.stringToKey(keyStr);
            String decrypted = EncryptionUtil.decrypt(encrypted, sameKey);
            Log.debug("解密后: " + decrypted);

            TotpInfo totpInfo = TotpInfo.builder()
                    .userId(userId)
                    .name(values.get(0))
                    .encryptedSecret(encrypted)
                    .encrypt(keyStr)
                    .issuer(values.get(2))
                    .build();
            HibernateFactory.merge(totpInfo);
            String otp = totpInfo.generateTotp();
            return "你的 " + values.get(0) + " 账户已注册！\n" +
                    "密钥：" + keyStr + "\n" +
                    "OTP：" + otp;
        } else {
            Log.info("链接无效！");
            return "你提供的二维码/链接无效哦！";
        }
    }
}