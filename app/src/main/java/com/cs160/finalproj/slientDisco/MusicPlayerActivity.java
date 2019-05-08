package com.cs160.finalproj.slientDisco;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.service.carrier.CarrierMessagingService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    @BindView(R.id.music_party_back_arrow) protected ImageView backArrow;


    @BindView(R.id.SlideUpPanel) protected View slideUpPanel;

    @BindView(R.id.track_title) protected TextView trackTitle;
    @BindView(R.id.track_progress) protected TextView trackProgress;
    @BindView(R.id.track_duration) protected TextView trackDuration;

    @BindView(R.id.track_timeline) protected SeekBar trackTimeline;


    @BindView(R.id.music_list_recycler_view) protected RecyclerView mSlideUpMusic;
    ArrayList<MusicContainer> mSlideUpMusicList;
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
    private SpotifyAppRemote mSpotifyAppRemote;
    private static final String CLIENT_ID = "8933a96ee220485997e12f9af761f6e9";
    private static final String REDIRECT_URI = "http://com.example.spotify/callback";

    private RecyclerView mUserRV;
    private RecyclerView.LayoutManager mUserLayoutManager;
    private UserAdapter mUserAdapter;
    private ArrayList<String> mAudience;
    private String mPartyName;
    private TextView partyHeader;
    private String mCode;
    private TextView mCodeView;

    private String mGenreName;
    private String mMode;  //create party or join party
    private double mLatitude;
    private double mLongitude;
    private int mNumPeople;

    private DatabaseReference partyRef;
    private DatabaseReference codesRef;

    String mUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        ButterKnife.bind(this);
        getComponents();
        getExtrasFromBundle();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        partyRef = database.getReference("parties").child(mPartyName);
        codesRef = database.getReference("codes");

        setUpUserRecyclerView();
        setListeners();

        //Set the Name of the party text
        setTitleHeader();

        if (mMode.equals("create")) {
            //Push the data from create party to firebase
            pushPartyFirebase();
        }

        // add user to party audience
        partyRef.child("audience").child(mUsername).setValue(true);
        getPartyFirebase(pc -> {
            resetPartyData(pc);
            mUserAdapter.update(mAudience);

            if (mCode != null) {
                setCode();
            }

            //slide up song list
            mSlideUpMusicList = new ArrayList<MusicContainer>();
            populateRecyclerViewData();
            setUpMusicRecyclerView(mSlideUpMusicList);

        });
    }

    public void getExtrasFromBundle() {
        Intent intent = getIntent();
        // use intent bundle to set values
        mUsername = intent.getStringExtra("username");
        accessToken = intent.getStringExtra("token");
        song_uri = intent.getStringExtra("songUri"); //get song from the creator

        mPartyName = intent.getStringExtra("partyname");
        mGenreName = intent.getStringExtra("genrename");
        mMode = intent.getStringExtra("mode"); //from create party or join party

        mCode = intent.getStringExtra("code");
        mLatitude = intent.getDoubleExtra("latitude", 0.0);
        mLongitude = intent.getDoubleExtra("longitude", 0.0);
    }

    public void setListeners() {
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


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // removes user from firebase party data
                partyRef.child("audience").child(mUsername).removeValue();
                mNumPeople = Math.max(0, mNumPeople - 1);
                partyRef.child("num_people").setValue(mNumPeople);

                Intent intent = new Intent(MusicPlayerActivity.this, ChooseParty.class);
                intent.putExtra("username", mUsername);
                startActivity(intent);
            }
        });

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
    }

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



    public void populateRecyclerViewData() {
        // get favorite songs of this user
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new comment has been added, add it to the displayed list
                String song = dataSnapshot.getValue(String.class);
                MusicContainer tmp = new MusicContainer(song);
                mSlideUpMusicList.add(tmp);
                mMusicAdapter.notifyItemInserted(mSlideUpMusicList.size() - 1);
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
        mMusicAdapter = new MusicAdapter(mSlideUpMusicList);
        mSlideUpMusic.setLayoutManager(mMusicLayoutManager);
        mSlideUpMusic.setAdapter(mMusicAdapter);
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
        mCodeView = findViewById(R.id.music_party_code);
    }

    public void setTitleHeader() {
        partyHeader.setText(mPartyName);
    }

    public void setCode() {
        mCodeView.setText("Code: " + mCode);
    }

    public void setUpUserRecyclerView() {
        // set trending recycler view
        mUserRV.setHasFixedSize(true);
        mUserLayoutManager = new LinearLayoutManager(this);
        mUserRV.setLayoutManager(mUserLayoutManager);

        mAudience = new ArrayList<>();
        mUserAdapter = new UserAdapter(mAudience);
        mUserRV.setAdapter(mUserAdapter);
    }

    public void pushPartyFirebase() {
        partyRef.child("audience").child(mUsername).setValue(true);
        partyRef.child("genre").setValue(mGenreName);
        partyRef.child("latitude").setValue(mLatitude);
        partyRef.child("longitude").setValue(mLongitude);
        partyRef.child("num_people").setValue(0);
        partyRef.child("owner").setValue(mUsername);
        partyRef.child("party_name").setValue(mPartyName);
        partyRef.child("song").setValue(song_uri);

        if (mCode != null) {
            partyRef.child("code").setValue(mCode);
            codesRef.child(mCode).setValue(mPartyName);
        }
    }


    public void getPartyFirebase(@NonNull LoginActivity.SimpleCallback<PartyContainer> finishedCallback) {
        partyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String genre = (String) dataSnapshot.child("genre").getValue();
                String songUri = (String) dataSnapshot.child("song").getValue();

                // TODO: clean when database parties are standardized
                int numPeople = 1;
                try {
                    numPeople = ((Number) dataSnapshot.child("num_people").getValue()).intValue();
                } catch (ClassCastException e) {
                    System.out.println(e);
                }

                // adds current user to number of users
                numPeople++;
                partyRef.child("num_people").setValue(numPeople);

                ArrayList<String> newAudience = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.child("audience").getChildren()) {
                    newAudience.add(snapshot.getKey());
                }

                PartyContainer pc = new PartyContainer(mPartyName, numPeople, genre, songUri);
                pc.setAudience(newAudience);


                if (dataSnapshot.hasChild("code")) {
                    String code = (String) dataSnapshot.child("code").getValue();
                    pc.setCode(code);
                }

                finishedCallback.callback(pc);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void resetPartyData(PartyContainer pc) {
        mAudience = pc.getAudience();
        mGenreName = pc.getGenre();
        mNumPeople = pc.getNumPeople();
        song_uri = pc.getSongUri();
        mCode = pc.getCode();
    }
}
