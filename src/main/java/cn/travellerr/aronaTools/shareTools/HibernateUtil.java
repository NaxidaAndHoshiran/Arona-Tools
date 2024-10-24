package cn.travellerr.aronaTools.shareTools;

import cn.chahuyun.hibernateplus.Configuration;
import cn.chahuyun.hibernateplus.DriveType;
import cn.chahuyun.hibernateplus.HibernatePlusService;
import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.config.Config;

import java.nio.file.Path;

public class HibernateUtil {
    /**
     * Hibernate初始化
     *
     * @param aronaTools 插件
     * @author Moyuyanli
     */
    public static void init(AronaTools aronaTools) {
        Config config = AronaTools.config;

        Configuration configuration = HibernatePlusService.createConfiguration(aronaTools.getClass());
        configuration.setPackageName("cn.travellerr.aronaTools.entity");

        DriveType dataType = config.getDataType();
        configuration.setDriveType(dataType);
        Path dataFolderPath = aronaTools.getDataFolderPath();
        switch (dataType) {
            case MYSQL:
                configuration.setAddress(config.getMysqlUrl());
                configuration.setUser(config.getMysqlUser());
                configuration.setPassword(config.getMysqlPassword());
                break;
            case H2:
                configuration.setAddress(dataFolderPath.resolve("Arona-Tools.h2").toString());
                break;
            case SQLITE:
                configuration.setAddress(dataFolderPath.resolve("Arona-Tools").toString());
                break;
        }

        HibernatePlusService.loadingService(configuration);
    }
}
