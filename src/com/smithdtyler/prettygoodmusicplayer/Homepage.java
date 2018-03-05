package com.smithdtyler.prettygoodmusicplayer;

import android.app.Activity;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Homepage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //Buttons
        ImageButton btn_home = (ImageButton) findViewById(R.id.btn_home);
        ImageButton btn_playlists = (ImageButton) findViewById(R.id.btn_playlists);
        ImageButton btn_search = (ImageButton) findViewById(R.id.btn_search);
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

            @Override
            public void onClick(View v) {
                //Intent i = new Intent(Homepage.this, Search.class);
                //startActivity(i);
            }
        });
    }
}
