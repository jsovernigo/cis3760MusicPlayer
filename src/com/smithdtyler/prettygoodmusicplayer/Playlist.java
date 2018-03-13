package com.smithdtyler.prettygoodmusicplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Playlist extends Activity {
    private ArrayList<String> playlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        playlist = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        //Buttons
        ImageButton btn_add = (ImageButton) findViewById(R.id.btn_pladd);
        //ImageButton btn_edit = (ImageButton) findViewById(R.id.btn_pledit);
        //ImageButton btn_delete = (ImageButton) findViewById(R.id.btn_pldelete);


        //Add Item to Playlist
        btn_add.setOnClickListener(new View.OnClickListener() {

            private String playlistname;

            @Override
            public void onClick(View v) {
                //Dialog and Layout
                View mView = getLayoutInflater().inflate(R.layout.dialog_playlist, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(Playlist.this);
                builder.setView(mView);

                //Buttons on Dialog
                Button ok = (Button) findViewById(R.id.btn_dialog_ok);
                Button cancel = (Button) findViewById(R.id.btn_dialog_cancel);

                //Input Field on Dialog
                final EditText input = (EditText) findViewById(R.id.dialog_plname);

                builder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playlistname = input.getText().toString();
                        playlist.add(playlistname);
                    }
                });

                /*
                //Ok Button
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playlistname = input.getText().toString();
                        playlist.add(playlistname);
                    }
                });
                //Cancel Button
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //onCreateDialog().cancel();
                    }
                });*/

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
