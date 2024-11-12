package cn.travellerr.aronaTools.electronicPets.type;

import cn.travellerr.aronaTools.shareTools.Log;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PetType {
    DOG ("狗", 500, 0.5, 100, ""),

    CAT("猫", 500, 0.5, 100, ""),

    RABBIT("兔", 500, 0.7, 70, ""),

    HAMSTER("仓鼠", 500, 0.3, 50, ""),

    PARROT("鹦鹉", 500, 0.2, 50, ""),

    FISH("鱼", 200, 0.4, 50, ""),

    TURTLE("乌龟", 500, 0.1, 200, ""),

    SNAKE("蛇", 500, 0.2, 120, ""),


    // 事情开始变得奇怪了
    MILK_DRAGON("奶龙", 600, 0.1, 100, ""),

    NEURO("Neuro", 100, 0.1, 100, ""),

    RBQ("绒布球", 2000, 0.1, 100, ""),

    MIKU("初音未来", 5000, 0.1, 100, ""),

    TETO("重音Teto", 5000, 0.1, 100, ""),

    RIN("镜音铃", 5000, 0.1, 100, ""),

    LEN("镜音连", 5000, 0.1, 100, ""),

    ZUNDAMON("俊达萌", 5000, 0.1, 100, ""),

    TRAITOR("东雪莲", 1000, 0.1, 100, ""),

    MIKADO("孙笑川", 1000, 0.1, 100, ""),

    TAFFY("永雏塔菲", 1000, 0.1, 100, ""),

    // 几个傻冒让我加的
    WEIYULINXI("苿雨林汐", 10, 1.0, 50, ""),

    Q10("Q10", 1, 5.0, 10, ""),

    COPILOT("副驾驶", 3000, 1.0, 300, ""),

    // 我勒个少女乐队大集合啊
    ANON("爱音", 1000, 0.1, 100, ""),

    SOYO("素世", 1000, 0.1, 100, ""),

    TOMORI("高松灯", 1000, 0.1, 100, ""),

    MOMOKA("河原木桃香", 1000, 0.1, 100, ""),

    NINA("井芹仁菜", 1000, 0.1, 100, ""),

    SUBARU("安和昴", 1000, 0.1, 100, ""),

    YUI("平沢唯", 1000, 0.1, 100, ""),

    UI("平沢忧", 1000, 0.1, 100, ""),

    MIO("秋山澪", 1000, 0.1, 100, ""),

    AZUSA("中野梓", 1000, 0.1, 100, ""),

    RITSU("田井中律", 1000, 0.1, 100, ""),

    BOCCHI("后藤一里", 1000, 0.1, 100, ""),

    LKUYO("喜多郁代", 1000, 0.1, 100, ""),

    NIJIKA("伊地知虹夏", 1000, 0.1, 100, ""),

    LORIS("萝莉斯", 1000, 0.2, 100, ""),

    KOKOMI("珊瑚宫心海", 1000, 0.1, 100, ""),


    TRAVELLERR("Travellerr", 1000000, 0.01, 10000, ""),;

    private final String petType;

    private final Integer cost;

    private final Double valueChangePerMin;

    private final Integer defaultMaxHp;

    private final String description;

    public static PetType fromString(String petType) {
        petType = petType.strip();
        for (PetType type : PetType.values()) {
            if (type.getPetType().equals(petType)) {
                return type;
            }
        }
        Log.error("未知的宠物: " + petType);
        return null;
    }
}
