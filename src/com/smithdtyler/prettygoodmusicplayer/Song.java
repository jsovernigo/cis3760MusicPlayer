package com.smithdtyler.prettygoodmusicplayer;

/**
 * Created by Liam on 2018-02-13.
 * Pretty basic song object to store data
 */

import java.util.*;

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

    public static class SongNameComparator implements Comparator<Song> {
        @Override
        public int compare(final Song s1,final Song s2){
            return s1.getSongName().compareTo(s2.getSongName());
        }
    }

    public static class ArtistNameComparator implements Comparator<Song>{
        @Override
        public int compare(final Song s1,final Song s2){
            return s1.getArtist().compareTo(s2.getArtist());
        }
    }

    public static class AlbumNameComparator implements Comparator<Song>{
        @Override
        public int compare(final Song s1,final Song s2){
            return s1.getAlbum().compareTo(s2.getAlbum());
        }
    }

}

