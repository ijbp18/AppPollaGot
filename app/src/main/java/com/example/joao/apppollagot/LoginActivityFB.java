package com.example.joao.apppollagot;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class LoginActivityFB extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 8118;
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_fb);

        providers= Arrays.asList(new  AuthUI.IdpConfig.EmailBuilder().build()
        );

        showSignInOptions();
    }

    private void showSignInOptions() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().
                setAvailableProviders(providers)
                .setLogo(R.mipmap.logo_got)
                .setTheme(R.style.MyTheme).build(), MY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == MY_REQUEST_CODE){

            if(resultCode == RESULT_OK){

//                if(!validarAccesoUser()){

                    Intent intentPrincipal = new Intent(this, MainActivity.class);
                    startActivity(intentPrincipal);
//                }else{
//
//                    Intent intentResultados = new Intent(this, FormResultadosActivity.class);
//                    startActivity(intentResultados);
//                }
            }
        }
    }

    private boolean validarAccesoUser() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance() ;
        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){

            String[] separated = user.getEmail().split("@");
            String userSel = separated[0].replace(".", " ");

            System.out.println("***userSel "+userSel);
            System.out.println("***userInDB "+firebaseDatabase.getReference("USERS").child(userSel).getKey());

            return (userSel.equals(firebaseDatabase.getReference("USERS").child(userSel).getKey()));
        }else
            return false;


    }
}
