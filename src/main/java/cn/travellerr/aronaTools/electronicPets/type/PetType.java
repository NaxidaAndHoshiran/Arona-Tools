package cn.travellerr.aronaTools.electronicPets.type;

import cn.travellerr.aronaTools.shareTools.Log;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PetType {
    DOG ("狗", 100, 0.1, 100, ""),

    CAT("猫", 100, 0.1, 100, ""),

    RABBIT("兔", 100, 0.1, 100, ""),

    HAMSTER("仓鼠", 100, 0.1, 100, ""),

    PARROT("鹦鹉", 100, 0.1, 100, ""),

    FISH("鱼", 100, 0.1, 100, ""),

    TURTLE("乌龟", 100, 0.1, 100, ""),

    SNAKE("蛇", 100, 0.1, 100, ""),


    // 事情开始变得奇怪了
    MILK_DRAGON("奶龙", 100, 0.1, 100, ""),

    NEURO("Neuro", 100, 0.1, 100, ""),

    RBQ("绒布球", 100, 0.1, 100, ""),

    MIKU("初音未来", 100, 0.1, 100, ""),

    TETO("重音Teto", 100, 0.1, 100, ""),

    RIN("镜音铃", 100, 0.1, 100, ""),

    LEN("镜音连", 100, 0.1, 100, ""),

    ZUNDAMON("俊达萌", 100, 0.1, 100, ""),

    TRAITOR("东雪莲", 100, 0.1, 100, ""),

    MIKADO("孙笑川", 100, 0.1, 100, ""),

    TAFFY("永雏塔菲", 100, 0.1, 100, ""),

    // 几个傻冒让我加的
    WEIYULINXI("苿雨林汐", 100, 3.0, 50, ""),

    Q10("Q10", 1, 5.0, 10, ""),

    COPILOT("副驾驶", 100, 1.0, 300, ""),

    // 我勒个少女乐队大集合啊
    ANON("爱音", 100, 0.1, 100, ""),

    SOYO("素世", 100, 0.1, 100, ""),

    TOMORI("高松灯", 100, 0.1, 100, ""),

    MOMOKA("河原木桃香", 100, 0.1, 100, ""),

    NINA("井芹仁菜", 100, 0.1, 100, ""),

    SUBARU("安和昴", 100, 0.1, 100, ""),

    YUI("平沢唯", 100, 0.1, 100, ""),

    UI("平沢忧", 100, 0.1, 100, ""),

    MIO("秋山澪", 100, 0.1, 100, ""),

    AZUSA("中野梓", 100, 0.1, 100, ""),

    RITSU("田井中律", 100, 0.1, 100, ""),

    BOCCHI("波奇", 100, 0.1, 100, ""),

    LKUYO("喜多郁代", 100, 0.1, 100, ""),

    NIJIKA("虹夏", 100, 0.1, 100, ""),

    LORIS("萝莉斯", 100, 0.2, 100, ""),;

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
