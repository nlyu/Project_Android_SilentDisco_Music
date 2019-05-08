package com.cs160.finalproj.slientDisco;

import android.content.Intent;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import butterknife.BindView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserProfile extends AppCompatActivity {

    private LinearLayout userSongList;
    private ArrayList<View> v;
    private int userSongListChildNumber;
    private Button userLogoff;

    private RecyclerView mLikedMusic;
    private MusicAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ConstraintLayout mLikeMusicsPanel;

    private TextView mUserName;
    private TextView mProfileName;

    String mUsername;
    DatabaseReference userRef;
    DatabaseReference curUserRef;
    ArrayList<String> songList;
    ArrayList<MusicContainer> mLikedMusics;
    Map<String, String> mLikedMusicsUri = new HashMap<String, String>();

    //Connect to spotify
    private static final String CLIENT_ID = "b966d335ca304ac7a2a5ef6fd455b088";
    private static final String REDIRECT_URI = "http://com.example.spotify/callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getExtrasFromBundle();

        //set back button
        ImageView back_arrow = findViewById(R.id.user_profile_back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get current user in Firebase
        curUserRef = FirebaseDatabase.getInstance().getReference("cur_user");

        //fill the recycle view -> user's favourite song
        mLikedMusic = findViewById(R.id.music_list_recycler_view);
        mLikedMusics = new ArrayList<MusicContainer>();
        populateRecyclerViewData();
        setUpRecyclerView(mLikedMusics);

        //find the frontend and find the recycleview
        mLikeMusicsPanel = findViewById(R.id.user_profile_song_list);

        // get user's song list, currently not used until userprofile page is completed
        songList = new ArrayList<>();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(mUsername);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("song").exists()) {
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


        //TODO, Logoff, need to be firebase authentication
        userLogoff = findViewById(R.id.user_profile_log_off);
        userLogoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(UserProfile.this, LoginActivity.class);
                // myIntent.putExtra("key", value); //Optional parameters
                curUserRef.setValue("nobody");
                mSpotifyAppRemote.getPlayerApi().pause();
                startActivity(myIntent);
            }
        });

        //Get user name for profile text from Firebase
        mUserName = findViewById(R.id.user_profile_name);
        mProfileName = findViewById(R.id.profile_name);
        curUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String tmp = dataSnapshot.getValue(String.class);
                mUserName.setText(tmp);
                mProfileName.setText(tmp + "'s Profile");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read username failed: " + databaseError.getCode());
            }
        });
    }


    public void getExtrasFromBundle() {
        Intent intent = getIntent();
        // use intent bundle to set values
        // String value = intent.getStringExtra("key");
        mUsername = intent.getStringExtra("username");

    }

    public void populateRecyclerViewData() {
        // get favorite songs of this user
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new comment has been added, add it to the displayed list
                String song = dataSnapshot.getValue(String.class);
                String song_uri = dataSnapshot.getKey();
                MusicContainer tmp = new MusicContainer(song);
                mLikedMusics.add(tmp);
                mLikedMusicsUri.put(song, song_uri);
                mAdapter.notifyItemInserted(mLikedMusics.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("Firebase: ", "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                MusicContainer song = dataSnapshot.getValue(MusicContainer.class);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("Firebase: ", "onChildRemoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("Firebase: ", "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase: ", "postComments:onCancelled", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }

        };

        //TODO get user's song from /user/song in firebase, user need to change
        DatabaseReference songRef = FirebaseDatabase.getInstance().getReference("users").child(mUsername).child("song");
        songRef.addChildEventListener(childEventListener);
        //demo
        MusicContainer e6 = new MusicContainer("this is a song6");
//        mLikedMusics.add(e5);
    }

    public void setUpRecyclerView(ArrayList<MusicContainer> mLikedMusics) {
        // set trending recycler view
        mLikedMusic.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MusicAdapter(mLikedMusics);
        mLikedMusic.setLayoutManager(mLayoutManager);
        mLikedMusic.setAdapter(mAdapter);

        mAdapter.setOnItemClickedListener(new MusicAdapter.onItemClickedListener() {
            @Override
            public void onItemClick(int position) {
                MusicContainer pc = mLikedMusics.get(position);
                // go to the spotify
                Intent intent = new Intent(UserProfile.this, OpenInSpotify.class);
                intent.putExtra("song", pc.getMusicName());
                intent.putExtra("songUri", mLikedMusicsUri.get(pc.getMusicName()));
                startActivity(intent);
                Toast toast=Toast.makeText(getApplicationContext(),pc.getMusicName(),Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    //CONNECT TO SPOTIFY
    @Override
    protected void onStart() {
        super.onStart();

        //builder.setScopes(new String[]{"streaming"});
        //AuthenticationRequest request = builder.build();

        //Spotify API login ! Now send the request code
        //AuthenticationClient.openLoginActivity(this, SPOTIFY_REQUEST_CODE, request);

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        mSpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("Spotify:", "Connected! Yay!");
                        // Now you can start interacting with App Remote
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("Spotify:", throwable.getMessage(), throwable);
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}
