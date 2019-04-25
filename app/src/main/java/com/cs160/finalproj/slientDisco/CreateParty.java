package com.cs160.finalproj.slientDisco;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CreateParty extends AppCompatActivity {
    ImageView mProfileButton;
    ImageView mHelpButton;
    Button createPartyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_party);

        getComponents();

        createPartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CreateParty.this, MusicPlayerActivity.class);
                // myIntent.putExtra("key", value); //Optional parameters
                startActivity(myIntent);
            }
        });
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CreateParty.this, UserProfile.class);
                // myIntent.putExtra("key", value); //Optional parameters
                startActivity(myIntent);
            }
        });

        mHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CreateParty.this, HelpActivity.class);
                // myIntent.putExtra("key", value); //Optional parameters
                startActivity(myIntent);
            }
        });

    }

    public void getComponents() {
        mProfileButton = findViewById(R.id.create_party_account_icon);
        mHelpButton = findViewById(R.id.create_party_help_icon);
        createPartyButton = findViewById(R.id.create_party_create_button);
    }
}
