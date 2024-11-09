package cn.travellerr.aronaTools.electronicPets.task

import cn.travellerr.aronaTools.electronicPets.type.TaskType
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val code: String,
    val name: String,
    val description: String,
    var isVerified: Boolean,
    val taskType: TaskType,
    val takeTime: Int,
    val moneyPerMin: Double,
    val expPerMin: Double,
    val moodPerMin: Double,
    val creatorName: String,
    val creatorId: Long
)
