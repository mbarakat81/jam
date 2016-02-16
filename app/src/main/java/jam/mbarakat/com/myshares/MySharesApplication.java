package jam.mbarakat.com.myshares;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

/**
 * Created by MBARAKAT on 1/29/2016.
 */
public class MySharesApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.initialize(this,"W4BiNGJnOVBjAqssRttv5R056NSqejcIPFyiFZna","2df1kC7vsbvv9Vrc7uoAg5FIoA28KdtcOOUSuo8c");



    }
}
