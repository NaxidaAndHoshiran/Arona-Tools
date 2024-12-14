package wordle.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStreamReader

@Serializable
data class Words(
    val valid: List<String>,
    val words: List<String>
) {
    companion object {

        fun parse(inputStream: InputStreamReader): Words {
            return Json.decodeFromString(serializer(), inputStream.readText())
        }
    }
    fun getRandomWord(): String {
        val random = words.indices.random()
        return words[random]
    }
}