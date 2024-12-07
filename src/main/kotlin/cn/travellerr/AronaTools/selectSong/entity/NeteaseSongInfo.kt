package cn.travellerr.aronaTools.selectSong.entity

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStreamReader


@Serializable
data class NeteaseResponse(
    val songs: List<NeteaseSongInfo>,
    val equalizers: Map<String, @Contextual Any>,
    val code: Int
) {
    companion object {
        fun fromInputStreamReader(inputStream: InputStreamReader): NeteaseResponse {
            return Json.decodeFromString(serializer(), inputStream.readText())
        }
    }
}

@Serializable
data class NeteaseSongInfo(
    val name: String,
    val id: Long,
    val position: Int,
    val alias: List<String>,
    val status: Int,
    val fee: Int,
    val copyrightId: Int,
    val disc: String,
    val no: Int,
    val artists: List<Artist>,
    val album: Album,
    val starred: Boolean,
    val popularity: Int,
    val score: Int,
    val starredNum: Int,
    val duration: Int,
    val playedNum: Int,
    val dayPlays: Int,
    val hearTime: Int,
    val sqMusic: Music?,
    val hrMusic: Music?,
    val ringtone: String,
    val crbt: String?,
    val audition: String?,
    val copyFrom: String,
    val commentThreadId: String,
    val rtUrl: String?,
    val ftype: Int,
    val rtUrls: List<String>,
    val copyright: Int,
    val transName: String?,
    val sign: String?,
    val mark: Int,
    val originCoverType: Int,
    val originSongSimpleData: String?,
    val single: Int,
    val noCopyrightRcmd: String?,
    val hMusic: Music?,
    val mMusic: Music?,
    val lMusic: Music?,
    val bMusic: Music?,
    val mvid: Int,
    val rtype: Int,
    val rurl: String?,
    val mp3Url: String?,
    val transNames: List<String>
)

@Serializable
data class Artist(
    val name: String,
    val id: Long,
    val picId: Long,
    val img1v1Id: Long,
    val briefDesc: String,
    val picUrl: String,
    val img1v1Url: String,
    val albumSize: Int,
    val alias: List<String>,
    val trans: String,
    val musicSize: Int,
    val topicPerson: Int
)

@Serializable
data class Album(
    val name: String,
    val id: Long,
    val type: String,
    val size: Int,
    val picId: Long,
    val blurPicUrl: String,
    val companyId: Int,
    val pic: Long,
    val picUrl: String,
    val publishTime: Long,
    val description: String,
    val tags: String,
    val company: String,
    val briefDesc: String,
    val artist: Artist,
    val songs: List<String>,
    val alias: List<String>,
    val status: Int,
    val copyrightId: Int,
    val commentThreadId: String,
    val artists: List<Artist>,
    val subType: String,
    val transName: String?,
    val onSale: Boolean,
    val mark: Int,
    val gapless: Int,
    val dolbyMark: Int,
    val picId_str: String
)

@Serializable
data class Music(
    val name: String?,
    val id: Long,
    val size: Int,
    val extension: String,
    val sr: Int,
    val dfsId: Long,
    val bitrate: Int,
    val playTime: Int,
    val volumeDelta: Int
)