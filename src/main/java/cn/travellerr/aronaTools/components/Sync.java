package cn.travellerr.aronaTools.components;

import cn.chahuyun.economy.HuYanEconomy;
import cn.chahuyun.hibernateplus.HibernateFactory;
import cn.hutool.core.date.DateUtil;
import cn.travellerr.Favorability;
import cn.travellerr.aronaTools.entity.PetInfo;
import cn.travellerr.aronaTools.entity.SyncInfo;
import cn.travellerr.aronaTools.entity.TotpInfo;
import cn.travellerr.aronaTools.entity.WordleInfo;
import cn.travellerr.aronaTools.shareTools.Log;
import cn.travellerr.aronaTools.shareTools.MessageUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.MessageChain;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

public class Sync {
    public static void sync(Contact subject, MessageChain messages, User user, Long targetId) {

        if (HibernateFactory.selectOne(SyncInfo.class, "targetId", targetId) != null) {
            subject.sendMessage(MessageUtil.quoteReply(messages, "目标用户已经同步过了，为保护数据安全，不允许重复同步"));
            return;
        }
        if (HibernateFactory.selectOne(SyncInfo.class, "userId", user.getId()) != null) {
            subject.sendMessage(MessageUtil.quoteReply(messages, "目标用户已经同步过了，为保护数据安全，不允许重复同步"));
            return;
        }

        if (HibernateFactory.selectOne(SyncInfo.class, "userId", targetId) != null) {
            subject.sendMessage(MessageUtil.quoteReply(messages, "请勿同步已同步过的用户！！"));
            return;
        }

        // Delete existing data for the user
        StringBuilder sb = new StringBuilder();

        syncMiraiEconomy(user.getId(), targetId, sb);

        syncUserInfo(user.getId(), targetId, sb);

        syncFavoriteData(user.getId(), targetId, sb);

        PetInfo petInfo = HibernateFactory.selectOne(PetInfo.class, targetId);
        List<TotpInfo> totpInfo = HibernateFactory.selectList(TotpInfo.class, "userId", targetId);
        List<WordleInfo> wordleInfo = HibernateFactory.selectList(WordleInfo.class, "userId", targetId)
                .stream().filter(info -> !info.isGroup()).toList();


        if (petInfo != null) {
            petInfo.setUserId(user.getId());
            HibernateFactory.merge(petInfo);
            sb.append("宠物数据 ");
        }
        if (totpInfo != null && !totpInfo.isEmpty()) {
            for (TotpInfo info : totpInfo) {
                info.setUserId(user.getId());
                HibernateFactory.merge(info);
            }
            sb.append("TOTP数据 ");
        }
        if (!wordleInfo.isEmpty()) {
            for (WordleInfo info : wordleInfo) {
                info.setUserId(user.getId());
                HibernateFactory.merge(info);
            }
            sb.append("Wordle数据 ");
        }

        SyncInfo syncInfo = new SyncInfo(user.getId(), targetId, true);

        HibernateFactory.merge(syncInfo);

        Log.info("同步完成");
        subject.sendMessage(MessageUtil.quoteReply(messages, sb + "同步完成!"));
    }

    private static void syncUserInfo(long newId, long oldId, StringBuilder sb) {
        Path filePath = HuYanEconomy.INSTANCE.getDataFolderPath().resolve("HuYanEconomy.h2");
        Log.info("数据库路径：" + filePath);
        try (Connection connection = DriverManager.getConnection("jdbc:h2:" + filePath)) {
            String newUUID = "u" + newId;

            // Delete existing rows with newId
            String deleteQuery = "DELETE FROM UserInfo WHERE QQ = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                deleteStmt.setLong(1, newId);
                deleteStmt.executeUpdate();
            }

            // Update rows with oldId to newId
            String updateQuery = "UPDATE UserInfo SET QQ = ?, ID = ?, SIGNTIME = ? WHERE QQ = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setLong(1, newId);
                updateStmt.setString(2, newUUID);
                updateStmt.setDate(3, DateUtil.yesterday().toSqlDate());
                updateStmt.setLong(4, oldId);
                updateStmt.executeUpdate();
            }

            String updateQuery2 = "UPDATE FISHINFO SET QQ = ? WHERE QQ = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery2)) {
                updateStmt.setLong(1, newId);
                updateStmt.setLong(2, oldId);
                updateStmt.executeUpdate();
            }

        } catch (Exception e) {
            Log.error("同步用户信息失败", e);
        }
        sb.append("经济数据p2 ");
    }

        private static void syncMiraiEconomy(long newId, long oldId, StringBuilder sb) {

        String oldUUID = "u" + oldId;
        String newUUID = "u" + newId;
        Path filePath = Path.of("data/xyz.cssxsh.mirai.plugin.mirai-economy-core/hibernate.h2").toAbsolutePath();
        Log.info("经济数据库路径：" + filePath);
        try (Connection connection = DriverManager.getConnection("jdbc:h2:" + filePath)) {
            String[] tables = {"ECONOMY_BALANCE_RECORD", "ECONOMY_ACCOUNT_RECORD"};
            for (String table : tables) {
                // Delete existing rows with newId
                String deleteQuery = "DELETE FROM " + table + " WHERE UUID = ?";
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                    deleteStmt.setString(1, newUUID);
                    deleteStmt.executeUpdate();
                }

                // Update rows with oldId to newId
                String updateQuery = "UPDATE " + table + " SET UUID = ? WHERE UUID = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, newUUID);
                    updateStmt.setString(2, oldUUID);
                    updateStmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            Log.error("同步用户信息失败", e);
        }
        sb.append("经济数据p1 ");
    }

    private static void syncFavoriteData(long newId, long oldId, StringBuilder sb) {

        Path filePath = Favorability.INSTANCE.getDataFolderPath().resolve("Favorability.h2");
        Log.info("好感数据库路径：" + filePath);
        try (Connection connection = DriverManager.getConnection("jdbc:h2:" + filePath)) {
                // Delete existing rows with newId
            String deleteQuery = "DELETE FROM Favourite WHERE QQ = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                deleteStmt.setLong(1, newId);
                deleteStmt.executeUpdate();
            }

            // Update rows with oldId to newId
            String updateQuery = "UPDATE Favourite SET QQ = ? WHERE QQ = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setLong(1, newId);
                updateStmt.setLong(2, oldId);
                updateStmt.executeUpdate();
            }
        } catch (Exception e) {
            Log.error("同步用户信息失败", e);
        }
        sb.append("好感数据 ");
    }
}
