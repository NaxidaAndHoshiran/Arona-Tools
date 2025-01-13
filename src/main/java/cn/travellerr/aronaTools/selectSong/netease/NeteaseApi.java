package cn.travellerr.aronaTools.selectSong.netease;

import cn.travellerr.aronaTools.selectSong.entity.*;
import cn.travellerr.aronaTools.shareTools.BuildCommand;
import cn.travellerr.aronaTools.shareTools.MessageUtil;
import kotlin.text.Regex;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MusicKind;
import net.mamoe.mirai.message.data.MusicShare;
import net.mamoe.mirai.message.data.QuoteReply;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class NeteaseApi {
    public static void SearchNeteaseSong (Contact subject, MessageChain messages, User user, String songName, Regex SELECT_SONG) {
        try {
            NeteaseSearchList searchList = getSongList(songName);
            StringBuilder result = new StringBuilder();
            result.append("----列表----\n");
            for (int i = 0; i < searchList.getResult().getSongs().size(); i++) {
                result.append(i + 1).append(" ").append(searchList.getResult().getSongs().get(i).getName()).append(" - ").append(searchList.getResult().getSongs().get(i).getArtists().stream().map(NeteaseArtist::getName).reduce((a, b) -> a + " / " + b).orElse("")).append("\n");
            }
            subject.sendMessage(new QuoteReply(messages).plus(result.toString()));

            String msg = MessageUtil.getNextMessage(user, subject, messages, 30, java.util.concurrent.TimeUnit.SECONDS);
            if (!SELECT_SONG.matches(msg)) {
                subject.sendMessage(new QuoteReply(messages).plus("输入内容错误"));
                return;
            }

            int songId = Integer.parseInt(BuildCommand.getEveryValue(SELECT_SONG, msg).get(0));

            NeteaseSongInfo songInfo = getNeteaseSongInfo(searchList.getResult().getSongs().get(songId-1).getId());

            String songUrl = "https://music.163.com/#/song?id=" + searchList.getResult().getSongs().get(songId-1).getId();

            MusicShare musicShare = new MusicShare(MusicKind.NeteaseCloudMusic, songInfo.getName(), songInfo.getArtists().stream().map(Artist::getName).reduce((a, b) -> a + " / " + b).orElse(""), songUrl, songInfo.getAlbum().getPicUrl(), songUrl);

//            subject.sendMessage(new QuoteReply(messages).plus("----歌曲链接----\n https://music.163.com/#/song?id=" + searchList.getResult().getSongs().get(songId-1).getId()));

            subject.sendMessage(musicShare);




        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static NeteaseSearchList getSongList(String name) throws IOException {
        String url = replaceUrlInfo(NeteaseUrl.SEARCH_LIST_URL.url, "name", name);
        System.out.println(url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
            return NeteaseSearchList.Companion.fromInputStream(reader);
        }
    }

    public static NeteaseSongInfo getNeteaseSongInfo(Long id) throws IOException {
        String url = replaceUrlInfo(NeteaseUrl.SONG_INFO_URL.url, "id", id);
        url = replaceUrlInfo(url, "ids", "["+id+"]");
        System.out.println(url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
            NeteaseResponse neteaseResponse = NeteaseResponse.Companion.fromInputStreamReader(reader);
            return neteaseResponse.getSongs().get(0);
        }
    }



    private static String replaceUrlInfo(String url, String target, Object replacement) {
        return url.replace("%"+target+"%", URLEncoder.encode(String.valueOf(replacement), StandardCharsets.UTF_8));
    }
}
