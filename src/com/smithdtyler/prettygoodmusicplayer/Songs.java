package com.smithdtyler.prettygoodmusicplayer;

/**
 * Created by Liam on 2018-02-13.
 */

import java.util.*;
import java.io.*;


/*
    Better Song list prototype
*/


public class Songs {
    protected List<Song> songList;

    /**
     * @author Liam Ewasko
     */
    public Songs(){
        songList = new ArrayList<Song>();
    }


    /**
     * @author Liam Ewasko
     * @param s song to add to list
     */
    public void addSong(Song s){
        songList.add(s);
    }

    /**
     * @author Julian Sovernigo
     * @return the arraylist of map<String, String> objects that ListView expects.
     */
    public List<Map<String, String>> transformToListViewCompat() {
        List<Map<String, String>> export;
        export = new ArrayList<Map<String, String>>();

        // for each song we have in our list
        for (Song s : this.songList) {
            export.add(s.getListViewFormat());
        }

        return export;
    }

    /**
     * @author Julian Sovernigo
     * @return the size of the list of songs.
     */
    public int size() {
        return this.songList.size();
    }

    public Song getSongByIndex(int i){
        if(i < 0 || i >= songList.size())
            return null;
        return songList.get(i);
    }

    /**
     * @author Julian Sovernigo
     * @return a string array of all file paths.
     */
    public String[] getFilePaths() {
        int i = 0;
        String[] paths = new String[this.songList.size()];
        for (Song s: this.songList) {
            paths[i] = s.getFilePath();
            i++;
        }

        return paths;
    }


    /**
     * @author Liam Ewasko
     * @return a list of Songs with the same artist.
     * @param artist a string containing the name of the artist to find
     */
    public List<Song> getSongsByArtist(String artist){
        List<Song> songs = new ArrayList<Song>();

        for(Song sng : songList){
            if(sng.getArtist().equals(artist))
                songs.add(sng);
        }

        return songs;
    }
    /**
     * @author Liam Ewasko
     * @return a list of Songs from the same album
     * @param album a string containing the name of the album
     */
    public List<Song> getSongsByAlbum(String album){
        List<Song> songs = new ArrayList<Song>();

        for(Song sng: songList){
            if(sng.getAlbum().equals(album))
                songs.add(sng);
        }

        return songs;
    }
    /**
     * @author Liam Ewasko
     * @return a list of Songs which have names containing the parameter name
     * @param name the name keyword to search for
     */
    public List<Song> searchSongs(String name){
        List<Song> songs = new ArrayList<Song>();
        Collections.sort(songs,new Song.SongNameComparator());
        for(Song s : songs) {
            if (s.getSongName().contains(name)) {
                songs.add(s);
            }
        }
        return songs;
    }


    public List<Song> searchByKeywords(String[] keywords) {
        List<Song> songs = new ArrayList<>();

        for(Song s : this.songList) {
            if (s.matchesKeywords(keywords)) {
                songs.add(s);
            }
        }
        return songs;
    }
    /**
     * @author Liam Ewasko
     * @param name the name of the song to remove from the list
     */
    public void removeSongByName(String name){
        for(Song s : this.songList){
            if(s.getSongName().equals(name)){
                songList.remove(s);
            }
        }
    }



}
