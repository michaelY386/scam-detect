package edu.cmu.eps.scams.utilities;

import android.util.Base64;

import java.util.Random;

/*
* Create random strings.
* */
public class RandomUtility {

    public static String getString(int length) {
        Random random = new Random();
        byte data[] = new byte[length];
        random.nextBytes(data);
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

}
