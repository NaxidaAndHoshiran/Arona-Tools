package cn.travellerr.aronaTools.electronicPets.use.shop

import cn.travellerr.aronaTools.electronicPets.use.type.ItemType
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val code: String,
    val name: String,
    val description: String,
    var isVerified: Boolean,
    val itemType: ItemType,
    val price: Int,
    val exp: Long,
    val hunger: Double,
    val mood: Double,
    val health: Double,
    val relation: Double,
    val creatorName: String,
    val creatorId: Long
)
