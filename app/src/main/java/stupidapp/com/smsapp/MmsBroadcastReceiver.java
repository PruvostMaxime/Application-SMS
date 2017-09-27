package stupidapp.com.smsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by maxime on 27/09/17.
 */

public class MmsBroadcastReceiver extends BroadcastReceiver {
    public static final String SMS_BUNDLE = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        throw new UnsupportedOperationException("Bientot !");
    }
}
