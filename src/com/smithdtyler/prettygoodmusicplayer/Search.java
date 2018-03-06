package com.smithdtyler.prettygoodmusicplayer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.Arrays;

/**
 * Created by julian on 28/02/18.
 */

public class Search extends Activity {
	public static final String SONG_ABS_FILE_NAME_LIST = "SEARCH";
	public static final String SONG_ABS_FILE_NAME_LIST_POSITION = "SEARCH_POSITION";
	public static final String[] KEYWORDS = {""};
	private static final String TAG = "Search";

	private static Songs listOfSongs;


	private SimpleAdapter simpleAdpt;
	private String currentTheme;
	private String currentSize;
	private boolean hasResume = false;
	private int resumeFilePos = -1;
	private int resumeProgress;
	private String resume;
	private boolean audiobookMode;

    /**
     * populateSongs
     * This function uses a root directory to collect all classified songs from the
     * music library.
     * @author Julian Sovernigo
     * @param rootDir - the root directory of the songs that we are looking for.
     */
	private void populateSongs(String rootDir, final String[] keywords) {
        listOfSongs = new Songs();

	    File root = new File(rootDir);
	    File[] artists = root.listFiles();

	    // for all artists
		if (artists != null) {
			for (File f : artists) {
				File[] albums = f.listFiles();

				// for all albums
				if (albums != null) {
					for (File a : albums) {
						File[] songs = a.listFiles();

						// for all songs
						if (songs != null) {
							for (File s : songs) {
								Song song = new Song(s.getName(), f.getName(), a.getName(), s.getAbsolutePath());

								// if we have keywords we need to fulfill
								if (keywords != null && keywords.length > 0) {
									if (song.matchesKeywords(keywords)) {
										listOfSongs.addSong(song);
									}
								} else {
									listOfSongs.addSong(song);
								}
							}
						}
					}
				}
			}
        }

        return;
	}

    /**
     * onCreate
     * when the page is created, use the search terms to collect all songs.
     * Otherwise, if no Keywords were provided, just ignore it.
     * @author Julian Sovernigo
     * @param savedInstanceState
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		 // Get the message from the intent
	    Intent intent = getIntent();
		final String[] keywords = intent.getStringArrayExtra(Homepage.KEYWORDS);

		String s = "";
		for (String k : keywords) {
			s = s + k + " ";
		}
		Log.i(TAG, s);

		// create the bar up at the top of the page.
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Search");

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPref.getString("pref_theme", getString(R.string.light));
        String size = sharedPref.getString("pref_text_size", getString(R.string.medium));
        audiobookMode = sharedPref.getBoolean("pref_audiobook_mode", false);

        currentTheme = theme;
        currentSize = size;
        // These settings were fixed in english for a while, so check for old style settings as well as language specific ones.


        // set the theme to the proper dark or light theme
        if(theme.equalsIgnoreCase(getString(R.string.dark)) || theme.equalsIgnoreCase("dark")){
        	Log.i(TAG, "setting theme to " + theme);
        	if(size.equalsIgnoreCase(getString(R.string.small)) || size.equalsIgnoreCase("small")){
        		setTheme(R.style.PGMPDarkSmall);
        	} else if (size.equalsIgnoreCase(getString(R.string.medium)) || size.equalsIgnoreCase("medium")){
        		setTheme(R.style.PGMPDarkMedium);
        	} else {
        		setTheme(R.style.PGMPDarkLarge);
        	}
        } else if (theme.equalsIgnoreCase(getString(R.string.light)) || theme.equalsIgnoreCase("light")){
        	Log.i(TAG, "setting theme to " + theme);
        	if(size.equalsIgnoreCase(getString(R.string.small)) || size.equalsIgnoreCase("small")){
        		setTheme(R.style.PGMPLightSmall);
        	} else if (size.equalsIgnoreCase(getString(R.string.medium)) || size.equalsIgnoreCase("medium")){
        		setTheme(R.style.PGMPLightMedium);
        	} else {
        		setTheme(R.style.PGMPLightLarge);
        	}
        }

		setContentView(R.layout.activity_song_list);

	    /* TODO FIX? */
	    this.populateSongs(/*Utils.getBestGuessMusicDirectory().getAbsolutePath()*/"/storage/emulated/0/", keywords);


        simpleAdpt = new SimpleAdapter(this, listOfSongs.transformToListViewCompat(), R.layout.pgmp_list_item, new String[] {"song"}, new int[] {R.id.PGMPListItemText});
        ListView lv = (ListView) findViewById(R.id.songListView);
        lv.setAdapter(simpleAdpt);

        // React to user clicks on item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

             public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                     long id) {

            	 Intent intent = new Intent(Search.this, NowPlaying.class);

            	 intent.putExtra(AlbumList.ALBUM_NAME, listOfSongs.getSongByIndex(position).getAlbum());
            	 intent.putExtra(ArtistList.ARTIST_NAME, listOfSongs.getSongByIndex(position).getArtist());
            	 String[] songNamesArr = new String[listOfSongs.size()];

            	 intent.putExtra(SONG_ABS_FILE_NAME_LIST, listOfSongs.getFilePaths());
            	 intent.putExtra(ArtistList.ARTIST_ABS_PATH_NAME, listOfSongs.getSongByIndex(position).getFilePath());
            	 intent.putExtra(NowPlaying.KICKOFF_SONG, true);

            	 if(hasResume){
            		 if(position == 0){
   	            		 intent.putExtra(SONG_ABS_FILE_NAME_LIST_POSITION, resumeFilePos);
   	            		 intent.putExtra(MusicPlaybackService.TRACK_POSITION, resumeProgress);
            		 } else {
            			 // a 'resume' option has been added to the beginning of the list
            			 // so adjust the selection to compensate
    	            	 intent.putExtra(SONG_ABS_FILE_NAME_LIST_POSITION, position - 1);
            		 }
            	 } else {
	            	 intent.putExtra(SONG_ABS_FILE_NAME_LIST_POSITION, position);
            	 }
            	 startActivity(intent);
             }
        });

		lv.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				showSongSettingsDialog();
				return true;
			}
		});

	}

	private void showSongSettingsDialog(){
		new AlertDialog.Builder(this).setTitle("Song Details")
				.setItems(new String[]{"enabled"}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
	}

    @Override
	protected void onResume() {
		super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPref.getString("pref_theme", getString(R.string.light));
        String size = sharedPref.getString("pref_text_size", getString(R.string.medium));
        boolean audiobookModePref = sharedPref.getBoolean("pref_audiobook_mode", false);
        Log.i(TAG, "got configured theme " + theme);
        Log.i(TAG, "Got configured size " + size);
        if (currentTheme == null) {
        	currentTheme = theme;
        }

        if (currentSize == null) {
        	currentSize = size;
        }

        boolean resetResume = false;
        if (audiobookMode != audiobookModePref) {
        	resetResume = true;
        }
        SharedPreferences prefs = getSharedPreferences("PrettyGoodMusicPlayer", MODE_PRIVATE);
        String newResume = prefs.getString(Utils.getBestGuessMusicDirectory().getAbsolutePath(), null);
        if (resume != null && newResume != null && !newResume.equals(resume)) {
        	resetResume = true;
        } else if (resume == null && newResume != null) {
        	resetResume = true;
        }

        if(!currentTheme.equals(theme) || !currentSize.equals(size) || resetResume){
        	// Calling finish and startActivity will re-launch this activity, applying the new settings
        	finish();
        	startActivity(this.getIntent());
        }
	}

}

