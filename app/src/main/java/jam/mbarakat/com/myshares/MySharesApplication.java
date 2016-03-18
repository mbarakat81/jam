package jam.mbarakat.com.myshares;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

import jam.mbarakat.com.myshares.activities.MainActivity;
import jam.mbarakat.com.myshares.helpers.ParseConstants;

/**
 * Created by MBARAKAT on 1/29/2016.
 */
public class MySharesApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.initialize(this,"W4BiNGJnOVBjAqssRttv5R056NSqejcIPFyiFZna","2df1kC7vsbvv9Vrc7uoAg5FIoA28KdtcOOUSuo8c");
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static void updateParseInstallation(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }
}
