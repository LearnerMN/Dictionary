package dic.learnermn.com.dictionary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by LearnerMN on 11/26/15.
 */
public class HomeButtonReceiver extends BroadcastReceiver {

    private final String TAG = ">>>>>: HomeButtonReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG,"Home button");
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_USER_PRESENT)){
            Log.e(TAG,"Home ACTION_USER_PRESENT");
        }
    }
}