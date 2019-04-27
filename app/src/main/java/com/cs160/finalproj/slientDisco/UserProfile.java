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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Vector;

import butterknife.BindView;

public class UserProfile extends AppCompatActivity {

    private LinearLayout userSongList;
    private ArrayList<View> v;
    private int userSongListChildNumber;
    private Button userLogoff;

    String mUsername;
    DatabaseReference userRef;
    ArrayList<String> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getExtrasFromBundle();

        // get user's song list, currently not used until userprofile page is completed
        songList = new ArrayList<>();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(mUsername);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("songs").exists()) {
                    // TODO: change songName to URIs or something
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String songName = snapshot.getValue().toString();
                        songList.add(songName);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(UserProfile.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });

        //TODO, Only for demo use, should be recycle view
        userSongList = findViewById(R.id.user_profile_song_list_linear);

        userSongListChildNumber = userSongList.getChildCount();

        v = new ArrayList<View>(userSongListChildNumber);

        for(int i = 0; i< userSongListChildNumber; i++) {
            View v_cur = userSongList.getChildAt(i);
            v.add(v_cur);

            v.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserProfile.this, OpenInSpotify.class);
                    intent.putExtra("username", mUsername);
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

    public void getExtrasFromBundle() {
        Intent intent = getIntent();
        // use intent bundle to set values
        // String value = intent.getStringExtra("key");
        mUsername = intent.getStringExtra("username");

    }
}
