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
    private List<Song> songList;
    private String playlistName;

    public Songs(){
        songList = new ArrayList<Song>();
    }
    /*
    public Songs(String name){
        songList = new ArrayList<Song>();
        playlistName = name;
    }*/

    public Songs(String pLine){
        songList = new ArrayList<Song>();
        String[] nameSplit  = pLine.split(":");
        playlistName = nameSplit[0];

        String[] songFiles = nameSplit[1].split(",");

        for(String songPath : songFiles){
            songList.add(new Song(songPath));
        }
    }

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

    public List<String> transformToArrayAdapter() {
        List<String> export;
        export = new ArrayList<String>();
        for (Song s : this.songList) {
            export.add(s.getSongName());
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

    public List<Song> getSongsByArtist(String artist){
        List<Song> songs = new ArrayList<Song>();

        for(Song sng : songList){
            if(sng.getArtist().equals(artist))
                songs.add(sng);
        }

        return songs;
    }

    public List<Song> getSongsByAlbum(String album){
        List<Song> songs = new ArrayList<Song>();

        for(Song sng: songList){
            if(sng.getAlbum().equals(album))
                songs.add(sng);
        }

        return songs;
    }

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

    public void removeSongByName(String name){
        for(Song s : this.songList){
            if(s.getSongName().equals(name)){
                songList.remove(s);
            }
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
