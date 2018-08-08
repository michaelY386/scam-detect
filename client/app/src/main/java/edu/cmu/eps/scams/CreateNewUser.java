package edu.cmu.eps.scams;

import android.content.Intent;
        import android.graphics.Bitmap;
        import android.support.v4.app.Fragment;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;

        import com.google.zxing.BarcodeFormat;
        import com.google.zxing.MultiFormatWriter;
        import com.google.zxing.WriterException;
        import com.google.zxing.common.BitMatrix;
        import com.journeyapps.barcodescanner.BarcodeEncoder;


/**
 * Created by fanmichaelyang on 4/15/18.
 */

public class CreateNewUser {

    public String userId;
    public String qrString;
    public String userType;


    public CreateNewUser(String userId, String qrString, String userType) {
        this.userId = userId;
        this.qrString = qrString;
        this.userType = userType;
    }


}
