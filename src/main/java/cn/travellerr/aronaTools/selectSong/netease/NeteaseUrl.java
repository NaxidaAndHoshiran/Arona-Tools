package cn.travellerr.aronaTools.selectSong.netease;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NeteaseUrl {
    SEARCH_LIST_URL("https://music.163.com/api/search/get?s=%name%&type=1&offset=0&limit=15"),
    SONG_URL("https://music.163.com/#/song?id="),
    SONG_INFO_URL("https://music.163.com/api/song/detail/?id=%id%&ids=%ids%"),;

    public final String url;
}
