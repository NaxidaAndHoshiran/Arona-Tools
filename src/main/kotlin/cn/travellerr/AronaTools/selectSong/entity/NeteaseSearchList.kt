package cn.travellerr.aronaTools.selectSong.entity

import cn.travellerr.aronaTools.selectSong.netease.NeteaseUrl
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStreamReader
import java.util.concurrent.atomic.AtomicInteger

@Serializable
data class NeteaseSearchResult (
    val songs: List<NeteaseSong>
)

@Serializable
data class NeteaseSong (
    val id: Long,
    val name: String,
    val artists: List<NeteaseArtist>,
    val album: NeteaseAlbum,
    val duration: Int,
    val copyrightId: Int,
    val status: Int,
    val alias: List<String>,
    val rtype: Int,
    val ftype: Int,
    val mvid: Int,
    val fee: Int,
    val rUrl: String?,
    val mark: Long
)

@Serializable
data class NeteaseArtist (
    val id: Long,
    val name: String,
    val picUrl: String?,
    val alias: List<String>,
    val albumSize: Int,
    val picId: Long,
    val fansGroup: String?,
    val img1v1Url: String,
    val img1v1: Int,
    val trans: String?
)

@Serializable
data class NeteaseAlbum (
    val id: Long,
    val name: String,
    val artist: NeteaseArtist,
    val publishTime: Long,
    val size: Int,
    val copyrightId: Int,
    val status: Int,
    val picId: Long,
    val mark: Long
)


@Serializable
data class NeteaseSearchList (
    val result: NeteaseSearchResult,
    val hasMore: Boolean,
    val songCount: Int
) {
    companion object {
        fun fromString(json: String): NeteaseSearchList {
            return Json.decodeFromString(serializer(), json)
        }
        fun fromInputStream(json: InputStreamReader): NeteaseSearchList {
            return Json.decodeFromString(serializer(), json.readText())
        }
    }
    fun getAllSongNames(): List<String> {
        return result.songs.map { it.name }
    }

    fun getAllSongIds(): List<Long> {
        return result.songs.map { it.id }
    }


    fun getFirstArtistNames(): List<String> {
        return result.songs.map { it.artists.firstOrNull()?.name ?: "Unknown" }
    }

    fun getFormattedSongList(): String {
        val id = AtomicInteger(1)
        return result.songs.joinToString("\n") { song ->
            "${id.getAndIncrement()} ${song.name} - ${song.artists.joinToString(" / ") { it.name }}"
        }
    }

    fun getSongUrl(index: Int): String {
        return NeteaseUrl.SONG_URL.url+result.songs[index].id
    }

}