package com.cs160.finalproj.slientDisco;

import android.app.Activity;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Trace;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;

public class CreateParty extends AppCompatActivity {
    ImageView mProfileButton;
    ImageView mHelpButton;
    Button createPartyButton;

    private ImageView mBackButton;
    private TextView mBackText;

    private ToggleButton mPrivateButton;
    private ToggleButton mPublicButton;
    private final String[] song_name_list = new String[] {
            "", "", "", "", "", "", "", "", "", ""
    }; //only show 10 result

    private final String[] song_name_uri_list = new String[] {
            "", "", "", "", "", "", "", "", "", ""
    }; //only show 10 result
    private static final String[] COUNTRIES = new String[] {
            "Belgium", "France", "Italy", "Germany", "Spain"
    };

    private AutoCompleteTextView mCreate_party_name_text;
    private AutoCompleteTextView mCreate_party_song_text;
    private AutoCompleteTextView mCreate_party_genres_text;

    String mUsername;
    double latitude;
    double longitude;

    SpotifyApi api;
    SpotifyService spotify;
    private String song_uri;
    private String accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_party);

        getExtrasFromBundle();
        getComponents();
        setOnClickListeners();

        createPartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CreateParty.this, MusicPlayerActivity.class);
                // myIntent.putExtra("key", value); //Optional parameters
                myIntent.putExtra("username", mUsername);
                myIntent.putExtra("token", accessToken);
                myIntent.putExtra("songUri", song_uri);
                myIntent.putExtra("partyName", mUsername); //TODO, just for demo
                startActivity(myIntent);
            }
        });
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CreateParty.this, UserProfile.class);
                // myIntent.putExtra("key", value); //Optional parameters
                myIntent.putExtra("username", mUsername);
                startActivity(myIntent);
            }
        });

        mHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CreateParty.this, HelpActivity.class);
                // myIntent.putExtra("key", value); //Optional parameters
                myIntent.putExtra("username", mUsername);
                startActivity(myIntent);
            }
        });


        String location = latitude + ", " + longitude;
        Toast.makeText(CreateParty.this, location, Toast.LENGTH_LONG).show();

        //Spotify api
        api = new SpotifyApi();
        api.setAccessToken(accessToken);
        spotify = api.getService();
        //search song list
        mCreate_party_name_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                spotify.searchTracks(s.toString(), new SpotifyCallback<TracksPager>() {
                    @Override
                    public void success(TracksPager tracksPager, Response response) {
                        List<Track> items = tracksPager.tracks.items;
                        for (int i = 0; i < items.size() && i < 10; i++){
                            song_name_list[i] = items.get(i).name;
                            song_name_uri_list[i] = items.get(i).uri;
                            Log.d("Spotify: ", items.get(i).name);
                        }

                        //set style and text of the complete list
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, song_name_list);
                        mCreate_party_name_text.setAdapter(adapter);
                    }

                    @Override
                    public void failure(SpotifyError error) {
                        Log.d("Spotify: ", "search failure");
                    }
                });
            }
        });


        mCreate_party_name_text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),"song" + song_name_uri_list[position] + " selected", Toast.LENGTH_LONG).show();
                song_uri = song_name_uri_list[position];
            }
        });
    }

    public void setOnClickListeners() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go back here
                Intent myIntent = new Intent(CreateParty.this, ChooseParty.class);
                // myIntent.putExtra("key", value); //Optional parameters
                myIntent.putExtra("username", mUsername);
                startActivity(myIntent);
            }
        });
        mBackText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go back here
                Intent myIntent = new Intent(CreateParty.this, ChooseParty.class);
                // myIntent.putExtra("key", value); //Optional parameters
                myIntent.putExtra("username", mUsername);
                startActivity(myIntent);
            }
        });

        // TODO: make these radio buttons - mPrivateButton, mPublicButton

    }



    public void getComponents() {
        // get profile, help, create party buttons
        mProfileButton = findViewById(R.id.create_party_account_icon);
        mHelpButton = findViewById(R.id.create_party_help_icon);
        createPartyButton = findViewById(R.id.create_party_create_button);

        //get back buttons
        mBackButton = findViewById(R.id.create_party_back_arrow);
        mBackText = findViewById(R.id.create_party_back_text);

        // get public / private buttons
        mPrivateButton = findViewById(R.id.create_party_private_button);
        mPublicButton  = findViewById(R.id.create_party_public_button);

        mCreate_party_name_text = findViewById(R.id.create_party_create_party_name);
        mCreate_party_song_text = findViewById(R.id.create_party_find_songs);
        mCreate_party_genres_text = findViewById(R.id.create_party_find_genres);
    }


    public void getExtrasFromBundle() {
        Intent intent = getIntent();
        // use intent bundle to set values
        // String value = intent.getStringExtra("key");
        mUsername = intent.getStringExtra("username");
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude", 0.0);
        accessToken = intent.getStringExtra("token");
    }
}
