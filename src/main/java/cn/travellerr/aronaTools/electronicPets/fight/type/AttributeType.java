package cn.travellerr.aronaTools.electronicPets.fight.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum AttributeType {
    NULL("null", "空"),
    CODE("code", "编码"),
    DATA("data", "数据"),
    MODEL("model", "模型"),
    ENTITY("entity", "实体"),
    EXCEPTION("exception", "异常"),
    PROGRAM("program", "程序");

    /**
    * 定义每个 AttributeType 的弱点关系。
    * <pre>
    * NULL -克制-> NULL
    * EXCEPTION -克制-> CODE
    * PROGRAM -克制-> EXCEPTION
    * MODEL -克制-> PROGRAM
    * DATA -克制-> MODEL
    * ENTITY -克制-> DATA
    * CODE -克制-> ENTITY
    * </pre>
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

    public boolean isWeakAgainst(AttributeType other) {
        return WEAKNESS_MAP.get(other) == this;
    }
}
