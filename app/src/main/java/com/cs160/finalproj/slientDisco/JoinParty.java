package com.cs160.finalproj.slientDisco;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class JoinParty extends AppCompatActivity {

    ImageView mProfileButton;
    ImageView mHelpButton;
    //TODO, ONLY FOR DEMO USE
    ConstraintLayout trendingPanel;
    ConstraintLayout AllPanel;

    private ImageView mBackButton;
    private TextView mBackText;

    private AutoCompleteTextView mCodeView;
    private Button mCodeButton;
    private String mCode;

    private RadioButton mSortByDist;
    private RadioButton mSortByNum;

    private RecyclerView mTrendingRV;
    private RecyclerView mAllRV;

    private PartyAdapter mTrendingAdapter;
    private RecyclerView.LayoutManager mTrendingLayoutManager;

    private PartyAdapter mAllAdapter;
    private RecyclerView.LayoutManager mAllLayoutManager;

    DatabaseReference partiesRef;
    DatabaseReference codesRef;

    private ArrayList<PartyContainer> trendingPartyData;
    private ArrayList<PartyContainer> allPartyData;

    private String song_uri;
    private String mPartyName;

    String mUsername;
    double mLatitude;
    double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_party);

        getComponents();
        getExtrasFromBundle();
        setOnClickListeners();

        FirebaseDatabase databaseRef = FirebaseDatabase.getInstance();
        partiesRef = databaseRef.getReference("parties");
        codesRef = databaseRef.getReference("codes");

        populateRecyclerViewData(partyData -> {
            allPartyData = partyData;

            // adds top 5 parties with most people to trending parties
            trendingPartyData = new ArrayList<>();
            for(int i = 0; i < 5; i++) {
                trendingPartyData.add(allPartyData.get(i));
            }

            // sorts all parties alphabetically for initial display
            Collections.sort(allPartyData, new SortbyPartyName());
            setUpRecyclerView();
        });

        //TODO, ONLY FOR DEMO USE
        trendingPanel = findViewById(R.id.Join_Party_Trending);
        AllPanel = findViewById(R.id.Join_Party_All);

    }

    public void setUpRecyclerView() {
        // set trending recycler view
        mTrendingRV.setHasFixedSize(true);
        mTrendingLayoutManager = new LinearLayoutManager(this);
        mTrendingAdapter = new PartyAdapter(trendingPartyData);
        mTrendingRV.setLayoutManager(mTrendingLayoutManager);
        mTrendingRV.setAdapter(mTrendingAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mTrendingRV.getContext(),
                LinearLayoutManager.VERTICAL);
        mTrendingRV.addItemDecoration(new VerticalSpaceItemDecoration(30));
        mTrendingRV.addItemDecoration(dividerItemDecoration);
        mTrendingAdapter.setOnItemClickedListener(new PartyAdapter.onItemClickedListener() {
            @Override
            public void onItemClick(int position) {

                PartyContainer pc = trendingPartyData.get(position);

                // if greater than 5000m, can't click in
                if (pc.getDistance() > 5000) {
                    Toast.makeText(JoinParty.this, "Too far", Toast.LENGTH_LONG).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(JoinParty.this);
                builder.setCancelable(true);
                builder.setTitle("You are about to join the Silent Disco party");
                builder.setMessage(pc.getPartyName());
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // set party name
                                // set party name
                                mPartyName = pc.getPartyName();
                                Intent myIntent = new Intent(JoinParty.this, MusicPlayerActivity.class);
                                // myIntent.putExtra("key", value); //Optional parameters
                                myIntent.putExtra("username", mUsername);
                                myIntent.putExtra("partyname", mPartyName);
                                myIntent.putExtra("songUri", "spotify:track:4lIxdJw6W3Fg4vUIYCB0S5"); //TODO, just for demo, play tyler swift's style

                                myIntent.putExtra("mode", "join");
                                startActivity(myIntent);

                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();


                //Toast toast=Toast.makeText(getApplicationContext(),pc.getPartyName(),Toast.LENGTH_SHORT);
                //toast.show();
            }
        });

        // set all recycler view
        mAllRV.setHasFixedSize(true);
        mAllLayoutManager = new LinearLayoutManager(this);
        mAllAdapter = new PartyAdapter(allPartyData);
        mAllRV.setLayoutManager(mAllLayoutManager);
        mAllRV.setAdapter(mAllAdapter);
        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(mAllRV.getContext(),
                LinearLayoutManager.VERTICAL);
        mAllRV.addItemDecoration(new VerticalSpaceItemDecoration(30));
        mAllRV.addItemDecoration(dividerItemDecoration2);

        mAllAdapter.setOnItemClickedListener(new PartyAdapter.onItemClickedListener() {
            @Override
            public void onItemClick(int position) {
                PartyContainer pc = allPartyData.get(position);


                // if greater than 5000m, can't click in
                if (pc.getDistance() > 5000) {
                    Toast.makeText(JoinParty.this, "Party over 5km away", Toast.LENGTH_LONG).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(JoinParty.this);
                builder.setCancelable(true);
                builder.setTitle("You are about to join the Silent Disco party");
                builder.setMessage(pc.getPartyName());
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // set party name
                                Toast toast=Toast.makeText(getApplicationContext(),pc.getPartyName(),Toast.LENGTH_SHORT);
                                toast.show();
                                mPartyName = pc.getPartyName();
                                Intent myIntent = new Intent(JoinParty.this, MusicPlayerActivity.class);
                                // myIntent.putExtra("key", value); //Optional parameters
                                myIntent.putExtra("username", mUsername);
                                myIntent.putExtra("partyname", mPartyName);
                                myIntent.putExtra("songUri", "spotify:track:4lIxdJw6W3Fg4vUIYCB0S5"); //TODO, just for demo, play tyler swift's style

                                //it is join party mode, firebase read only
                                myIntent.putExtra("mode", "join");
                                startActivity(myIntent);

                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

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

        // get access code
        mCodeView = findViewById(R.id.join_party_enter_code);
        mCodeButton = findViewById(R.id.join_party_code_button);
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

        mSortByDist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(allPartyData, new SortbyDistance());
                mAllAdapter.notifyDataSetChanged();
            }
        });

        mSortByNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(allPartyData, new SortbyNumPeople());
                mAllAdapter.notifyDataSetChanged();
            }
        });

        mCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCode = mCodeView.getText().toString();

                searchAccessCode(partyName -> {
                    if (partyName.equals("")) {
                        Toast.makeText(JoinParty.this, "Invalid code", Toast.LENGTH_LONG).show();
                    } else {
                        Intent myIntent = new Intent(JoinParty.this, MusicPlayerActivity.class);
                        // myIntent.putExtra("key", value); //Optional parameters
                        myIntent.putExtra("username", mUsername);
                        myIntent.putExtra("partyname", partyName);
                        myIntent.putExtra("mode", "join");
                        startActivity(myIntent);

                    }
                });

            }
        });    }

    public void getExtrasFromBundle() {
        Intent intent = getIntent();
        // use intent bundle to set values
        // String value = intent.getStringExtra("key");
        mUsername = intent.getStringExtra("username");
        mLatitude = intent.getDoubleExtra("latitude", 0.0);
        mLongitude = intent.getDoubleExtra("longitude", 0.0);
    }

    public int partyDistance(double partyLat, double partyLon) {
        if (mLatitude == 0 || mLongitude == 0) {
            return 99999;
        }

        float[] results = new float[3];
        Location.distanceBetween(mLatitude, mLongitude, partyLat, partyLon, results);

        return (int) results[0];
    }

    public void populateRecyclerViewData(@NonNull LoginActivity.SimpleCallback<ArrayList<PartyContainer>> finishedCallback) {
        partiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<PartyContainer> allPartyData = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // only show public parties
                            if (!snapshot.hasChild("code")) {
                                String partyName = (String) snapshot.child("party_name").getValue();
                                int numPeople = 0;
                                try {
                                    numPeople = ((Number) snapshot.child("num_people").getValue()).intValue();
                                } catch (ClassCastException e) {
                                    System.out.println(e);
                                }
                                String genre = (String) snapshot.child("genre").getValue();
                                String songName = (String) snapshot.child("song").getValue();
                                double latitude = ((Number) snapshot.child("latitude").getValue()).doubleValue();
                                double longitude = ((Number) snapshot.child("longitude").getValue()).doubleValue();
                                int distance = partyDistance(latitude, longitude);

                                PartyContainer pc = new PartyContainer(partyName, numPeople, genre, songName);
                                pc.setDistance(distance);

                                if (distance < 5000) {
                                    allPartyData.add(pc);
                                }
                            }
                        }

                        Collections.sort(allPartyData, new SortbyNumPeople());

                        finishedCallback.callback(allPartyData);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void searchAccessCode(@NonNull LoginActivity.SimpleCallback<String> finishedCallback) {
        codesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mCode)) {
                    finishedCallback.callback((String) dataSnapshot.child(mCode).getValue());
                } else {
                    finishedCallback.callback("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}

