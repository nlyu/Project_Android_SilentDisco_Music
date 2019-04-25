package com.cs160.finalproj.slientDisco;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ChooseParty extends AppCompatActivity {

    /**
     * Instance Variables
     */
    ImageView mProfileButton;
    ImageView mHelpButton;
    TextView mJoinPartyButton;
    TextView mCreatePartyButton;


    // todo : init any instance vars that come from previous activity @lyu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_party);

        // init instance vars from bundle
        getExtrasFromBundle();

        // get UI components
        getComponents();

        // set the onclick listeners
        setOnClickListeners();

    }

    public void getExtrasFromBundle() {
        Intent intent = getIntent();
        // use intent bundle to set values
        // String value = intent.getStringExtra("key");
    }

    public void setOnClickListeners() {
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to profile i.e.
                Intent myIntent = new Intent(ChooseParty.this, UserProfile.class);
                // myIntent.putExtra("key", value); //Optional parameters
                startActivity(myIntent);
            }
        });

        mHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to help screen i.e.
                Intent myIntent = new Intent(ChooseParty.this, HelpActivity.class);
                // myIntent.putExtra("key", value); //Optional parameters
                startActivity(myIntent);
            }
        });

        mJoinPartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to join party screen i.e.
                Intent myIntent = new Intent(ChooseParty.this, JoinParty.class);
                // myIntent.putExtra("key", value); //Optional parameters
                startActivity(myIntent);
            }
        });

        mCreatePartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to create party screen i.e.
                Intent myIntent = new Intent(ChooseParty.this, CreateParty.class);
                // myIntent.putExtra("key", value); //Optional parameters
                startActivity(myIntent);
            }
        });

    }

    public void getComponents() {
        mProfileButton = findViewById(R.id.choose_party_account_icon);
        mHelpButton = findViewById(R.id.choose_party_help_icon);
        mJoinPartyButton = findViewById(R.id.choose_party_join_party);
        mCreatePartyButton = findViewById(R.id.choose_party_create_party);
    }
}

