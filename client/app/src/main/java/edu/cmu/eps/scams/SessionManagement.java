package edu.cmu.eps.scams;

import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Intent;


/**
 * Created by fanmichaelyang on 4/14/18.
 */

public class SessionManagement {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "MyPrefs";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_USERTYPE = "usertype";

    public static final String KEY_QR = "qr";

    public static final String KEY_ID = "userid";

    // Constructor
    public SessionManagement(Context context) {
        this._context = context;
        this.pref = _context.getSharedPreferences(PREF_NAME, 0);
        this.editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String userid, String usertype, String qr){

        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing ID in pref
        editor.putString(KEY_ID, userid);

        editor.putString(KEY_USERTYPE, usertype);

        editor.putString(KEY_QR, qr);

        // commit changes
        editor.commit();

        System.out.println(IS_LOGIN);
        System.out.println(KEY_ID);
        System.out.println(KEY_QR);
        System.out.println(KEY_USERTYPE);


    }

    public void clearLoginSession(){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, false);

        // commit changes
        editor.commit();

    }


    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_USERTYPE, pref.getString(KEY_USERTYPE, null));

        // user email id
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        user.put(KEY_QR, pref.getString(KEY_QR, null));

        // return user
        return user;
    }



    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, FirstTimeLogin.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }


    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

}
