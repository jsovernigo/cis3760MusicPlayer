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
    public static final String KEYWORDS = "KEYWORDS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //Buttons
        ImageButton btn_home = (ImageButton) findViewById(R.id.btn_home);
        ImageButton btn_playlists = (ImageButton) findViewById(R.id.btn_playlists);
        Button btn_search = (Button) findViewById(R.id.btn_search);
        ImageButton btn_settings = (ImageButton) findViewById(R.id.btn_settings);

        //Move to Home
        btn_home.setOnClickListener(new View.OnClickListener(){
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

        //Move to Search
        btn_search.setOnClickListener(new View.OnClickListener() {

            private String searchString;

            @Override
            public void onClick(View v) {
                final Intent i = new Intent(Homepage.this, Search.class);
                AlertDialog.Builder builder = new AlertDialog.Builder(Homepage.this);

                final EditText input = new EditText(Homepage.this);

                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
