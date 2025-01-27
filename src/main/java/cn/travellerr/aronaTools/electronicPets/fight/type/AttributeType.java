package cn.travellerr.aronaTools.electronicPets.fight.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 属性类型
 * <pre>
 * NULL
 * EXCEPTION
 * PROGRAM
 * MODEL
 * DATA
 * ENTITY
 * CODE
 * </pre>
 *
 * @see AttributeType#WEAKNESS_MAP
 */
@Getter
@AllArgsConstructor
public enum AttributeType {
    /**
     * NULL - 空属性，请用于普通宠物（狗、猫、鸟）。
     */
    NULL("null", "空"),
    CODE("code", "编码"),
    DATA("data", "数据"),
    MODEL("model", "模型"),
    ENTITY("entity", "实体"),
    EXCEPTION("exception", "异常"),
    PROGRAM("program", "程序"),

    /**
     * 全部属性, 用于表示不受属性克制。
     */
    ALL("all", "全部");

    /**
     * 定义每个 AttributeType 的弱点关系。
     * <br>
     * 键为优方，值为劣方。
     * <br>
     *  <strong><i>键 -克制-> 值</i></strong>
     * <pre>
     * NULL -克制-> NULL
     * EXCEPTION -克制-> CODE
     * PROGRAM -克制-> EXCEPTION
     * MODEL -克制-> PROGRAM
     * DATA -克制-> MODEL
     * ENTITY -克制-> DATA
     * CODE -克制-> ENTITY </pre>
     */
    private static final Map<AttributeType, AttributeType> WEAKNESS_MAP = new HashMap<>();


    static {
        WEAKNESS_MAP.put(NULL, NULL);
        WEAKNESS_MAP.put(EXCEPTION, CODE);
        WEAKNESS_MAP.put(PROGRAM, EXCEPTION);
        WEAKNESS_MAP.put(MODEL, PROGRAM);
        WEAKNESS_MAP.put(DATA, MODEL);
        WEAKNESS_MAP.put(ENTITY, DATA);
        WEAKNESS_MAP.put(CODE, ENTITY);
    }

    private final String code;
    private final String name;

    /**
     * 判断是否克制对方属性
     * @param other 对方属性
     * @return 1: 克制对方属性, -1: 被对方属性克制, 0: 无克制关系
     */
    public int isWeakAgainst(AttributeType other) {
        return WEAKNESS_MAP.get(this).equals(other) ? 1 : WEAKNESS_MAP.get(other).equals(this) ? -1 : 0;
    }
}
