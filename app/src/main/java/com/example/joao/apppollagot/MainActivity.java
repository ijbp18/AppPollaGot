package com.example.joao.apppollagot;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.joao.apppollagot.Interface.IFirebaseLoadDone;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IFirebaseLoadDone {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance() ;
    private DatabaseReference databaseIronThrone ;
    SearchableSpinner spnProfiles ;
    IFirebaseLoadDone iFirebaseLoadDone;
    List<Profile> profileList;
    boolean isFirstTimeClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            spnProfiles =  (SearchableSpinner)findViewById(R.id.spnProfiles);
            mSwipeView = (SwipePlaceHolderView)findViewById(R.id.swipeView);
            mContext = getApplicationContext();

//            spnProfiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                    if(!isFirstTimeClick){
//                        Profile profile = profileList.get(position);
//                        Toast.makeText(mContext, "ProfileSel "+profile.getName(), Toast.LENGTH_SHORT).show();
//                    }else
//                        isFirstTimeClick = false;
//
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//
//            cargarInfoSpinner();


            mSwipeView.getBuilder()
                    .setDisplayViewCount(3)
                    .setSwipeDecor(new SwipeDecor()
                            .setPaddingTop(20)
                            .setRelativeScale(0.01f)
                            .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                            .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));


            for(Profile profile : Utils.loadProfiles(this.getApplicationContext())){
                mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
            }


            findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwipeView.doSwipe(false);
                }
            });

            findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwipeView.doSwipe(true);
                }
            });

    }

    private void cargarInfoSpinner() {
        //init db
        databaseIronThrone = FirebaseDatabase.getInstance().getReference("PROFILE THRONE");

        //init interface
        iFirebaseLoadDone = this;

        //get data
        databaseIronThrone.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    List<Profile> profiles = new ArrayList<>();

                    for(DataSnapshot profileSnap: dataSnapshot.getChildren()){
                        profiles.add(profileSnap.getValue(Profile.class));
                    }

                    iFirebaseLoadDone.onFireLoadSuccess(profiles);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iFirebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage() );
            }
        });
    }


    @Override
    public void onBackPressed() {
        loginOut();

    }

    private void loginOut() {

        AuthUI.getInstance().signOut(MainActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Cerró sesión correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, LoginActivityFB.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Ocurrió un error al cerrar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFireLoadSuccess(List<Profile> listProfiles) {
        profileList = listProfiles;
        //get all name
        List<String> nameList = new ArrayList<>();

        for (Profile profile: listProfiles)
            nameList.add(profile.getName());

        //create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nameList);
        spnProfiles.setAdapter(adapter);

    }

    @Override
    public void onFirebaseLoadFailed(String message) {

    }
}