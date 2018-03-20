package com.smithdtyler.prettygoodmusicplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class Homepage extends Activity {
    private Object currentTheme;

    public static final String KEYWORDS = "keywords";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //Buttons
        Button btn_browse = (Button) findViewById(R.id.btn_browse);
        Button btn_search = (Button) findViewById(R.id.btn_search);
        Button btn_playlists = (Button) findViewById(R.id.btn_playlists);
        Button btn_settings = (Button) findViewById(R.id.btn_settings);

        //Move to Home
        btn_browse.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Homepage.this, ArtistList.class);
                startActivity(i);
            }
        });

        //Move to Settings
        btn_settings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Homepage.this, SettingsActivity.class);
                startActivity(i);
            }
        });


        btn_search.setOnClickListener(new View.OnClickListener() {

            private String searchString;

            @Override
            public void onClick(View v) {
                final Intent i = new Intent(Homepage.this, Search.class);
                AlertDialog.Builder builder = new AlertDialog.Builder(Homepage.this);

                final EditText input = new EditText(Homepage.this);

                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                searchString = "";

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        searchString = input.getText().toString();


                        String[] keywords = searchString.split(" ");
                        i.putExtra(Homepage.KEYWORDS, keywords);
                        startActivity(i);
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
