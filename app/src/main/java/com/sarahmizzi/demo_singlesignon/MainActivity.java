package com.sarahmizzi.demo_singlesignon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final Activity activity = this;
    final String TAG = "MainActivity";
    public String name = "";
    public String email = "";
    public String birthDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);

        // Check if currentUser is set in cache -> if yes -> logged in -> go to LoggedInActivity
        if(ParseUser.getCurrentUser() != null){
            Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

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

        // Set on click listener for Register / Login Button
        Button registerLogin = (Button) findViewById(R.id.register_login_button);
        registerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start LoginActivity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        // Set on click listener for Sign in with Facebook Button
        Button signIn = (Button) findViewById(R.id.sign_in_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start progress dialog
                progressDialog.show();

                // Set facebook permissions
                final List<String> permissions = Arrays.asList("public_profile", "email", "user_birthday");

                // Start login with Facebook requesting set permissions
                ParseFacebookUtils.logInWithReadPermissionsInBackground(activity, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (user == null) {
                            // Something went wrong, show error dialog
                            progressDialog.hide();
                            dialog.show();
                            Log.d(TAG, "The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            // Link Parse User to Facebook Account
                            ParseFacebookUtils.linkInBackground(user, AccessToken.getCurrentAccessToken());

                            // Update User Details
                            getFacebookUserDetails();

                            Log.d(TAG, "User signed up and logged in through Facebook!");

                            // Start LoggedInActivity
                            startActivity(intent);
                            finish();
                        } else {
                            // Link Parse User to Facebook Account
                            ParseFacebookUtils.linkInBackground(user, AccessToken.getCurrentAccessToken());

                            // Update User Details
                            getFacebookUserDetails();

                            Log.d(TAG, "User logged in through Facebook!");

                            // Start LoggedInActivity
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });

        // Set on click listener for Skip Login Button
        Button skipLogin = (Button) findViewById(R.id.skip_login);
        skipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start progress dialog
                progressDialog.show();

                ParseAnonymousUtils.logIn(new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e != null) {
                            // Something went wrong, show error dialog
                            progressDialog.hide();
                            dialog.show();
                            Log.d(TAG, "Anonymous login failed.");
                        } else {
                            // Anonymous user creation successful, start LoggedInActivity
                            Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            Log.d(TAG, "Anonymous user logged in.");
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

    public void getFacebookUserDetails() {
        final ParseUser parseUser = ParseUser.getCurrentUser();
        // Get Facebook Account User data as JSON Object using GRAPH API
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                if (graphResponse.getError() == null) {
                    try {
                        // Read data from JSON Object and set up user
                        if (jsonObject.has("name")) {
                            name = jsonObject.getString("name");
                            parseUser.put("name", name);
                        }
                        if (jsonObject.has("email")) {
                            email = jsonObject.getString("email");
                            parseUser.setEmail(email);
                        }
                        if(jsonObject.has("birthday")) {
                            birthDate = jsonObject.getString("birthday");
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            try {
                                Date date = simpleDateFormat.parse(birthDate);
                                Date timeZoneDate = new Date(date.getTime() + (60 * 60 * 1000));
                                parseUser.put("birthDate", timeZoneDate);
                            }
                            catch(java.text.ParseException e){
                                Log.e(TAG, "Could not parse date.");
                            }
                        }

                        parseUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null){
                                    Log.d(TAG, "User saved successfully");
                                }
                                else{
                                    Log.e(TAG, "User did not save" + e.toString());
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON", e);
                    }
                }
                else {
                    Log.e(TAG, graphResponse.getError().getErrorMessage());
                }
            }
        });
        Bundle parameters = new Bundle();
        // Set JSON object field structure
        parameters.putString("fields", "name,email,birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }
}
