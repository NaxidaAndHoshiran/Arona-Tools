package cn.travellerr.aronaTools.electronicPets.fight;

import cn.travellerr.aronaTools.electronicPets.fight.type.AttributeType;
import cn.travellerr.aronaTools.electronicPets.use.type.PetType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 技能枚举常数
 * <br>
 * 用于表示不同的技能
 * <br>
 * PetType 用于表示不同的宠物，为PetType.values()时表示适用所有宠物，为new PetType[]{}时表示无适用宠物。
 * AttributeType 用于表示不同的属性，为null时表示无属性。
 */
@Getter
@AllArgsConstructor
public enum Skill {
    ATTACK("攻击", SkillType.ATTACK, 5, 0, 0, PetType.values(), AttributeType.ALL),
    DEFEND("防御", SkillType.DEFEND, 10, 1, 0, PetType.values(), AttributeType.ALL),
    HEALTH("回复", SkillType.HEALTH, 10, 1, 0, PetType.values(), AttributeType.ALL),
    NORMAL_ATTACK("普通攻击", SkillType.ATTACK, 10, 1, 0, PetType.values(), AttributeType.ALL),
    FIREBALL("火球术", SkillType.ATTACK, 20, 2, 2, new PetType[]{PetType.CAT}, AttributeType.CODE),
    OBLIVIONIS("遗忘", SkillType.ATTACK, 20, 2, 2, new PetType[]{PetType.SAKI}, null),


    // From QQ user "五雷"
    LEEK_PANCAKE("韭菜盒子", SkillType.HEALTH, 20, 1, 2, new PetType[]{PetType.OTTO}, null),
    WOW("哇袄", SkillType.DEFEND, 20, 2, 4, new PetType[]{PetType.OTTO}, null),
    ICE("冰！！！", SkillType.ATTACK, 5, 0, 2, new PetType[]{PetType.OTTO}, null),
    HA_LI_LU_STORM("哈里路大旋风！！！", SkillType.ATTACK, 40, 5, 5, new PetType[]{PetType.OTTO}, null),


    // From QQ user "小鸟游星野(159*****69)"
    TACTICAL_SUPPRESSION("战术压制", SkillType.DEFEND, 30, 2, 2, new PetType[]{PetType.HOSHINO}, null),
    COLORFUL_EGG_SHOOTING("彩蛋射击", SkillType.ATTACK, 30, 3, 3, new PetType[]{PetType.MAKI}, null),
    OBSERVATION_SUPPORT("观测支援", SkillType.CONTROL, 50, 2, 3, new PetType[]{PetType.NODOKA}, null),
    BUILD_FIREWALL("防火墙建设", SkillType.DEFEND, 30, 2, 2, new PetType[]{PetType.CHIHIRO}, null),
    MENTAL_ARITHMETIC("高速心算", SkillType.HEALTH, 20, 2, 2, new PetType[]{PetType.YUUKA}, null),
    THE_MEANS_OF_THE_DEFENSE_ROOM("防卫室主人的手段", SkillType.ATTACK, 30, 3, 3, new PetType[]{PetType.KAYA}, null),
    TARGET_EXCLUSION("目标排除", SkillType.CONTROL, 40, 2, 3, new PetType[]{PetType.NANAGAMI_RIN}, null),

    ABNORMAL_ELECTROMAGNETIC("异常电磁", SkillType.CONTROL, 30, 2, 3, new PetType[]{}, AttributeType.EXCEPTION),
    BLACK_ARROW_ZERO("漆黑矢零式", SkillType.ATTACK, 20, 2, 3, new PetType[]{}, AttributeType.EXCEPTION),

    TROJAN_VIRUS("木马病毒", SkillType.ATTACK, 40, 4, 5, new PetType[]{}, AttributeType.PROGRAM),
    PROGRAM_RECOVERY("程序恢复", SkillType.HEALTH, 20, 3, 2, new PetType[]{}, AttributeType.PROGRAM),

    MOBILE_ARMOR("机动装甲", SkillType.DEFEND, 50, 3, 4, new PetType[]{}, AttributeType.MODEL),
    MODEL_TRAINING("模型训练", SkillType.HEALTH, 50, 2, 5, new PetType[]{}, AttributeType.MODEL),

    FIRE_SUPPORT("火力支援", SkillType.ATTACK, 40, 3, 3, new PetType[]{}, AttributeType.ENTITY),
    QUICK_SUPPLY("快速补给", SkillType.HEALTH, 20, 2, 2, new PetType[]{}, AttributeType.ENTITY),

    DATA_DEFENSE("数据化", SkillType.DEFEND, 25, 2, 3, new PetType[]{}, AttributeType.DATA),
    INVASION_FIREWALL("入侵防火墙", SkillType.CONTROL, 30, 2, 3, new PetType[]{}, AttributeType.DATA),

    STAR_PIERCING("星贯", SkillType.ATTACK, 45, 3, 2, new PetType[]{}, AttributeType.CODE),
    HIDDEN_CODE("隐入编码", SkillType.DEFEND, 50, 2, 4, new PetType[]{}, AttributeType.CODE),

    // From QQ user "扇喜葵(169*****71)"
    LOGIC_AND_LEGITIMACY("逻辑与正当性", SkillType.CONTROL, 60, 4, 4, new PetType[]{PetType.OKIAOI}, null),

    // From QQ user "清浅(399*****55)"
    CALL_OF_DRONES_FIRE_SUPPORT("无人机召唤:火力支援", SkillType.ATTACK, 30, 3, 3, new PetType[]{PetType.SHIROKO}, null),



    // 测试用技能
    //ONESHOT("一击必杀", SkillType.ATTACK, 100, 10, 10, new PetType[]{PetType.CAT}, AttributeType.CODE),
    ;


    /**
     * 技能的名称。
     */
    private final String name;

    /**
     * 技能类型。
     *
     * @see SkillType
     */
    private final SkillType skillType;

    /**
     * 技能的默认值(伤害/防御/回复)。
     */
    private final int defaultValue;

    /**
     * 使用技能的默认消耗。
     */
    private final int defaultCost;

    /**
     * 技能的默认冷却时间。
     */
    private final int defaultCoolDown;

    /**
     * 可以使用此技能的宠物类型。
     *
     * @see PetType
     */
    private final PetType[] petType;

    /**
     * 技能的属性类型。
     * <p>
     * <code>null</code>值为无。
     * <code>all</code>值为全部属性。
     *
     * @see AttributeType
     */
    private final AttributeType attributeType;

    public String getInfo() {
        return "技能名称：" + name + "\n" +
                "技能类型：" + skillType + "\n" +
                "技能默认值：" + defaultValue + "\n" +
                "技能默认消耗：" + defaultCost + "\n" +
                "技能默认冷却时间：" + defaultCoolDown + "\n" +
                "技能适用宠物：" + Arrays.toString(petType) + "\n" +
                "技能属性类型：" + attributeType;
    }

    public String getSimplifiedInfo() {
        return name + "(类型: " + skillType.getChinese() + ", 默认数值: " + defaultValue + ", 默认花费: " + defaultCost + ")";
    }

}
