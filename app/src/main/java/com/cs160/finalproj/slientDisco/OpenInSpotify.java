package com.cs160.finalproj.slientDisco;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OpenInSpotify extends AppCompatActivity {

    protected Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_in_spotify);

        //Cancel going to Spotify
        cancelButton = findViewById(R.id.open_spotify_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //TODO, envoke spotify API
    }
}
