package com.cs160.finalproj.slientDisco;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class JoinParty extends AppCompatActivity {

    ImageView mProfileButton;
    ImageView mHelpButton;
    //TODO, ONLY FOR DEMO USE
    ConstraintLayout trendingPanel;
    ConstraintLayout AllPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_party);

        getComponents();

        //TODO, ONLY FOR DEMO USE
        trendingPanel = findViewById(R.id.Join_Party_Trending);
        AllPanel = findViewById(R.id.Join_Party_All);

        //TODO, ONLY FOR DEMO USE, show all the trending party
        trendingPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(JoinParty.this, MusicPlayerActivity.class);
                // myIntent.putExtra("key", value); //Optional parameters
                startActivity(myIntent);
            }
        });

        //TODO, ONLY FOR DEMO USE, show all the party
        AllPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(JoinParty.this, MusicPlayerActivity.class);
                // myIntent.putExtra("key", value); //Optional parameters
                startActivity(myIntent);
            }
        });

        //User profile
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(JoinParty.this, UserProfile.class);
                // myIntent.putExtra("key", value); //Optional parameters
                startActivity(myIntent);
            }
        });

        //Help page
        mHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(JoinParty.this, HelpActivity.class);
                // myIntent.putExtra("key", value); //Optional parameters
                startActivity(myIntent);
            }
        });
    }

    public void getComponents() {
        mProfileButton = findViewById(R.id.join_party_account_icon);
        mHelpButton = findViewById(R.id.join_party_help_icon);
        //createPartyButton = findViewById(R.id.join_party_create_button);
    }
}