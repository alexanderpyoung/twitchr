package com.lxndryng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexander on 17/11/14.
 */
public class TwitchUrlProcess {
    private String channel;

    public TwitchUrlProcess(String channel) {
        this.channel = channel;
    }

    public List<String> getInfo() throws Exception {
        URL twitchAccess = new URL("https://api.twitch.tv/api/channels/" + channel + "/access_token");
        HttpURLConnection connection = (HttpURLConnection) twitchAccess.openConnection();

        BufferedReader twitchAccessResponse = new BufferedReader(
                                                new InputStreamReader(connection.getInputStream()));
        String jsonLine;
        StringBuffer fullJson = new StringBuffer();

        while ((jsonLine = twitchAccessResponse.readLine()) != null) {
            fullJson.append(jsonLine);
        }
        String json = fullJson.toString();
        String sig = this.getSig(json);
        String token = this.getToken(json);
        List<String> returnValues = new ArrayList<String>();
        returnValues.add(token);
        returnValues.add(sig);
        return returnValues;
    }

    public String getToken(String json) {
        int start_index = 10;
        int end_index = json.indexOf("\",\"sig\"");
        String subString = json.substring(start_index, end_index);
        String token = subString.replace("\\", "").replace("\n", "").replace("\r", "");
        return token;
    }

    public String getSig(String json) {
        int index = json.indexOf("sig"); // this will not work if the channel has the string "sig" in its name
        String subString = json.substring(index + 6, index + 46);
        return subString;
    }

    public String getPlaylist(String token, String sig) throws Exception {
        URL twitchPlaylist = new URL("http://usher.justin.tv/api/channel/hls/" + this.channel + ".m3u8?token=" + token + "&sig=" + sig +"&allow_source=true");
        HttpURLConnection connection = (HttpURLConnection) twitchPlaylist.openConnection();

        BufferedReader twitchPlaylistResponse = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        );

        String playlistLine;
        StringBuffer fullPlaylist = new StringBuffer();

        while ((playlistLine = twitchPlaylistResponse.readLine()) != null) {
            fullPlaylist.append(playlistLine + "\n");
        }

        String playlist = fullPlaylist.toString();
        return playlist;
    }

    public List<Map<String, String>> getHLS(String playlist) {
        //strip the two header lines
        int index1 = playlist.indexOf("\n") + 2;
        int index2 = playlist.indexOf("\n", index1);
        String playlistNoHeader = playlist.substring(index2 + 1);
        String[] playlistItems = playlistNoHeader.split("\n#EXT-X-MEDIA:TYPE=VIDEO,GROUP-ID=.*,NAME=.*,AUTOSELECT=YES,DEFAULT=YES");
        ArrayList<Map<String, String>> returnOptions = new ArrayList<Map<String, String>>();
        for (String item : playlistItems) {
            String quality = item.substring(item.indexOf(",VIDEO") + 8, item.indexOf("\"\n"));
            String bandwidth = item.substring(item.indexOf("BANDWIDTH") + 10, item.indexOf(",", item.indexOf("BANDWIDTH")));
            String url = item.substring(item.indexOf("\"\n") + 2);
            Map<String, String> itemMap = new HashMap<String, String>();
            itemMap.put("quality", quality);
            itemMap.put("bandwidth", bandwidth);
            itemMap.put("url", url);
            returnOptions.add(itemMap);
        }
        return returnOptions;
    }
}