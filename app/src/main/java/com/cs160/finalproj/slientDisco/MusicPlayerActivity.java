package com.cs160.finalproj.slientDisco;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
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
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.List;

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

    private List<Track> tracks;

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

    String mUsername;


    private void setPositionListener() {
        isTimeListenerSet = true;
        runOnUiThread(() -> {
            if (oldPosition != position) {
                oldPosition = position;
                trackTimeline.setMax(PlayerUtils.toSeconds(Player.getInstance().getTrackEndTime()));
            }

            trackTimeline.setProgress(PlayerUtils.toSeconds(Player.getInstance().getTrackTimePosition()));
            trackProgress.setText(PlayerUtils.toMinutes(Player.getInstance()
                    .getTrackTimePosition()));

            if (Player.getInstance().endPlaying()) {
                next();
            }

            timeHandler.postDelayed(this::setPositionListener, Constants.TIME.TRACK_PROGRESS_DELAY_IN_MILLIS);
        });
    }

    private void previous() {
        if (position > 0) {
            Player.getInstance().stop();
            --position;
            UIUtils.colorizeImage(nextTrack, true);
            Player.getInstance().play(tracks.get(position));
            UIUtils.setImageDrawable(playOrPause, R.drawable.ic_pause);
            UIUtils.setBitmapCover(trackCover, Player.getInstance().getCover(tracks.get(position), this));
            trackTitle.setText(tracks.get(position).getTitle());
            trackDuration.setText(PlayerUtils.toMinutes(Player.getInstance().getTrackEndTime()));
        }
        if (position == 0) {
            UIUtils.colorizeImage(previousTrack, false);
        }
    }

    private void playOrPause() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            AppUtils.checkReadStoragePermission(this);
        } else {
            if (tracks.isEmpty()) {
                List<Track> trackList = Player.getInstance().checkDirectory();
                if (!trackList.isEmpty()) {
                    tracks.addAll(trackList);
                } else {
                    Toast.makeText(this, R.string.music_dorectory_error, Toast.LENGTH_LONG).show();
                    return;
                }
            }
            if (Player.getInstance().isPausing()) {
                if (trackTimePosition > 0) {
                    Player.getInstance().resume();
                } else {
                    Player.getInstance().play(tracks.get(position));
                }
                if (!isTimeListenerSet) {
                    setPositionListener();
                    UIUtils.setBitmapCover(trackCover, Player.getInstance().getCover(tracks.get(position), this));
                    trackTitle.setText(tracks.get(position).getTitle());
                    trackTimeline.setMax(PlayerUtils.toSeconds(Player.getInstance().getTrackEndTime()));
                }
                UIUtils.setImageDrawable(playOrPause, R.drawable.ic_pause);
                if (tracks.size() > 1) {
                    if (trackProgress.getVisibility() == View.INVISIBLE) {
                        trackProgress.setVisibility(View.VISIBLE);
                        UIUtils.colorizeImage(nextTrack, true);
                    }
                }
                trackDuration.setText(PlayerUtils.toMinutes(Player.getInstance().getTrackEndTime()));
            } else {
                trackTimePosition = Player.getInstance().getTrackTimePosition();
                Player.getInstance().pause();
                UIUtils.setImageDrawable(playOrPause, R.drawable.ic_play);
            }
        }
    }

    private void next() {
        if (position < tracks.size() - 1) {
            Player.getInstance().stop();
            ++position;
            UIUtils.colorizeImage(previousTrack, true);
            Player.getInstance().play(tracks.get(position));
            UIUtils.setImageDrawable(playOrPause, R.drawable.ic_pause);
            UIUtils.setBitmapCover(trackCover, Player.getInstance().getCover(tracks.get(position), this));
            trackTitle.setText(tracks.get(position).getTitle());
            trackDuration.setText(PlayerUtils.toMinutes(Player.getInstance().getTrackEndTime()));
        }
        if (position == tracks.size() - 1) {
            UIUtils.colorizeImage(nextTrack, false);
        }
    }

    @OnClick(R.id.previous_track)
    public void onPreviousTrackClick() {
        previous();
    }

    @OnClick(R.id.play_or_pause)
    public void onPlayPauseClick() {
        playOrPause();
    }

    @OnClick(R.id.next_track)
    public void onNextTrackClick() {
        next();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        ButterKnife.bind(this);
        getExtrasFromBundle();

        tracks = new ArrayList<>();

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

        UIUtils.colorizeImage(previousTrack, false);
        UIUtils.colorizeImage(nextTrack, false);

        //set back button
        ImageView back_arrow = findViewById(R.id.music_party_back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //TODO, set party name, just for demo
        TextView mpartyName = findViewById(R.id.music_party_name);
        mpartyName.setText(party_name + "'s Party");
    }

    public void getExtrasFromBundle() {
        Intent intent = getIntent();
        // use intent bundle to set values
        // String value = intent.getStringExtra("key");
        mUsername = intent.getStringExtra("username");
        accessToken = intent.getStringExtra("token");
        party_name = intent.getStringExtra("partyName"); //just for demo use
        song_uri = intent.getStringExtra("songUri"); //get song from the creator
    }

    //CONNECT TO SPOTIFY
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

    private void connected() {
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(accessToken);

        //play an example music
        Log.d("Spotify: ", "play music page" + song_uri);
        mSpotifyAppRemote.getPlayerApi().play(song_uri);
    }
}
