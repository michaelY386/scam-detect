package edu.cmu.eps.scams.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import edu.cmu.eps.scams.FirstTimeLogin;
import edu.cmu.eps.scams.R;
import edu.cmu.eps.scams.logic.ApplicationLogicFactory;
import edu.cmu.eps.scams.logic.ApplicationLogicResult;
import edu.cmu.eps.scams.logic.ApplicationLogicTask;
import edu.cmu.eps.scams.logic.IApplicationLogic;
import edu.cmu.eps.scams.logic.IApplicationLogicCommand;
import edu.cmu.eps.scams.logic.model.AppSettings;
import edu.cmu.eps.scams.logic.model.MessageType;
import edu.cmu.eps.scams.logic.model.OutgoingMessage;

public class MessageResponseReceiver extends BroadcastReceiver {
    private static final String TAG = "MessageResponseReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationFacade facade = new NotificationFacade(context);
        String respondTo = intent.getStringExtra("respondTo");
        int notificationId = intent.getIntExtra("notificationId", -1);
        if (notificationId > 0) {
            Log.d(TAG, String.format("Cancelling notification due to user response: %d", notificationId));
            facade.cancelNotification(context, notificationId);
        }
        if (intent.getAction().equalsIgnoreCase("edu.cmu.eps.scams.ALLOW_MESSAGE")){
            Log.d(TAG, "Allow call, so no response");
        } else if (intent.getAction().equalsIgnoreCase("edu.cmu.eps.scams.BLOCK_MESSAGE")) {
            Log.d(TAG, "Sending block message");
            IApplicationLogic appLogic = ApplicationLogicFactory.build(context);
            // Start a task to check the session status
            ApplicationLogicTask task = new ApplicationLogicTask(
                    appLogic,
                    progress -> {

                    },
                    result -> {

                    }
            );
            OutgoingMessage message = new OutgoingMessage();
            message.setRecipient(respondTo);
            message.getProperties().put("title", "SCAM DETECTED!");
            message.getProperties().put("message", "Your friend thinks your current call is from a scammer!");
            message.getProperties().put("type", MessageType.BLOCK);
            task.execute((IApplicationLogicCommand) logic -> new ApplicationLogicResult(appLogic.sendMessage(message)));
        }
    }
}
