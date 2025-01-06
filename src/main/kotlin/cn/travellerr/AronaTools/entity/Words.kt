package cn.travellerr.aronaTools.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStreamReader

@Serializable
data class WordList (
    val words: List<Words>,
    val valid: List<String>
) {
    companion object {

        fun parse(inputStream: InputStreamReader): WordList {
            return Json.decodeFromString(serializer(), inputStream.readText())
        }
    }
    fun getRandomWord(): Words {
        val random = words.indices.random()
        return words[random]
    }
}

@Serializable
data class Words(
/*    val valid: List<String>,
    val words: List<String>*/
    val word: String,
    val chDef: String,
    val enDef: String

) {
    fun getMessage(): String {
        return "$word\n中文释义：$chDef"
    }
}