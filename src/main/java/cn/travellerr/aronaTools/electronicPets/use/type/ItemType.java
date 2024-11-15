package cn.travellerr.aronaTools.electronicPets.use.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemType {
    FOOD("food", "食物"),
    DRINK("drink", "饮品"),
    MEDICINE("medicine", "药品"),
    WASH("wash", "洗浴"),
    TOY("toy", "玩具");

    private final String code;

    private final String name;

    public static ItemType fromString(String itemType) throws Exception {
        itemType = itemType.strip();
        for (ItemType type : ItemType.values()) {
            if (type.getCode().equals(itemType) || type.getName().equals(itemType)) {
                return type;
            }
        }
        throw new Exception("未知的任务类型:" + ItemType.class.getName() + "." + itemType);
    }

}
