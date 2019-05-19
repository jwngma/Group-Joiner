package com.joiner.groupjoiner.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.joiner.groupjoiner.R;
import com.tapadoo.alerter.Alerter;

public class JoinGroupActivity extends AppCompatActivity {
    private static final String TAG = "JoinGroupActivity";
    private DatabaseReference TokenId;

    String title, link, ref, count;
    private Button ToastBtn;
    private int value_of_old_count, new_value;
    private String tokenid;
    private TextView title_txt;
    private Button joinBtn;
    private ImageView back_arrow;
    private TextView mToolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        tokenid = FirebaseInstanceId.getInstance().getToken();

        TokenId = FirebaseDatabase.getInstance().getReference().child("Users").child("devices").child(tokenid);

        TokenId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("count")) {
                    count = dataSnapshot.child("count").getValue().toString();
                    //Toast.makeText(JoinGroupActivity.this, count, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        getIncomingIntent();
        initToolbar();
        initAll();


    }


    private void getIncomingIntent() {
        if (getIntent().hasExtra("title") && getIntent().hasExtra("link") && getIntent().hasExtra("ref")) {
            title = getIntent().getStringExtra("title");
            link = getIntent().getStringExtra("link");
            ref = getIntent().getStringExtra("ref");

            Log.d(TAG, "getIncomingIntent: reff" + ref);

        }
    }

    private void initToolbar() {
        back_arrow = findViewById(R.id.back_arrow);
        mToolbar_title = findViewById(R.id.toolbar_text);
        mToolbar_title.setText("Join Group");
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initAll() {
        title_txt = findViewById(R.id.group_name);
        joinBtn = findViewById(R.id.joinBtn);
        ToastBtn = findViewById(R.id.trlBtn);
        title_txt.setText(title);

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
                if (isWhatsappInstalled) {

                    /*String url=link;
                    Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
                    intentWhatsapp.setData(Uri.parse(url));
                    intentWhatsapp.setPackage("com.whatsapp");
                    startActivity(intentWhatsapp);
                    Toast.makeText(JoinGroupActivity.this, "done", Toast.LENGTH_SHORT).show();*/

                    /* this is what i want to do but its not working silliy mistake*/
                    TokenId.child("count").setValue(value_of_old_count).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                            String url = link;
                            Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
                            intentWhatsapp.setData(Uri.parse(url));
                            intentWhatsapp.setPackage("com.whatsapp");
                            startActivity(intentWhatsapp);

/*
                            Uri uri = Uri.parse("smsto:" + "6000984014");
                            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hai Good Morning");
                            sendIntent.setPackage("com.whatsapp");
                            startActivity(sendIntent);*/


                        }
                    });
                } else {
                    Toast.makeText(JoinGroupActivity.this, "WhatsApp not Installed",
                            Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse("market://details?id=com.whatsapp");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(goToMarket);

                }

            }
        });


    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}
