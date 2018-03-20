package com.smithdtyler.prettygoodmusicplayer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class Playlist extends Activity {
    private static final String TAG = "Playlist";
    private ArrayList<String> playlist;
    private ListView lv;

    private String currentTheme;
    private String currentSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        playlist = new ArrayList<>();

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPref.getString("pref_theme", getString(R.string.light));
        String size = sharedPref.getString("pref_text_size", getString(R.string.medium));
        Log.i(TAG, "got configured theme " + theme);
        Log.i(TAG, "got configured size " + size);
        currentTheme = theme;
        currentSize = size;

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

        setContentView(R.layout.activity_playlist);

        //List
        lv = (ListView) findViewById(R.id.playlistListView);

        //Buttons
        ImageButton btn_add = (ImageButton) findViewById(R.id.btn_pladd);
        //ImageButton btn_edit = (ImageButton) findViewById(R.id.btn_pledit);
        //ImageButton btn_delete = (ImageButton) findViewById(R.id.btn_pldelete);

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, playlist);

        lv.setAdapter(arrayAdapter);


        //Add Item to Playlist
        btn_add.setOnClickListener(new View.OnClickListener() {

            private String playlistname;

            @Override
            public void onClick(View v) {
                //Dialog and Layout
                AlertDialog.Builder builder = new AlertDialog.Builder(Playlist.this);
                builder.setTitle("playlist name");

                //Input Field on Dialog
                final EditText input = new EditText(Playlist.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playlistname = input.getText().toString();
                        playlist.add(playlistname);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

