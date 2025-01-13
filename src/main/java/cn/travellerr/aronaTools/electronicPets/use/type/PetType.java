package cn.travellerr.aronaTools.electronicPets.use.type;

import cn.travellerr.aronaTools.electronicPets.fight.type.AttributeType;
import cn.travellerr.aronaTools.shareTools.Log;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 宠物种类枚举常数
 */
@AllArgsConstructor
@Getter
public enum PetType {
    DOG ("狗", new String[]{}, 500, 0.5, 100, "", AttributeType.NULL),

    CAT("猫", new String[]{}, 500, 0.5, 100, "", AttributeType.NULL),

    RABBIT("兔", new String[]{}, 500, 0.7, 70, "", AttributeType.NULL),

    HAMSTER("仓鼠", new String[]{}, 500, 0.3, 50, "", AttributeType.NULL),

    PARROT("鹦鹉", new String[]{}, 500, 0.2, 50, "", AttributeType.NULL),

    FISH("鱼", new String[]{}, 200, 0.4, 50, "", AttributeType.NULL),

    TURTLE("乌龟", new String[]{}, 500, 0.1, 200, "", AttributeType.NULL),

    SNAKE("蛇", new String[]{}, 500, 0.2, 120, "", AttributeType.NULL),


    // 事情开始变得奇怪了
    MILK_DRAGON("奶龙", new String[]{}, 600, 0.1, 100, "-我是奶龙 -我才是奶龙……", AttributeType.CODE),

    NEURO("Neuro", new String[]{}, 1000, 0.1, 100, "Heart <3", AttributeType.MODEL),

    RBQ("绒布球", new String[]{}, 2000, 0.1, 100, "", AttributeType.DATA),

    MIKU("初音未来", new String[]{"初音"}, 5000, 0.1, 100, "我去，初音未来！", AttributeType.CODE),

    TETO("重音Teto", new String[]{"Teto"}, 5000, 0.1, 100, "31岁奇美拉", AttributeType.CODE),

    RIN("镜音铃", new String[]{}, 5000, 0.1, 100, "", AttributeType.PROGRAM),

    LEN("镜音连", new String[]{}, 5000, 0.1, 100, "", AttributeType.PROGRAM),

    ZUNDAMON("俊达萌", new String[]{}, 5000, 0.1, 100, "なのだ☆", AttributeType.ENTITY),

    TRAITOR("东雪莲", new String[]{}, 1000, 0.1, 100, "骂谁罕见呢？骂谁罕见！", AttributeType.MODEL),

    MIKADO("孙笑川", new String[]{}, 1000, 0.1, 100, "", AttributeType.MODEL),

    TAFFY("永雏塔菲", new String[]{}, 1000, 0.1, 100, "关注永雏塔菲喵，关注永雏塔菲谢谢喵~", AttributeType.MODEL),

    OTTO("电棍otto", new String[]{}, 1000, 0.1, 100, "冲刺！冲刺！冲！", AttributeType.EXCEPTION),

    // 几个傻冒让我加的
    WEIYULINXI("苿雨林汐", new String[]{}, 10, 1.0, 50, "私のオナニーお見でください", AttributeType.PROGRAM),

    Q10("Q10", new String[]{}, 1, 5.0, 10, "啊……那里……那里不行……不要……噫！要……要坏掉了……", AttributeType.PROGRAM),

    ZY("中烟", new String[]{}, 500, 2.5, 250, "帅！中烟！帅！", AttributeType.NULL),

    COPILOT("副驾驶", new String[]{}, 3000, 1.0, 300, "Github Copilot 是由 Microsoft 和 OpenAI 合作开发的人工智能代码提示工具，它可以根据上下文提示代码，帮助程序员提高编程效率。", AttributeType.CODE),

    // 我勒个少女乐队大集合啊
    ANON("千早爱音", new String[]{"爱音"}, 1000, 0.1, 100, "AnonTokyo!!!!!", AttributeType.DATA),

    SOYO("长崎素世", new String[]{"soyorin", "素世", "长崎爽世", "爽世"}, 1000, 0.1, 100, "どうしたら戻れるの？私にできることなら何でもするから！", AttributeType.DATA),

    TOMORI("高松灯", new String[]{"tomorin", "灯"}, 1000, 0.1, 100, "那……能陪我组一辈子的乐队吗？", AttributeType.EXCEPTION),

    TAKI("椎名立希", new String[]{"rikki", "立希"}, 1000, 0.1, 100, "哈？", AttributeType.EXCEPTION),

    RANA("要乐奈", new String[]{"流浪猫"}, 1000, 0.1, 100, "抹茶巴菲！", AttributeType.PROGRAM),

    SAKI("丰川祥子", new String[]{"saki", "saki酱", "祥子"}, 1000, 0.1, 100, "你这个人，满脑子都是自己呢", AttributeType.CODE),

    MOMOKA("河原木桃香", new String[]{"mmk", "桃香"}, 1000, 0.1, 100, "m0m0kさん!", AttributeType.ENTITY),

    NINA("井芹仁菜", new String[]{"nina", "虾仁芹菜", "仁菜"}, 1000, 0.1, 100, "とっても美味しかったです。ありがとうございました 凸(^▽^)凸", AttributeType.EXCEPTION),

    SUBARU("安和昴", new String[]{"486", "昴"}, 1000, 0.1, 100, "すばるです~", AttributeType.EXCEPTION),

    YUI("平泽唯", new String[]{"唯", "呆唯"}, 1000, 0.1, 100, "ギー太！", AttributeType.ENTITY),

    MIO("秋山澪", new String[]{"mio", "澪"}, 1000, 0.1, 100, "滑滑蛋~", AttributeType.CODE),

    AZUSA("中野梓", new String[]{"梓", "阿梓喵", "梓喵"}, 1000, 0.1, 100, "あずにゃん~~", AttributeType.MODEL),

    RITSU("田井中律", new String[]{"律队", "律"}, 1000, 0.1, 100, "", AttributeType.DATA),

    MUGI("琴吹䌷", new String[]{"mugi", "䌷"}, 1000, 0.1, 100, "只是觉得女孩子之间真是好啊", AttributeType.MODEL),

    BOCCHI("后藤一里", new String[]{"波奇", "波奇酱", "小孤独"}, 1000, 0.1, 100, "", AttributeType.EXCEPTION),

    LKUYO("喜多郁代", new String[]{"喜多", "郁代"}, 1000, 0.1, 100, "", AttributeType.PROGRAM),

    NIJIKA("伊地知虹夏", new String[]{"虹夏"}, 1000, 0.1, 100, "", AttributeType.EXCEPTION),

    RYO("山田凉", new String[]{"凉", "凉前辈", "凉先辈"}, 1000, 0.1, 100, "", AttributeType.ENTITY),

    LORIS("萝莉斯", new String[]{}, 1000, 0.2, 100, "萝莉斯正在开心地睡觉", AttributeType.DATA),

    KOKOMI("珊瑚宫心海", new String[]{"心海"}, 1000, 0.1, 100, "", AttributeType.ENTITY),

    AYAKA("神里绫华", new String[]{"凌华", "绫华"}, 1000, 0.1, 100, "稻妻“社奉行”神里家的大小姐。容姿端丽，品行高洁。绫华贵为“公主”，平日主理家族内外事宜。绫华常出现在社交场合，与民间交集也较多。因此，更被人们熟悉的她反而获得了高于兄长的名望，被雅称为“白鹭公主”。众所周知，神里家的女儿绫华小姐容姿端丽、品行高洁，是深受民众钦慕的人物", AttributeType.ENTITY),


    // Blue Archive

    ALIS("爱丽丝", new String[]{"天童爱丽丝"}, 1000, 0.1, 100, "我草，盒！爱丽丝错了，爱丽丝不该在网上口嗨的！", AttributeType.MODEL),
    HOSHINO("星野", new String[]{"小鸟游星野"}, 1000, 0.1, 100, "完全没睡到午觉耶，嘛，也没办法吧", AttributeType.EXCEPTION),


    TRAVELLERR("Travellerr", new String[]{}, 1000000, 0.01, 10000, "", AttributeType.PROGRAM),;

    /**
     * 宠物种类名称
     */
    private final String petType;

    private final String[] alias;

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

    private final AttributeType attributeType;

    /**
     * 通过字符串获取宠物种类
     * @param petType 宠物种类字符串
     * @return 宠物种类(不存在返回null)
     */
    public static PetType fromString(String petType) {
        petType = petType.strip();
        for (PetType type : PetType.values()) {
            if (type.toString().equals(petType) || type.getPetType().equals(petType) || Arrays.stream(type.getAlias()).toList().contains(petType)) {
                return type;
            }
        }
        Log.error("未知的宠物: " + petType);
        return null;
    }
}
