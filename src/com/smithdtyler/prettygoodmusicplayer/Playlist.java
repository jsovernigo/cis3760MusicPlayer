package com.smithdtyler.prettygoodmusicplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Playlist extends Activity {
    //ArrayList
    private ArrayList<String> playlist;

    //Mode (1 - Delete, 2 - Edit)
    private int mode = 0;

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        playlist = new ArrayList<>();
        mode = 0;
        loadPlaylist(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        //List
        lv = (ListView) findViewById(R.id.playlistListView);

        //Buttons
        final ImageButton btn_add = (ImageButton) findViewById(R.id.btn_pladd);
        final ImageButton btn_edit = (ImageButton) findViewById(R.id.btn_pledit);
        final ImageButton btn_delete = (ImageButton) findViewById(R.id.btn_pldelete);
        final ImageButton btn_cancel = (ImageButton) findViewById(R.id.btn_plcancel);
        btn_cancel.setVisibility(View.GONE);

        //Adapter
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, playlist);

        lv.setAdapter(arrayAdapter);


        //Add Item to Playlist
        btn_add.setOnClickListener(new View.OnClickListener() {

            private String playlistname;

            @Override
            public void onClick(View v) {
                //Dialog and Layout
                AlertDialog.Builder builder = new AlertDialog.Builder(Playlist.this);
                builder.setTitle("New Playlist Name");

                //Input Field on Dialog
                final EditText input = new EditText(Playlist.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Add item to Playlist
                        playlistname = input.getText().toString();
                        playlist.add(playlistname);

                        //Save Playlist
                        savePlaylist();
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

        //Edit Item on Playlist
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = 2;
                btn_cancel.setVisibility(View.VISIBLE);
            }
        });

        //Delete Item on Playlist
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = 1;
                btn_cancel.setVisibility(View.VISIBLE);
            }
        });

        //Cancel Edit or Delete
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = 0;
                btn_cancel.setVisibility(View.GONE);
            }
        });

        //Mode
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Edit Item on Playlist
                if (mode == 1) {
                    //Dialog and Layout
                    AlertDialog.Builder builder = new AlertDialog.Builder(Playlist.this);
                    builder.setTitle("Delete Confirmation");
                    final int pos = position;

                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Remove Item, Save Playlist and Show Changes
                            playlist.remove(pos);
                            savePlaylist();
                            arrayAdapter.notifyDataSetChanged();

                            Toast.makeText(getApplicationContext(), "Playlist Deleted", Toast.LENGTH_SHORT).show();
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
                //Delete Item on Playlist
                else if (mode == 2) {
                    //Dialog and Layout
                    AlertDialog.Builder builder = new AlertDialog.Builder(Playlist.this);
                    builder.setTitle("Set Playlist Name");
                    final int pos = position;

                    //Input Field on Dialog
                    final EditText input = new EditText(Playlist.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Add item to Playlist
                            String playlistname = input.getText().toString();
                            playlist.set(pos, playlistname);

                            //Save Playlist
                            savePlaylist();
                            arrayAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), playlistname + " Edited", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                    mode = 0;
                }
            }
        });
    }

    private void savePlaylist() {
        SharedPreferences savesharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = savesharedPref.edit();
        editor.putInt("playlist_size", playlist.size());

        for (int i = 0; i < playlist.size(); i = i + 1) {
            editor.remove("playlist" + i);
            editor.putString("playlist" + i, playlist.get(i));
        }

        editor.commit();
    }

    private void loadPlaylist(Context context) {
        SharedPreferences loadsharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int size = loadsharedPref.getInt("playlist_size", 0);
        playlist.clear();

        for (int i = 0; i < size; i = i + 1) {
            playlist.add(loadsharedPref.getString("playlist" + i,null));
        }
    }
}
