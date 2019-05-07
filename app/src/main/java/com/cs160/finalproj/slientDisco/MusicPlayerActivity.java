package com.cs160.finalproj.slientDisco;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.service.carrier.CarrierMessagingService;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cs160.finalproj.slientDisco.player.Player;
import com.cs160.finalproj.slientDisco.player.Track;
import com.cs160.finalproj.slientDisco.support.Constants;
import com.cs160.finalproj.slientDisco.support.utils.AppUtils;
import com.cs160.finalproj.slientDisco.support.utils.PlayerUtils;
import com.cs160.finalproj.slientDisco.support.utils.UIUtils;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.PlayerState;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MusicPlayerActivity extends AppCompatActivity {

    //TODO, this music play will play all local file in the internal storage/Music directory
    //TODO, need to add backend in future
    @BindView(R.id.track_cover) protected ImageView trackCover;
    @BindView(R.id.previous_track) protected ImageView previousTrack;
    @BindView(R.id.play_or_pause) protected ImageView playOrPause;
    @BindView(R.id.next_track) protected ImageView nextTrack;
    @BindView(R.id.music_party_help_icon) protected ImageView helpIcon;
    @BindView(R.id.music_party_account_icon) protected ImageView userIcon;

    @BindView(R.id.SlideUpPanel) protected View slideUpPanel;

    @BindView(R.id.track_title) protected TextView trackTitle;
    @BindView(R.id.track_progress) protected TextView trackProgress;
    @BindView(R.id.track_duration) protected TextView trackDuration;

    @BindView(R.id.track_timeline) protected SeekBar trackTimeline;

    private RecyclerView mSlideUpMusic;
    ArrayList<MusicContainer> mSlideUpMusics;
    private MusicAdapter mMusicAdapter;
    private RecyclerView.LayoutManager mMusicLayoutManager;

    private int position;
    private int oldPosition;
    private int trackTimePosition;
    private int slideup = 0;
    private boolean isTimeListenerSet;

    private Handler timeHandler = new Handler();
    private String song_uri;
    private String accessToken;
    private String party_name;
    private SpotifyAppRemote mSpotifyAppRemote;
    private static final String CLIENT_ID = "b966d335ca304ac7a2a5ef6fd455b088";
    private static final String REDIRECT_URI = "http://com.example.spotify/callback";

    private RecyclerView mUserRV;
    private RecyclerView.LayoutManager mUserLayoutManager;
    private UserAdapter mUserAdapter;
    private ArrayList<String> mNames;
    private String mPartyName;
    private TextView partyHeader;

    private String mGenreName;
    private String mSongName;
    private String mMode;  //create party or join party
    private double mLatitude;
    private double mLongitude;
    private boolean mPublic;
    private PartyContainer mPartyData;
    private DatabaseReference mDatabase;
    Map<String, HashMap<String, String>> allPartyData;

    String mUsername;

    private void playOrPause() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            AppUtils.checkReadStoragePermission(this);
        } else {
            if (Player.getInstance().isPausing()) {
                Log.d("Spotify:", " play");
                UIUtils.setImageDrawable(playOrPause, R.drawable.ic_play);
                Player.getInstance().resume();
                mSpotifyAppRemote.getPlayerApi().pause();
            } else {
                Log.d("Spotify:", " pause");
                UIUtils.setImageDrawable(playOrPause, R.drawable.ic_pause);
                Player.getInstance().pause();
                mSpotifyAppRemote.getPlayerApi().resume();
            }
        }
    }


    //this is the spotify button
    @OnClick(R.id.previous_track)
    public void onPreviousTrackClick() {
        startActivity(getPackageManager().getLaunchIntentForPackage("com.spotify.music"));
    }

    //this is the play/pause button
    @OnClick(R.id.play_or_pause)
    public void onPlayPauseClick() {
        playOrPause();
    }

    //this is favourite button
    @OnClick(R.id.next_track)
    public void onNextTrackClick() {
        mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(new CallResult.ResultCallback<PlayerState>() {
            @Override
            public void onResult(PlayerState playerState) {
                String tmp = playerState.track.uri;
                Toast.makeText(getApplicationContext(), "Song " + playerState.track.name + " is added!", Toast.LENGTH_SHORT).show();
                Log.d("Spotify: ", " favourite song uri is" + tmp + playerState.track.name);
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(mUsername).child("song");
                userRef.child(tmp).setValue(playerState.track.name);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        ButterKnife.bind(this);
        getExtrasFromBundle();

        //Set up audience list
        getComponents();

        //Set the Name of the party text
        setTitleHeader();
        setUpUserRecyclerView();

        if(mMode == "create") {
            //Push the data from create party to firebase
            setDatabaseListener();
            mPartyData = new PartyContainer(mPartyName, 1, mGenreName, mSongName, 0);
            pushPartyFirebase(mPartyData);
        }

        trackTimeline.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int length, boolean state) {}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Player.getInstance().toTime(PlayerUtils.toMilliseconds(seekBar.getProgress()));
            }
        });

        // If the track title is long - start the running line:
        trackTitle.setSelected(true);

        slideUpPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams params = v.getLayoutParams();
                float px;
                Resources r = getResources();
                if(slideup == 1){
                    px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, r.getDisplayMetrics());
                    slideup = 0;
                } else {
                    px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, r.getDisplayMetrics());
                    slideup = 1;
                }

                params.height = Math.round(px);
                v.setLayoutParams(params);
            }
        });

        helpIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicPlayerActivity.this, HelpActivity.class);
                intent.putExtra("username", mUsername);
                startActivity(intent);
            }
        });

        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicPlayerActivity.this, UserProfile.class);
                intent.putExtra("username", mUsername);
                startActivity(intent);
            }
        });

        //set back button
        ImageView back_arrow = findViewById(R.id.music_party_back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //TODO, set party name, just for demo, now from firebase
        //slide up song list
        mSlideUpMusic = findViewById(R.id.music_list_recycler_view);
        mSlideUpMusics = new ArrayList<MusicContainer>();
        populateRecyclerViewData();
        setUpMusicRecyclerView(mSlideUpMusics);
    }


    public void populateRecyclerViewData() {
        // get favorite songs of this user
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new comment has been added, add it to the displayed list
                String song = dataSnapshot.getValue(String.class);
                MusicContainer tmp = new MusicContainer(song);
                mSlideUpMusics.add(tmp);
                mMusicAdapter.notifyItemInserted(mSlideUpMusics.size() - 1);
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
        DatabaseReference songRef = FirebaseDatabase.getInstance().getReference("users").child("nlyu2").child("song");
        songRef.addChildEventListener(childEventListener);
    }


    public void setUpMusicRecyclerView(ArrayList<MusicContainer> mLikedMusics) {
        // set trending recycler view
        mSlideUpMusic.setHasFixedSize(true);
        mMusicLayoutManager = new LinearLayoutManager(this);
        mMusicAdapter = new MusicAdapter(mSlideUpMusics);
        mSlideUpMusic.setLayoutManager(mMusicLayoutManager);
        mSlideUpMusic.setAdapter(mMusicAdapter);
    }

    public void getExtrasFromBundle() {
        Intent intent = getIntent();
        // use intent bundle to set values
        mUsername = intent.getStringExtra("username");
        accessToken = intent.getStringExtra("token");
        song_uri = intent.getStringExtra("songUri"); //get song from the creator
        
        mPartyName = intent.getStringExtra("partyname");
        mGenreName = intent.getStringExtra("genrename");
        mSongName = intent.getStringExtra("songUri");
        mMode = intent.getStringExtra("mode"); //from create party or join party

        mPublic = intent.getBooleanExtra("public", true);
        mLatitude = intent.getDoubleExtra("latitude", 0.0);
        mLongitude = intent.getDoubleExtra("longitude", 0.0);
    }


    //CONNECT TO SPOTIFYplay_or_pause
    @Override
    protected void onStart() {
        super.onStart();

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
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("Spotify: ", throwable.getMessage(), throwable);
                    }
                });
    }

    private void connected(){
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(accessToken);

        //play an example music
        Log.d("Spotify: ", "play music page" + song_uri);
        Player.getInstance().pause();
        UIUtils.setImageDrawable(playOrPause, R.drawable.ic_pause);
        mSpotifyAppRemote.getPlayerApi().play(song_uri);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 2 seconds
                mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                    @Override
                    public void onResult(PlayerState playerState) {
                        trackTitle.setText(playerState.track.name);
                    }
                });
            }
        }, 1000);
    }
    
    public void getComponents(){
        partyHeader = findViewById(R.id.music_party_name);
        mUserRV = findViewById(R.id.music_player_recyclerview_names);

        mNames = new ArrayList<>();
        mNames.add("jimbo");
        mNames.add("slice");
        mNames.add("test");
    }

    public void setTitleHeader() {
        partyHeader.setText(mPartyName);
    }

    public void setUpUserRecyclerView() {
        // set trending recycler view
        mUserRV.setHasFixedSize(true);
        mUserLayoutManager = new LinearLayoutManager(this);
        mUserAdapter = new UserAdapter(mNames);
        mUserRV.setLayoutManager(mUserLayoutManager);
        mUserRV.setAdapter(mUserAdapter);
    }

    public void pushPartyFirebase(PartyContainer pd) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // set audience
        mDatabase.child("parties").child(mPartyName).child("audience").setValue(mUsername);
        mDatabase.child("parties").child(mPartyName).child("latitude").setValue(mLatitude);
        mDatabase.child("parties").child(mPartyName).child("longitude").setValue(mLongitude);
        mDatabase.child("parties").child(mPartyName).child("other_data").setValue("yadayada");
        mDatabase.child("parties").child(mPartyName).child("owner").setValue(mUsername);
        mDatabase.child("parties").child(mPartyName).child("party_name").setValue(pd.getPartyName());
        mDatabase.child("parties").child(mPartyName).child("song").setValue(pd.songToString());
        mDatabase.child("parties").child(mPartyName).child("num_people").setValue(pd.getNumPeople());
    }

//    public void readData(@NonNull LoginActivity.SimpleCallback<String> finishedCallback) {
//        partiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChild(mUsername)) {
//                    finishedCallback.callback((String) dataSnapshot.child(mUsername).child("password").getValue());
//                } else {
//                    finishedCallback.callback(null);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError firebaseError) {
//            }
//        });
//    }
    public void setDatabaseListener() {
        /*
        mDatabase = FirebaseDatabase.getInstance().getReference("parties");
        ValueEventListener myDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                allPartyData = (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();

                if (allPartyData != null) {

                    for (Map.Entry<String, HashMap<String, String>> entry : allPartyData.entrySet()) {
                        //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                        String key = entry.getKey();
                        HashMap<String, String> value = (HashMap<String, String>) entry.getValue();


                    }

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("0", "cancelled");
            }
        };
        // try changing `someRef` here
        mDatabase.addValueEventListener(myDataListener);
        */
    }
}
