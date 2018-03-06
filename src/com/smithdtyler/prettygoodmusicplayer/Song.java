package com.smithdtyler.prettygoodmusicplayer;

/**
 * Created by Liam on 2018-02-13.
 * Pretty basic song object to store data
 */

import java.io.File;
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

    public Map<String, String> getListViewFormat() {
        HashMap<String, String> format;
        format = new HashMap<>();

        format.put("song", this.name);
        return format;
    }

    /**
     * @author Julian Sovernigo
     * @param keywords - the array of keywords that we are searching with.
     * @return true if any keyword exists in the artist, album, or song name.
     */
    public boolean matchesKeywords(String[] keywords) {

        for (int i = 0; i < keywords.length; i++) {
            String keyword = keywords[i];
            if (this.artist.toLowerCase().contains(keyword.toLowerCase()) || this.album.toLowerCase().contains(keyword.toLowerCase()) || this.name.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @author Julian Sovernigo
     * @return the file path for this file.
     */
    public String getFilePath() {
        File f = new File(this.file);
        String path = f.getAbsolutePath();

        return path;
    }

    /**
     * @author Julian Sovernigo
     * @return the artist's directory path.
     */
    public String getArtistPath() {
        File f = new File(this.file);
        return f.getParentFile().getParentFile().getAbsolutePath();
    }

    /**
     * @author Julian Sovernigo
     * @return the album directory, fully qualified.
     */
    public String getAlbumPath() {
        File f = new File(this.file);
        return f.getParentFile().getAbsolutePath();
    }


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

