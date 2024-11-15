package cn.travellerr.aronaTools.electronicPets.type;

import cn.travellerr.aronaTools.shareTools.Log;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 宠物种类枚举常数
 */
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
    MILK_DRAGON("奶龙", 600, 0.1, 100, "-我是奶龙 -我才是奶龙……"),

    NEURO("Neuro", 1000, 0.1, 100, "Heart <3"),

    RBQ("绒布球", 2000, 0.1, 100, ""),

    MIKU("初音未来", 5000, 0.1, 100, "我去，初音未来！"),

    TETO("重音Teto", 5000, 0.1, 100, "31岁奇美拉"),

    RIN("镜音铃", 5000, 0.1, 100, ""),

    LEN("镜音连", 5000, 0.1, 100, ""),

    ZUNDAMON("俊达萌", 5000, 0.1, 100, "なのだ☆"),

    TRAITOR("东雪莲", 1000, 0.1, 100, "骂谁罕见呢？骂谁罕见！"),

    MIKADO("孙笑川", 1000, 0.1, 100, ""),

    TAFFY("永雏塔菲", 1000, 0.1, 100, "关注永雏塔菲喵，关注永雏塔菲谢谢喵~"),

    // 几个傻冒让我加的
    WEIYULINXI("苿雨林汐", 10, 1.0, 50, "私のオナニーお見でください"),

    Q10("Q10", 1, 5.0, 10, "啊……那里……那里不行……不要……噫！要……要坏掉了……"),

    COPILOT("副驾驶", 3000, 1.0, 300, "Github Copilot 是由 Microsoft 和 OpenAI 合作开发的人工智能代码提示工具，它可以根据上下文提示代码，帮助程序员提高编程效率。"),

    // 我勒个少女乐队大集合啊
    ANON("千早爱音", 1000, 0.1, 100, "AnonTokyo!!!!!"),

    SOYO("长崎素世", 1000, 0.1, 100, "どうしたら戻れるの？私にできることなら何でもするから！"),

    TOMORI("高松灯", 1000, 0.1, 100, "那……能陪我组一辈子的乐队吗？"),

    MOMOKA("河原木桃香", 1000, 0.1, 100, "m0m0kさん!"),

    NINA("井芹仁菜", 1000, 0.1, 100, "とっても美味しかったです。ありがとうございました 凸(^▽^)凸"),

    SUBARU("安和昴", 1000, 0.1, 100, "すばるです~"),

    YUI("平泽唯", 1000, 0.1, 100, "ギー太！"),

    UI("平泽忧", 1000, 0.1, 100, "“好成熟可靠的孩子”"),

    MIO("秋山澪", 1000, 0.1, 100, "滑滑蛋~"),

    AZUSA("中野梓", 1000, 0.1, 100, "あずにゃん~~"),

    RITSU("田井中律", 1000, 0.1, 100, ""),

    BOCCHI("后藤一里", 1000, 0.1, 100, ""),

    LKUYO("喜多郁代", 1000, 0.1, 100, ""),

    NIJIKA("伊地知虹夏", 1000, 0.1, 100, ""),

    LORIS("萝莉斯", 1000, 0.2, 100, "萝莉斯正在开心地睡觉"),

    KOKOMI("珊瑚宫心海", 1000, 0.1, 100, ""),

    ALIS("爱丽丝", 1000, 0.1, 100, "我草，盒！爱丽丝错了，爱丽丝不该在网上口嗨的！"),


    TRAVELLERR("Travellerr", 1000000, 0.01, 10000, ""),;

    /**
     * 宠物种类名称
     */
    private final String petType;

    /**
     * 购买价格
     */
    private final Integer cost;

    /**
     * 每分钟变化(基数)
     */
    private final Double valueChangePerMin;

    /**
     * 默认最大生命值
     */
    private final Integer defaultMaxHp;

    /**
     * 宠物描述
     */
    private final String description;

    /**
     * 通过字符串获取宠物种类
     * @param petType 宠物种类字符串
     * @return 宠物种类(不存在返回null)
     */
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
