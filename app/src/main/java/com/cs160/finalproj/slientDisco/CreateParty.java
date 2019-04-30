package com.cs160.finalproj.slientDisco;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CreateParty extends AppCompatActivity {
    ImageView mProfileButton;
    ImageView mHelpButton;
    Button createPartyButton;

    private ImageView mBackButton;
    private TextView mBackText;

    private ToggleButton mPrivateButton;
    private ToggleButton mPublicButton;


    String mUsername;
    double latitude;
    double longitude;

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
    }

    public void getExtrasFromBundle() {
        Intent intent = getIntent();
        // use intent bundle to set values
        // String value = intent.getStringExtra("key");
        mUsername = intent.getStringExtra("username");
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude", 0.0);
    }
}
