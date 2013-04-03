package com.gregschier.simplesms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;
import com.gregschier.sms.classes.Contact;
import com.gregschier.sms.classes.Conversation;
import com.gregschier.sms.classes.SMSMessage;
import com.gregschier.sms.helpers.ContactHelper;
import com.gregschier.sms.helpers.SMSHelper;

public class GCMIntentService extends GCMBaseIntentService {
	private SMSHelper smsHelper;

	private void notifyActivity(Conversation conversation, Context context) {
		Intent intent = new Intent("new-message");
	    intent.putExtra("conversation_id", conversation.conversation_id);
	    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	} 

	private void generateNotification(Context context, String message, String fromOrTitle, Boolean isGeneral) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Prepare intent which is triggered if the
		// notification is selected
		Intent intent = new Intent(context, ActivityMessageList.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

		// Build notification
        String title = fromOrTitle;

        if (!isGeneral) {
            title = "SMS (" + fromOrTitle + ")";
        }

		Notification.Builder builder = new Notification.Builder(context).setContentTitle(title).setContentText(message)
				.setSmallIcon(R.drawable.ic_action_search).setDefaults(Notification.DEFAULT_ALL)
				.setContentIntent(pIntent);
		Notification noti = builder.getNotification();

        if (isGeneral) {
            noti.tickerText = message;
        } else {
            noti.tickerText = title + " says: " + message;
        }

		// Hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(0, noti);
	}

	@Override
	protected void onError(Context context, String str) {
        generateNotification(context, "GCMIntentService Error", str, true);
    }

	@Override
	protected void onMessage(Context context, Intent arg1) {
		String txt = arg1.getStringExtra("json");
//		if (txt != null) {
//			Log.i("SMS_TEST", txt);
//			Toast.makeText(context, txt, Toast.LENGTH_LONG).show();
//			generateNotification(context, txt, "HELLO", false);
//		}
        if (txt != null) {
		
            // PARSE MESSAGE
            String json = arg1.getStringExtra("json");
            Gson gson = new Gson();
            SMSMessage sms = gson.fromJson(json, SMSMessage.class);

            // UPDATE CONVERSATION
            smsHelper = new SMSHelper(context);
            Conversation conversation = smsHelper.receivedNewSMS(sms, context);

            if (sms.direction.equals("inbound")) {
                // GENERATE NOTIFICATION
                ContactHelper contactHelper = ContactHelper.getInstance(context);
                Contact contact = contactHelper.getContact(sms.from);
                generateNotification(context, sms.body, contact.name, false);
            }
            notifyActivity(conversation, context);
        }
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
	}

}
