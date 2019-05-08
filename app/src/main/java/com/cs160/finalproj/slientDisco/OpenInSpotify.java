package com.cs160.finalproj.slientDisco;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cs160.finalproj.slientDisco.player.Player;
import com.cs160.finalproj.slientDisco.support.utils.UIUtils;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.PlayerState;

import kaaes.spotify.webapi.android.SpotifyApi;

public class OpenInSpotify extends AppCompatActivity {

    protected Button cancelButton;

    String mUsername;
    private String songUri;
    private String accessToken;
    private String party_name;
    private SpotifyAppRemote mSpotifyAppRemote;
    private static final String CLIENT_ID = "8933a96ee220485997e12f9af761f6e9";
    private static final String REDIRECT_URI = "http://com.example.spotify/callback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_in_spotify);
        getExtrasFromBundle();

        //Cancel going to Spotify
        cancelButton = findViewById(R.id.open_spotify_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //TODO, envoke spotify API, wait 2 second
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 2 seconds
                startActivity(getPackageManager().getLaunchIntentForPackage("com.spotify.music"));
            }
        }, 2000);
    }

    public void getExtrasFromBundle() {
        Intent intent = getIntent();
        // use intent bundle to set values
        // String value = intent.getStringExtra("key");
        mUsername = intent.getStringExtra("username");
        songUri = intent.getStringExtra("songUri");
    }

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
        mSpotifyAppRemote.getPlayerApi().play(songUri);
    }
}
