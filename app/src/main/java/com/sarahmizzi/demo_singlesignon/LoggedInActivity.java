package com.sarahmizzi.demo_singlesignon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;
import java.text.SimpleDateFormat;

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

        // Check if user is anonymous or logged in
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            userDetails.setText("You have logged in as an anonymous user. Please sign in.");

            // Show Sign in Button
            signInButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);

            // Set on click listener for Sign in Button
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start progress dialog
                    progressDialog.show();

                    try {
                        // Log out anonymous user
                        ParseUser.logOut();
                        ParseUser currentUser = ParseUser.getCurrentUser();

                        if (currentUser == null) {
                            // If log out successful, start MainActivity
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            // Something went wrong, show error dialog
                            progressDialog.hide();
                            dialog.show();
                        }
                    } catch (Exception e) {
                        // Something went wrong, show error dialog
                        progressDialog.hide();
                        dialog.show();
                    }
                }
            });
        } else {
            // If user is logged in get current user
            ParseUser user = ParseUser.getCurrentUser();

            String showText = "";

            // Show user details
            if (user.get("name") != null) {
                showText = "Hello, " + user.get("name") + ". You have logged in successfully using Facebook.";
            } else {
                showText = "Hello \nYou have logged in successfully.";
            }

            if (user.getEmail() != null) {
                showText += " Your email address is " + user.getEmail() + ".";
            }

            if (user.get("birthDate") != null) {
                SimpleDateFormat parserSDF = new SimpleDateFormat("dd/MM/yyyy");
                String date = parserSDF.format(user.getDate("birthDate"));
                showText += " Your birthday is on " + date + ".";
            }
            userDetails.setText(showText);

            // Show Logout Button
            logoutButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);

            // Set on click listener for Logout Button
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start progress dialog
                    progressDialog.show();

                    try {
                        // Log out user
                        ParseUser.logOut();
                        ParseUser currentUser = ParseUser.getCurrentUser();

                        if (currentUser == null) {
                            // If log out successful, start MainActivity
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            // Something went wrong, show error dialog
                            progressDialog.hide();
                            dialog.show();
                        }
                    } catch (Exception e) {
                        // Something went wrong, show error dialog
                        progressDialog.hide();
                        dialog.show();
                    }
                }
            });
        }
    }

}
