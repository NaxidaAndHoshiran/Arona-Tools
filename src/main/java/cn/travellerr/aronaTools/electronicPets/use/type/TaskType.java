package cn.travellerr.aronaTools.electronicPets.use.type;

import kotlinx.serialization.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务类型枚举类
 * 表示不同的任务类型
 * 包含学习、工作和游玩
 *
 * @author Travellerr
 */
@AllArgsConstructor
@Getter
@Serializable
public enum TaskType {
    STUDY("study", "学习"),
    WORK("work", "工作"),
    PLAY("play", "游玩");

    private final String code;
    private final String name;

    /**
     * 根据字符串获取任务类型
     *
     * @param taskType 任务类型字符串
     * @return 对应的任务类型枚举
     * @throws Exception 如果任务类型未知
     */
    public static TaskType fromString(String taskType) throws Exception {
        taskType = taskType.strip();
        for (TaskType type : TaskType.values()) {
            if (type.getCode().equals(taskType) || type.getName().equals(taskType)) {
                return type;
            }
        }
        throw new Exception("未知的任务类型:" + TaskType.class.getName() + "." + taskType);
    }
}