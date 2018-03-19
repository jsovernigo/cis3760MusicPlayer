package com.smithdtyler.prettygoodmusicplayer;

import java.util.ArrayList;

/**
 * Created by Liam on 2018-03-18.
 */

public class PlaylistSongs extends Songs {
    private String playlistName;

    public PlaylistSongs(String pLine){
        super();
        //songList = new ArrayList<Song>(); called in the super
        String[] nameSplit  = pLine.split(":");
        playlistName = nameSplit[0];

        String[] songFiles = nameSplit[1].split(",");

        for(String songPath : songFiles){
            songList.add(new Song(songPath));
        }
    }

    public String toString(){
        String out = playlistName;
        out = out + ":";

        for(Song s : this.songList){
            out = out + s.getFilePath() + ",";
        }

        out = out + "\n";

        return out;
    }
}
