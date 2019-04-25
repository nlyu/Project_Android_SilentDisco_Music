package com.cs160.finalproj.slientDisco;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Vector;

import butterknife.BindView;

public class UserProfile extends AppCompatActivity {

    private LinearLayout userSongList;
    private ArrayList<View> v;
    private int userSongListChildNumber;
    private Button userLogoff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userSongList = findViewById(R.id.user_profile_song_list_linear);
        userSongListChildNumber = userSongList.getChildCount();

        v = new ArrayList<View>(userSongListChildNumber);

        //TODO, Only for demo use, should be recycle view
        for(int i = 0; i< userSongListChildNumber; i++) {
            View v_cur = userSongList.getChildAt(i);
            v.add(v_cur);

            v.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserProfile.this, OpenInSpotify.class);
                    startActivity(intent);
                }
            });
        }

        //TODO, Logoff, need to be firebase authentication
        userLogoff = findViewById(R.id.user_profile_log_off);
        userLogoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(UserProfile.this, LoginActivity.class);
                // myIntent.putExtra("key", value); //Optional parameters
                startActivity(myIntent);
            }
        });
    }
}
