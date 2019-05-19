package com.joiner.groupjoiner.Activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.joiner.groupjoiner.Adapters.GroupsViewHolder;
import com.joiner.groupjoiner.Models.GroupsModel;
import com.joiner.groupjoiner.R;


public class GroupClickActivity extends AppCompatActivity {

    private static final String TAG = "GroupClickActivity";
    private DatabaseReference GroupsRef;
    private TextView tv;
    private Toolbar mtoolbar;
    private String ref;
    private RecyclerView groups_recyclerview;

    FirebaseRecyclerOptions<GroupsModel> options;
    FirebaseRecyclerAdapter<GroupsModel, GroupsViewHolder> adapter;
    private AdView adView;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_click);

        getIncomingIntent();

        initToolbar();
        initRecyclerview();
        loadBannerAds();
        loadInterStitialAds();

    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("ref")) {
            ref = getIntent().getStringExtra("ref");
            GroupsRef = FirebaseDatabase.getInstance().getReference().child("Groups").child("Category").child(ref);
            GroupsRef.keepSynced(true);
        }
    }

    private void initToolbar() {
        mtoolbar = findViewById(R.id.group_click_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(ref);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        tv = findViewById(R.id.tv);
        tv.setText(ref);
    }

    private void initRecyclerview() {

        groups_recyclerview = findViewById(R.id.groups_recyclerview);
        groups_recyclerview.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        groups_recyclerview.setLayoutManager(layoutManager);

        options = new FirebaseRecyclerOptions.Builder<GroupsModel>()
                .setQuery(GroupsRef, GroupsModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<GroupsModel, GroupsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final GroupsViewHolder holder, final int position, @NonNull final GroupsModel model) {

                final String groupRef = getRef(position).getKey();
                Log.d(TAG, "onBindViewHolder: key" + groupRef);
                holder.setTitle(model.getTitle());
                Log.d(TAG, "onBindViewHolder: title" + model.getTitle());
                holder.setLink(model.getLink());
                Log.d(TAG, "onBindViewHolder: link" + model.getLink());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (position % 2 == 0) {
                            if (interstitialAd.isLoaded()) {

                                String title = model.getTitle();
                                String link = model.getLink();
                                Intent intent = new Intent(GroupClickActivity.this, JoinGroupActivity.class);
                                intent.putExtra("title", title);
                                intent.putExtra("link", link);
                                intent.putExtra("ref", ref);
                                startActivity(intent);
                                showInterstitial();

                            } else {

                                String title = model.getTitle();
                                String link = model.getLink();
                                Intent intent = new Intent(GroupClickActivity.this, JoinGroupActivity.class);
                                intent.putExtra("title", title);
                                intent.putExtra("link", link);
                                intent.putExtra("ref", ref);
                                startActivity(intent);
                                // startActivity(new Intent(GroupClickActivity.this, JoinGroupActivity.class));
                            }

                        } else {
                            String title = model.getTitle();
                            String link = model.getLink();
                            Intent intent = new Intent(GroupClickActivity.this, JoinGroupActivity.class);
                            intent.putExtra("title", title);
                            intent.putExtra("link", link);
                            intent.putExtra("ref", ref);
                            startActivity(intent);
                        }

                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        CharSequence option[] = new CharSequence[]{"Remove This Group", "Add To Favorite"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(GroupClickActivity.this);
                        builder.setTitle("Select Options");
                        builder.setIcon(R.drawable.menu_icon);
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {

                                if (position == 0) {
                                    Toast.makeText(GroupClickActivity.this, "Removing feature will be added later", Toast.LENGTH_SHORT).show();

                                } else if (position == 1) {
                                    Toast.makeText(GroupClickActivity.this, "Favorite Feature will be added later", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(GroupClickActivity.this, "else", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

                        builder.create().show();
                        return true;

                    }
                });


            }

            @NonNull
            @Override
            public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.second_single_group_layout, viewGroup, false);

                return new GroupsViewHolder(view);
            }
        }

        ;
        groups_recyclerview.setAdapter(adapter);
    }

    private void loadBannerAds() {

        MobileAds.initialize(this, String.valueOf(R.string.app_id));
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void loadInterStitialAds() {

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ads_unit_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                startLoadingAdsAgain();

                /* startActivity(new Intent(GroupClickActivity.this, JoinGroupActivity.class));*/
            }
        });
    }

    private void showInterstitial() {

        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();

        } else {
            Toast.makeText(this, "Ads did not loaded", Toast.LENGTH_SHORT).show();
            startLoadingAdsAgain();
        }
    }

    private void startLoadingAdsAgain() {

        if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ref != null) {
            adapter.startListening();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ref != null) {
            adapter.startListening();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
