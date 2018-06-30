package com.goldenant.bhaktisangrah.fcm;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.goldenant.bhaktisangrah.common.util.Constants;
import com.goldenant.bhaktisangrah.helpers.StorageUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.goldenant.bhaktisangrah.common.util.Constants.Key_user_fcm_id;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private static final String PREF_NAME = Constants.APP_NAME;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

            Log.d(TAG, "Refreshed token: " + refreshedToken);
        savePrefs(Key_user_fcm_id,refreshedToken);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {

    }
    public void savePrefs(String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }
}
