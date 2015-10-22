package com.sarahmizzi.demo_singlesignon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    final String TAG = "LoginActivity";
    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        // Set on click listener for Sign in Button
        Button signIn = (Button) findViewById(R.id.sign_in_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start progress dialog
                progressDialog.show();

                // Check if user already exists
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("username", email.getText().toString());
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, com.parse.ParseException e) {
                        if(!list.isEmpty()){
                            // User exists, try log in
                            ParseUser.logInInBackground(email.getText().toString(), password.getText().toString(), new LogInCallback() {
                                public void done(ParseUser user, ParseException e) {
                                    if (user != null) {
                                        // Username and password match, start LoggedInActivity
                                        Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // Something went wrong, show error dialog
                                        progressDialog.hide();
                                        dialog.show();
                                        Log.e(TAG, "Login failed.");
                                    }
                                }
                            });
                        }
                        else{
                            // User not found, try sign up
                            ParseUser user = new ParseUser();
                            user.setUsername(email.getText().toString());
                            user.setPassword(password.getText().toString());
                            user.setEmail(email.getText().toString());

                            user.signUpInBackground(new SignUpCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // Sign up successful, start LoggedInActivity
                                        Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // Something went wrong, show error dialog
                                        progressDialog.hide();
                                        dialog.show();
                                        Log.e(TAG, "Sign up failed.");
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
