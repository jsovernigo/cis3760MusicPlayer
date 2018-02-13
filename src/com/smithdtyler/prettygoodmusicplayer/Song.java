package com.smithdtyler.prettygoodmusicplayer;

/**
 * Created by Liam on 2018-02-13.
 * Pretty basic song object to store data
 */

public class Song {
    private String name;
    private String artist;
    private String album;
    private String file;


    public Song(String songName,String artistName,String albumName,String fileName){
        name = songName;
        artist = artistName;
        album = albumName;
        file = fileName;
    }

    public String getSongName(){return name;}
    public String getArtist(){return artist;}
    public String getAlbum(){return album;}
    public String getFileName(){return file;}


}
