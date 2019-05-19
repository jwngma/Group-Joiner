package com.joiner.groupjoiner.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.joiner.groupjoiner.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddGroupActivity extends AppCompatActivity {
    private static final String TAG = "AddGroupActivity";

    private DatabaseReference AllGroupRef;
    private DatabaseReference CategoryRef;
    private DatabaseReference LanguageRef;
    private Button addGroupBtn;
    private EditText GroupName, GroupLink;
    private String groupname, grouplink, category, language;
    private Spinner Category_Spinner, Language_Spinner;
    private Toolbar mtoolbar;
    ArrayAdapter<CharSequence> cateAdapter;
    ArrayAdapter<CharSequence> lanAdapter;
    private String saveCurrentTime, saveCurrentDate, random_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        AllGroupRef= FirebaseDatabase.getInstance().getReference().child("Groups").child("NewlyAddedGroups");
        CategoryRef=FirebaseDatabase.getInstance().getReference().child("Groups").child("Category");
        LanguageRef=FirebaseDatabase.getInstance().getReference().child("Groups").child("Language");

        initAll();
        initToolbar();
        initSpinner();
        AddGroupToDatabase();


    }




    private void initAll() {
        addGroupBtn = findViewById(R.id.addBtn);
        GroupName = findViewById(R.id.group_name);
        GroupLink = findViewById(R.id.group_link);
        Category_Spinner = findViewById(R.id.group_category);
        Language_Spinner = findViewById(R.id.graoup_language);
        mtoolbar = findViewById(R.id.add_toolbar);
    }

    private void initToolbar() {
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Add Group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void initSpinner() {
        cateAdapter = ArrayAdapter.createFromResource(this, R.array.category_names, android.R.layout.simple_spinner_item);
        cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Category_Spinner.setAdapter(cateAdapter);


        lanAdapter = ArrayAdapter.createFromResource(this, R.array.language_names, android.R.layout.simple_spinner_item);
        lanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Language_Spinner.setAdapter(lanAdapter);

        Category_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                category=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Language_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                language=parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void AddGroupToDatabase() {

        addGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                groupname=GroupName.getText().toString();
                grouplink=GroupLink.getText().toString();



                if (TextUtils.isEmpty(groupname)){
                    Toast.makeText(AddGroupActivity.this, "Group name canneot be Empty", Toast.LENGTH_SHORT).show();

                }
                else if (TextUtils.isEmpty(grouplink) ){
                    Toast.makeText(AddGroupActivity.this, "Group Link cannot be empty", Toast.LENGTH_SHORT).show();

                }
                else if (grouplink.length()<49 && grouplink.length()>49){
                    Toast.makeText(AddGroupActivity.this, "Please Enter a Valid Group Link", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(category)){
                    Toast.makeText(AddGroupActivity.this, "category Must be Selected", Toast.LENGTH_SHORT).show();

                }
                else if (TextUtils.isEmpty(language)){
                    Toast.makeText(AddGroupActivity.this, "Language Must be Selected", Toast.LENGTH_SHORT).show();

                }
                else {

                    Log.d(TAG, "onClick: groupname"+groupname);
                    Log.d(TAG, "onClick: grouplink"+grouplink);
                    Log.d(TAG, "onClick: category"+category);
                    Log.d(TAG, "onClick: language"+language);
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                    saveCurrentDate = currentdate.format(calendar.getTime());

                    Calendar calforTime = Calendar.getInstance();
                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                    saveCurrentTime = currentTime.format(calforTime.getTime());

                    random_name = saveCurrentDate + saveCurrentTime;

                    final HashMap map=new HashMap();
                    map.put("category",category);
                    map.put("language",language);
                    map.put("link",grouplink);
                    map.put("title",groupname);
                    CategoryRef.child(category).child(groupname+random_name).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            LanguageRef.child(language).child(groupname+random_name).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()){
                                        AllGroupRef.child(groupname+random_name).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()){
                                                    GroupName.setText("");
                                                    GroupLink.setText("");
                                                    Category_Spinner.clearFocus();
                                                    Language_Spinner.clearFocus();
                                                    sendTomain();
                                                }
                                            }
                                        });

                                    }
                                    else {
                                        String error=task.getException().getMessage();
                                        Toast.makeText(AddGroupActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });


                }
            }
        });
    }

    private void sendTomain() {
        Intent intent= new Intent(AddGroupActivity.this,MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }


}
