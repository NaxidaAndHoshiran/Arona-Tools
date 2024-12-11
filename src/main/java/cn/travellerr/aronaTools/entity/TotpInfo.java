package cn.travellerr.aronaTools.entity;

import cn.hutool.core.codec.Base32;
import cn.travellerr.aronaTools.shareTools.Log;
import cn.travellerr.aronaTools.totp.EncryptionUtil;
import jakarta.persistence.*;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import lombok.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TotpInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String name;

    @Column(length = 1024)
    private String encryptedSecret;
    private String issuer;

    @Column(length = 1024)
    private String encrypt;

    private static String generateTOTP(byte[] key, long time) {
        byte[] timeBytes = longToBytes(time);
        byte[] hash = hmacSha1(key, timeBytes);
        int offset = hash[19] & 0xf;

        int binary = ((hash[offset] & 0x7f) << 24) |
                ((hash[offset + 1] & 0xff) << 16) |
                ((hash[offset + 2] & 0xff) << 8) |
                (hash[offset + 3] & 0xff);

        int otp = binary % (int) Math.pow(10, 6);
        StringBuilder result = new StringBuilder(Integer.toString(otp));
        while (result.length() < 6) {
            result.insert(0, "0");
        }
        return result.toString();
    }

    private static byte[] hmacSha1(byte[] key, byte[] data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] longToBytes(long value) {
        byte[] bytes = new byte[8];
        for (int i = 7; i >= 0; --i) {
            bytes[i] = (byte) (value & 0xff);
            value >>= 8;
        }
        return bytes;
    }

    public static List<String> getEveryValue(Regex regex, String input) {
        List<MatchResult> matches = kotlin.sequences.SequencesKt.toList(regex.findAll(input, 0));
        return matches.isEmpty() ? List.of() : matches.get(0).getGroupValues().subList(1, matches.get(0).getGroupValues().size());
    }

    public String info() {
        return id + ":\n" +
                "应用程序名: " + name +
                "\n用户名: " + userId;
    }

    public String generateTotp() {
        try {
            byte[] key = Base32.decode(EncryptionUtil.decrypt(this.encryptedSecret, EncryptionUtil.stringToKey(this.encrypt)));
            long time = System.currentTimeMillis() / 1000 / 30;
            String totp = generateTOTP(key, time);
            Log.debug("TOTP: " + totp);
            Log.debug("==========");
            return totp;
        } catch (Exception e) {
            Log.error("出错啦~", e);
            return "";
        }
    }
}
