package com.smithdtyler.prettygoodmusicplayer;

import java.util.ArrayList;

/**
 * Created by Liam on 2018-03-18.
 */

public class PlaylistSongs extends Songs {
    private String playlistName;
    /**
     * @author Liam Ewasko
     * @param pLine the line representation of a playlist format PLAYLIST_NAME:"/some/file/here.file",...,
     */
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
    /**
     * @author Liam Ewasko
     * @return the line representation of a playlist PLAYLIST_NAME:"/some/file/here/",...,
     */
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
