package com.vorp.reachit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123; // can be anything
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        final Button signin = (Button)findViewById(R.id.signin);
        //LinearLayout linView = findViewById(R.id.linView);
        //View mLoginButton = SnapLogin.getButton(this, (ViewGroup)linView);
        //TODO:MAKE MAPVIEW "UNBACKABLE"

        signin.setOnClickListener(new View.OnClickListener() {
            boolean visible = false;
            @Override
            public void onClick(View view) {
                launchMapActivity();
            }
        });



    }

    private void launchMapActivity()
    {
        Intent intent = new Intent(this, MapScreen.class);
        startActivity(intent);
    }


}
