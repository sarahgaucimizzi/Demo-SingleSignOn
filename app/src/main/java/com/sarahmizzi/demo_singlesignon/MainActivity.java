package com.sarahmizzi.demo_singlesignon;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final Activity activity = this;
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

                final List<String> permissions = Arrays.asList("public_profile", "email", "user_birthday");

                ParseFacebookUtils.logInWithReadPermissionsInBackground(activity, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
                        if (user == null) {
                            progressDialog.hide();
                            dialog.show();
                            Log.d("Demo", "The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            ParseFacebookUtils.linkInBackground(user, AccessToken.getCurrentAccessToken());

                            getFacebookUserDetails();

                            Log.d("Demo", "User signed up and logged in through Facebook!");
                            startActivity(intent);
                        } else {
                            ParseFacebookUtils.linkInBackground(user, AccessToken.getCurrentAccessToken());

                            getFacebookUserDetails();

                            Log.d("Demo", "User logged in through Facebook!");
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
                            Log.d("Demo", "Anonymous login failed.");
                        } else {
                            Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
                            startActivity(intent);
                            Log.d("Demo", "Anonymous user logged in.");
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
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                if (graphResponse.getError() == null) {
                    try {
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
                                Log.e("Demo", "Could not parse date.");
                            }
                        }

                        parseUser.saveInBackground();
                    } catch (JSONException e) {
                        Log.e("Demo", "Error parsing JSON", e);
                    }
                }
                else {
                    Log.e("Demo", graphResponse.getError().getErrorMessage());
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email,birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }
}
