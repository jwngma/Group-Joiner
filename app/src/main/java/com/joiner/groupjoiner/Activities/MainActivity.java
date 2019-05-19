package com.joiner.groupjoiner.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.joiner.groupjoiner.Adapters.GroupsViewHolder;
import com.joiner.groupjoiner.Fragments.ChatFragment;
import com.joiner.groupjoiner.Fragments.TrendingFragment;
import com.joiner.groupjoiner.Models.GroupsModel;
import com.joiner.groupjoiner.R;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private DatabaseReference GroupsRef,TokenId;

    private RecyclerView main_recyclerview;
    private Toolbar mtoolbar;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FloatingActionButton addBtn;
    private RelativeLayout rel_alpha;

    FirebaseRecyclerOptions<GroupsModel> options;
    FirebaseRecyclerAdapter<GroupsModel, GroupsViewHolder> adapter;
    private  String tokenid;
    private TextView countText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        GroupsRef = FirebaseDatabase.getInstance().getReference().child("Groups").child("All Groups");
        TokenId = FirebaseDatabase.getInstance().getReference().child("Users");
        CreatedatabasewithTokenId();
        initToolbar();
        rel_alpha = findViewById(R.id.rel_alpha);
        countText=findViewById(R.id.remaing_clicks);
        addGroup();
        initRecyclerview();
        initNavigationDrawer();
        initBottomNavigation();


    }

    private void CreatedatabasewithTokenId() {
        tokenid= FirebaseInstanceId.getInstance().getToken();
        HashMap userMap= new HashMap();
        userMap.put("token_id",tokenid);
        TokenId.child("devices").child(tokenid).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){



                }
            }
        });

    }


    private void initToolbar() {
        mtoolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Groups Joiner");

    }

    private void addGroup() {
        addBtn = findViewById(R.id.float_addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(MainActivity.this, AddGroupActivity.class);
                startActivity(addIntent);
            }
        });
    }

    private void initRecyclerview() {

        main_recyclerview = findViewById(R.id.groups_recy);
        main_recyclerview.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        main_recyclerview.setLayoutManager(layoutManager);

        options = new FirebaseRecyclerOptions.Builder<GroupsModel>()
                .setQuery(GroupsRef, GroupsModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<GroupsModel, GroupsViewHolder>(options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull GroupsViewHolder holder, int position, @NonNull GroupsModel model) {

                final String ref = getRef(position).getKey();
                Log.d(TAG, "onBindViewHolder: key" + ref);
                holder.setImage(getApplicationContext(), model.getImage());
                Log.d(TAG, "onBindViewHolder: image" + model.getImage());
                holder.setTitle(model.getTitle());
                Log.d(TAG, "onBindViewHolder: title" + model.getTitle());
                holder.setLink(model.getLink());
                Log.d(TAG, "onBindViewHolder: link" + model.getLink());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(MainActivity.this,GroupClickActivity.class);
                        intent.putExtra("ref",ref);
                        startActivity(intent);
                    }
                });


            }

            @NonNull
            @Override
            public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_group_layout, viewGroup, false);

                return new GroupsViewHolder(view);
            }
        };
        main_recyclerview.setAdapter(adapter);
    }

    private void initNavigationDrawer() {
        navigationView = findViewById(R.id.navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mtoolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        View headerView = navigationView.inflateHeaderView(R.layout.header_layout);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_all_groups:
                        Intent all_groups= new Intent(MainActivity.this,GroupClickActivity.class);
                        all_groups.putExtra("ref","All Group");
                        startActivity(all_groups);
                        closeDrawer();
                        break;
                    case R.id.nav_photography:
                        Intent photoGraphyIntent= new Intent(MainActivity.this,GroupClickActivity.class);
                        photoGraphyIntent.putExtra("ref","PhotoGraphy");
                        startActivity(photoGraphyIntent);
                        closeDrawer();
                        break;
                    case R.id.nav_business:
                        Intent BusinessIntent= new Intent(MainActivity.this,GroupClickActivity.class);
                        BusinessIntent.putExtra("ref","Business");
                        startActivity(BusinessIntent);
                        closeDrawer();
                        break;
                    case R.id.nav_buy_n_sell:
                        Intent buyselIntent= new Intent(MainActivity.this,GroupClickActivity.class);
                        buyselIntent.putExtra("ref","Buy And Sell");
                        startActivity(buyselIntent);
                        closeDrawer();
                        break;
                    case R.id.nav_community:
                        Intent communityIntent= new Intent(MainActivity.this,GroupClickActivity.class);
                        communityIntent.putExtra("ref","Community");
                        startActivity(communityIntent);
                        closeDrawer();
                        break;
                    case R.id.nav_social_friends:
                        Intent socialIntent= new Intent(MainActivity.this,GroupClickActivity.class);
                        socialIntent.putExtra("ref","Social friends");
                        startActivity(socialIntent);
                        closeDrawer();
                        break;
                    case R.id.nav_share:
                        Intent shareintent= new Intent(Intent.ACTION_SEND);
                        shareintent.setType("text/plain");
                        String shareSub=" Your Sub Here";
                        String shareBody=" Your sms will appear here";
                        shareintent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                        shareintent.putExtra(Intent.EXTRA_TEXT,shareBody);
                        shareintent.putExtra(Intent.EXTRA_EMAIL,shareBody);
                        startActivity(Intent.createChooser(shareintent,"Share Using"));
                        /*Toast.makeText(MainActivity.this, menuItem.getTitle() + " has been clicked", Toast.LENGTH_SHORT).show();*/
                        closeDrawer();
                        break;
                    case R.id.nav_rate:
                        Intent RateIntent= new Intent(MainActivity.this,RatingActivity.class);
                        startActivity(RateIntent);
                        closeDrawer();
                        break;
                }
                return true;
            }
        });
    }

    private void initBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bot_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bot_home:
                        Intent home_intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(home_intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    case R.id.bot_trending:
                        addFragment(new TrendingFragment());
                        rel_alpha.setAlpha(0);

                        break;
                    case R.id.bot_chat:
                        addFragment(new ChatFragment());
                        rel_alpha.setAlpha(0);
                        break;
                }
                return true;
            }
        });
    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent= new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                Toast.makeText(this, item.getTitle() + " has been clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        TokenId.child("devices").child(tokenid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String count=dataSnapshot.child("count").getValue().toString();
                countText.setText("Remaining Clicks "+count);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();
    }
}
