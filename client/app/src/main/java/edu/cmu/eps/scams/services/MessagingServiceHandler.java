package edu.cmu.eps.scams.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.List;

import edu.cmu.eps.scams.logic.ApplicationLogicFactory;
import edu.cmu.eps.scams.logic.IApplicationLogic;
import edu.cmu.eps.scams.logic.model.ClassifierParameters;
import edu.cmu.eps.scams.logic.model.IncomingMessage;
import edu.cmu.eps.scams.logic.model.MessageType;
import edu.cmu.eps.scams.logic.model.NotifyMessageContent;
import edu.cmu.eps.scams.logic.model.ReviewMessageContent;
import edu.cmu.eps.scams.notifications.NotificationFacade;

/**
 * Created by jeremy on 4/15/2018.
 * The background handler that runs on another thread. Actually does the work of polling the server
 * for messages
 */
public class MessagingServiceHandler extends Handler {

    private static final String TAG = "MessagingServiceHandler";
    private final Context context;
    private final int loopEventDelay;
    private final IApplicationLogic logic;
    private ClassifierParameters classifierParameters;
    private final NotificationFacade notificationFacade;

    public MessagingServiceHandler(Looper looper, Context context) {
        super(looper);
        this.context = context;
        this.loopEventDelay = 5000;
        this.notificationFacade = new NotificationFacade(this.context);
        this.logic = ApplicationLogicFactory.build(this.context);
        this.classifierParameters = null;
    }

    @Override
    public void handleMessage(Message message) {
        Log.d(TAG, String.format("Message received: %d %d", message.arg1, message.arg2));
        if (this.logic.getAppSettings().isRegistered() == true) {
            try {
                if (this.classifierParameters == null) {
                    this.classifierParameters = this.logic.getClassifierParameters();
                }
                List<IncomingMessage> messages = this.logic.receiveMessages();
                Log.d(TAG, String.format("Incoming messages: %d", messages.size()));
                for (IncomingMessage incomingMessage : messages) {
                    switch (MessageType.valueOf(incomingMessage.getType())) {
                        case NOTIFY: {
                            NotifyMessageContent notifyMessage = new NotifyMessageContent(incomingMessage.getContent());
                            Log.i(TAG, String.format("Notify Message to view: %s %s", notifyMessage.getTitle(), notifyMessage.getMessage()));
                            notificationFacade.createWithResponse(this.context, notifyMessage.getTitle(), notifyMessage.getMessage(), incomingMessage.getSender());
                            this.logic.acknowledgeMessage(incomingMessage);
                            break;
                        }
                        case BLOCK: {
                            NotifyMessageContent notifyMessage = new NotifyMessageContent(incomingMessage.getContent());
                            Log.i(TAG, String.format("Block Message to view: %s %s", notifyMessage.getTitle(), notifyMessage.getMessage()));
                            notificationFacade.create(this.context, notifyMessage.getTitle(), notifyMessage.getMessage());
                            this.logic.acknowledgeMessage(incomingMessage);
                            break;
                        }
                        case REVIEW: {
                            ReviewMessageContent reviewMessage = new ReviewMessageContent(incomingMessage.getContent());
                            Log.i(TAG, String.format("Review Message to view: %s %s", reviewMessage.getTranscript(), reviewMessage.getPhoneNumber()));
                            notificationFacade.createWithResponse(
                                    this.context,
                                    String.format("Potential Scam from %s?", reviewMessage.getPhoneNumber()),
                                    String.format("%s", reviewMessage.getTranscript()),
                                    incomingMessage.getSender());
                            this.logic.acknowledgeMessage(incomingMessage);
                            break;
                        }
                        case KNOWN: {
                            ReviewMessageContent reviewMessage = new ReviewMessageContent(incomingMessage.getContent());
                            Log.i(TAG, String.format("Known Message to view: %s %s", reviewMessage.getTranscript(), reviewMessage.getPhoneNumber()));
                            notificationFacade.create(
                                    this.context,
                                    String.format("Known Scam from %s", reviewMessage.getPhoneNumber()),
                                    String.format("%s", reviewMessage.getTranscript()));
                            this.logic.acknowledgeMessage(incomingMessage);
                            break;
                        }
                    }
                }
                this.sendMessageDelayed(this.buildLoopEventMessage(), this.loopEventDelay);
            } catch (Exception e) {
                Log.i(TAG, String.format("Encountered exception: %s", e.getMessage()));
            }
        }
    }

    private Message buildLoopEventMessage() {
        return this.obtainMessage(1,0, 0);
    }
}
