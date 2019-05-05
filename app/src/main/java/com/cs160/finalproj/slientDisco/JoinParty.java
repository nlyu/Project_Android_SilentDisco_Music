package com.cs160.finalproj.slientDisco;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;

public class JoinParty extends AppCompatActivity {

    ImageView mProfileButton;
    ImageView mHelpButton;
    //TODO, ONLY FOR DEMO USE
    ConstraintLayout trendingPanel;
    ConstraintLayout AllPanel;

    private ImageView mBackButton;
    private TextView mBackText;

    private ToggleButton mSortByDist;
    private ToggleButton mSortByNum;

    private RecyclerView mTrendingRV;
    private RecyclerView mAllRV;

    private PartyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private PartyAdapter mAdapter2;
    private RecyclerView.LayoutManager mLayoutManager2;

    // trending party data TODO: replace with real firebase data
    private ArrayList<PartyContainer> trendingPartyData;
    private ArrayList<PartyContainer> allPartyData;


    String mUsername;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_party);

        getComponents();
        getExtrasFromBundle();
        setOnClickListeners();

        trendingPartyData = new ArrayList<PartyContainer>();
        allPartyData = new ArrayList<PartyContainer>();
        populateRecyclerViewData();
        setUpRecyclerView(trendingPartyData);

        //TODO, ONLY FOR DEMO USE
        trendingPanel = findViewById(R.id.Join_Party_Trending);
        AllPanel = findViewById(R.id.Join_Party_All);

        //TODO, ONLY FOR DEMO USE, show all the trending party
        trendingPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(JoinParty.this, MusicPlayerActivity.class);
                // myIntent.putExtra("key", value); //Optional parameters
                myIntent.putExtra("username", mUsername);
                startActivity(myIntent);
            }
        });

        //TODO, ONLY FOR DEMO USE, show all the party
        AllPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(JoinParty.this, MusicPlayerActivity.class);
                // myIntent.putExtra("key", value); //Optional parameters
                myIntent.putExtra("username", mUsername);
                startActivity(myIntent);
            }
        });




        String location = latitude + ", " + longitude;
        Toast.makeText(JoinParty.this, location, Toast.LENGTH_LONG).show();
    }

    public void populateRecyclerViewData() {
        // mTrendingRV
        // populate dummy view
        // partyName, numPeople, genre
        PartyContainer e1 = new PartyContainer("It's litty again",
                64, "hip hop", "cola");
        PartyContainer e2 = new PartyContainer("Popular kids",
                32, "rock", "cola");
        PartyContainer e3 = new PartyContainer("Bob's party",
                12, "pop", "cola");
        PartyContainer e4 = new PartyContainer("Basic taste club",
                12, "pop", "cola");
        PartyContainer e5 = new PartyContainer("Deep and dank",
                4, "house", "cola");
        trendingPartyData.add(e1);
        trendingPartyData.add(e2);
        trendingPartyData.add(e3);
        trendingPartyData.add(e4);
        trendingPartyData.add(e5);

        PartyContainer e6 = new PartyContainer("Babab",
                1, "hip hop", "cola");
        PartyContainer e7 = new PartyContainer("Amortized",
                2, "rock", "cola");
        PartyContainer e8 = new PartyContainer("party123",
                4, "classical", "cola");
        PartyContainer e9 = new PartyContainer("tyygaa",
                2, "classical", "cola");
        PartyContainer e10 = new PartyContainer("i heart radio",
                4, "pop", "cola");
        allPartyData.add(e6);
        allPartyData.add(e7);
        allPartyData.add(e8);
        allPartyData.add(e9);
        allPartyData.add(e10);
        allPartyData.add(e1);
        allPartyData.add(e2);
        allPartyData.add(e3);
        allPartyData.add(e4);
        allPartyData.add(e5);

    }

    public void setUpRecyclerView(ArrayList<PartyContainer> trendingPartyData) {
        // set trending recycler view
        mTrendingRV.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new PartyAdapter(trendingPartyData);
        mTrendingRV.setLayoutManager(mLayoutManager);
        mTrendingRV.setAdapter(mAdapter);

        mAdapter.setOnItemClickedListener(new PartyAdapter.onItemClickedListener() {
            @Override
            public void onItemClick(int position) {
                PartyContainer pc = trendingPartyData.get(position);
                // go to the specified party
                //Intent intent = new Intent(BearFeedActivity.this, CommentFeedActivity.class);
                //intent.putExtra("username", username);
                //startActivity(intent);
                Toast toast=Toast.makeText(getApplicationContext(),pc.getPartyName(),Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        // set all recycler view
        mAllRV.setHasFixedSize(true);
        mLayoutManager2 = new LinearLayoutManager(this);
        mAdapter2 = new PartyAdapter(allPartyData);
        mAllRV.setLayoutManager(mLayoutManager2);
        mAllRV.setAdapter(mAdapter2);

        mAdapter2.setOnItemClickedListener(new PartyAdapter.onItemClickedListener() {
            @Override
            public void onItemClick(int position) {
                PartyContainer pc = allPartyData.get(position);
                // go to the specified party
                //Intent intent = new Intent(BearFeedActivity.this, CommentFeedActivity.class);
                //intent.putExtra("username", username);
                //startActivity(intent);
                Toast toast=Toast.makeText(getApplicationContext(),pc.getPartyName(),Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    public void getComponents() {

        mProfileButton = findViewById(R.id.join_party_account_icon);
        mHelpButton = findViewById(R.id.join_party_help_icon);

        //createPartyButton = findViewById(R.id.join_party_create_button);
        mBackButton = findViewById(R.id.join_party_back_arrow);
        mBackText = findViewById(R.id.join_party_back_text);

        // get toggle buttons
        mSortByDist = findViewById(R.id.sort_by_distance);
        mSortByNum = findViewById(R.id.sort_by_people);

        // get recyclerviews
        mTrendingRV = findViewById(R.id.join_party_trending_recycler_vew);
        mAllRV = findViewById(R.id.join_party_all_recycler_vew);

    }

    // TODO: move above onClickListeners here
    public void setOnClickListeners() {

        //User profile
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(JoinParty.this, UserProfile.class);
                // myIntent.putExtra("key", value); //Optional parameters
                myIntent.putExtra("username", mUsername);
                startActivity(myIntent);
            }
        });

        //Help page
        mHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(JoinParty.this, HelpActivity.class);
                // myIntent.putExtra("key", value); //Optional parameters
                myIntent.putExtra("username", mUsername);
                startActivity(myIntent);
            }
        });


        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go back here
                Intent myIntent = new Intent(JoinParty.this, ChooseParty.class);
                // myIntent.putExtra("key", value); //Optional parameters
                myIntent.putExtra("username", mUsername);
                startActivity(myIntent);
            }
        });
        mBackText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go back here
                Intent myIntent = new Intent(JoinParty.this, ChooseParty.class);
                // myIntent.putExtra("key", value); //Optional parameters
                myIntent.putExtra("username", mUsername);
                startActivity(myIntent);
            }
        });
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