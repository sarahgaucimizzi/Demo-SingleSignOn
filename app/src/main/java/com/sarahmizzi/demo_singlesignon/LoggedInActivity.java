package com.sarahmizzi.demo_singlesignon;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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
        TextView userDetails = (TextView) findViewById(R.id.userDetails);

        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.failed_title)
                .content(R.string.failed_content)
                .positiveText(R.string.try_again)
                .build();

        final MaterialDialog progressDialog = new MaterialDialog.Builder(this)
                .title(R.string.progress_title)
                .content(R.string.progress_content)
                .progress(true, 0)
                .build();

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        Button logoutButton = (Button) findViewById(R.id.logout_button);

        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            userDetails.setText("You have logged in as an anonymous user. Please sign in using Facebook.");
            signInButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();

                    ParseUser.logOut();
                    ParseUser currentUser = ParseUser.getCurrentUser();

                    if (currentUser == null) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        progressDialog.hide();
                        dialog.show();
                    }
                }
            });
        } else {
            userDetails.setText("You have logged in successfully using Facebook.");
            logoutButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();

                    ParseUser.logOut();
                    ParseUser currentUser = ParseUser.getCurrentUser();

                    if (currentUser == null) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        progressDialog.hide();
                        dialog.show();
                    }
                }
            });
        }
    }

}
