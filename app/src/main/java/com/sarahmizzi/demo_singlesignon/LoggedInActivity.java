package com.sarahmizzi.demo_singlesignon;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;

public class LoggedInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        Button logoutButton = (Button) findViewById(R.id.logout_button);

        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            signInButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser.logOut();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            logoutButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser.logOut();
                    ParseUser currentUser = ParseUser.getCurrentUser();

                    if (currentUser == null) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

}
