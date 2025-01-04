package cn.travellerr.aronaTools.electronicPets.fight;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 技能类型枚举类
 * 表示不同的技能类型
 *
 * @author Travellerr
 */
@Getter
@AllArgsConstructor
public enum SkillType {
    ATTACK("attack"), // 攻击技能
    DEFEND("defend"), // 防御技能
    HEALTH("health"); // 恢复技能

    /**
     * 技能编号(英文);
     */
    private final String code;

    public String getChinese() {
        return switch (this) {
            case ATTACK -> "攻击";
            case DEFEND -> "防御";
            case HEALTH -> "恢复";
            default -> "未知";
        };
    }
}
