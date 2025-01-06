package cn.travellerr.aronaTools.electronicPets.fight;

import cn.travellerr.aronaTools.electronicPets.fight.type.AttributeType;
import cn.travellerr.aronaTools.electronicPets.use.type.PetType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Skill {
    ATTACK("攻击", SkillType.ATTACK, 5, 0, 0, PetType.values(), AttributeType.ALL),
    DEFEND("防御", SkillType.DEFEND, 10, 1, 0, PetType.values(), AttributeType.ALL),
    HEALTH("回复", SkillType.HEALTH, 10, 1, 0, PetType.values(), AttributeType.ALL),
    NORMAL_ATTACK("普通攻击", SkillType.ATTACK, 10, 1, 0, PetType.values(), AttributeType.ALL),
    FIREBALL("火球术", SkillType.ATTACK, 20, 2, 2, new PetType[]{PetType.CAT}, AttributeType.CODE),

    // From QQ user "五雷"
    LEEK_PANCAKE("韭菜盒子", SkillType.HEALTH, 20, 1, 2, new PetType[]{PetType.OTTO}, null),
    WOW("哇袄", SkillType.DEFEND, 30, 2, 4, new PetType[]{PetType.OTTO}, null),
    ICE("冰！！！", SkillType.ATTACK, 5, 0, 2, new PetType[]{PetType.OTTO}, null),
    HA_LI_LU_STORM("哈里路大旋风！！！", SkillType.ATTACK, 40, 5, 5, new PetType[]{PetType.OTTO}, null),

    // 测试用技能
    //ONESHOT("一击必杀", SkillType.ATTACK, 100, 10, 10, new PetType[]{PetType.CAT}, AttributeType.CODE),
    ;


    /**
     * 技能的名称。
     */
    private final String name;

    /**
     * 技能类型。
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
     */
    private final PetType[] petType;

    /**
     * 技能的属性类型。
     * <p>
     * <code>null</code>值为无。
     * <code>all</code>值为全部属性。
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
