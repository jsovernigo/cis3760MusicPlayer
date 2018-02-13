package com.smithdtyler.prettygoodmusicplayer;

/**
 * Created by Liam on 2018-02-13.
 */

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.*;

/*
    Better Song list prototype

*/


public class Songs {
    private List<Song> songList;

    public Songs(){
        songList = new ArrayList<Song>();
    }

    public void addSong(Song s){
        songList.add(s);
    }

    public Song getSongByIndex(int i){
        if(i < 0 || i >= songList.size())
            return null;
        return songList.get(i);
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

}
