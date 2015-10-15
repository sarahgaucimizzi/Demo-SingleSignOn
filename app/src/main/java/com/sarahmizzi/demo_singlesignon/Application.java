package com.sarahmizzi.demo_singlesignon;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by Sarah on 14-Oct-15.
 */
public class Application extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "fzGiBgv40QvrSUSkCG1pBRbh6VUpqiUl0lZmPXtb", "EQP64wY9B3SIEQocYoynU8qaVdWHEIGxA6mAR7h8");

        ParseFacebookUtils.initialize(this);
    }
}
