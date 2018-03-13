package com.smithdtyler.prettygoodmusicplayer;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Playlist extends Activity {
    private List<Map<String,String>> playlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        playlist = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
    }
}
