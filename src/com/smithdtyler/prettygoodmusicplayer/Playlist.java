package com.smithdtyler.prettygoodmusicplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class Playlist extends Activity {
    private ArrayList<String> playlist;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        playlist = new ArrayList<>();

        super.onCreate(savedInstanceState);
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
}
