package com.sarahmizzi.demo_singlesignon;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);

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

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, LoggedInActivity.class);
            startActivity(intent);
        }

        Button signIn = (Button) findViewById(R.id.sign_in_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                final List<String> permissions = new ArrayList<>();
                permissions.add("public_profile");
                permissions.add("email");
                permissions.add("user_friends");
                permissions.add("user_birthday");

                ParseFacebookUtils.logInWithReadPermissionsInBackground(activity, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
                        if (user == null) {
                            progressDialog.hide();
                            dialog.show();
                            Log.d("MyApp", "The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                            startActivity(intent);
                        } else {
                            Log.d("MyApp", "User logged in through Facebook!");
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        Button skipLogin = (Button) findViewById(R.id.skip_login);
        skipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                ParseAnonymousUtils.logIn(new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e != null) {
                            progressDialog.hide();
                            dialog.show();
                            Log.d("MyApp", "Anonymous login failed.");
                        } else {
                            Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
                            startActivity(intent);
                            Log.d("MyApp", "Anonymous user logged in.");
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
