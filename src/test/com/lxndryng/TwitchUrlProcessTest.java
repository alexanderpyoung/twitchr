package com.lxndryng;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class TwitchUrlProcessTest {

    private String jsonResponse = "{\"token\":\"{\\\"user_id\\\":null,\\\"channel\\\":\\\"dreamleague\\\",\\\"expires\\\":1416252018,\\\"chansub\\\":{\\\"view_until\\\":1924905600,\\\"restricted_bitrates\\\":[]},\\\"private\\\":{\\\"allowed_to_view\\\":true},\\\"privileged\\\":false}\",\"sig\":\"017ce8a1fc4e13368ac79ee2d311eb04e9cb276f\",\"mobile_restricted\":false}";

    @Before
    public void initialize() {
    }

    @Test
    public void testGetInfo() throws Exception {
        TwitchUrlProcess tup = new TwitchUrlProcess("dreamleague");
        List<String> infoList = tup.getInfo();
        assertThat(infoList.get(0), containsString("user_id"));
        assertThat(infoList.get(1).length(), is(40));
    }

    @Test
    public void testGetSig() throws Exception {
        TwitchUrlProcess tup = new TwitchUrlProcess("dreamleague");
        assertThat(tup.getSig(this.jsonResponse), is("017ce8a1fc4e13368ac79ee2d311eb04e9cb276f"));
    }

    @Test
    public void testGetToken() throws Exception {
        TwitchUrlProcess tup = new TwitchUrlProcess("dreamleague");
        assertThat(tup.getToken(this.jsonResponse), containsString("\"user_id\":null,"));
        assertThat(tup.getToken(this.jsonResponse), not(containsString("sig")));
    }

    @Test
    public void testGetPlaylist() throws Exception {
        TwitchUrlProcess tup = new TwitchUrlProcess("dreamleague");
        List<String> token = tup.getInfo();
        String playlist = tup.getPlaylist(token.get(0), token.get(1));
        assertThat(playlist, containsString("#EXT-X-TWITCH-INFO:NODE="));
    }

    @Test
    public void testGetHLS() throws Exception {
        TwitchUrlProcess tup = new TwitchUrlProcess("dreamleague");
        List<String> token = tup.getInfo();
        String playlist = tup.getPlaylist(token.get(0), token.get(1));
        List<Map<String, String>> HlsList  = tup.getHLS(playlist);
        assertThat(HlsList.get(0).get("quality"), is("chunked"));
    }
}