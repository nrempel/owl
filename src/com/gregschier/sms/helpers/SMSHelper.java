package com.gregschier.sms.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.gregschier.sms.classes.Conversation;
import com.gregschier.sms.classes.RSMSMessage;
import com.gregschier.sms.classes.SMSMessage;

public class SMSHelper {
	private static final String ACCOUNT_SID = "AC4f7865665b1d8ccd52c37c740c579102";
	private static final String AUTH_TOKEN = "3bf3791e69eaa77ebbfc61cf6ce22f6f";
	
	// TEST USER --> test2@test.com : test
	private static final String USER_ID = "515a1fa5fbc1cf000000008b";
	
	private static final Integer SMS_LENGTH = 160;

	private static final String BASE_URL = "http://smsserver.jit.su/api/v1";
	private DatabaseHelper db;

	public SMSHelper(Context context) {
		this.db = DatabaseHelper.getInstance(context);
	}

	public SMSMessage sendSingleSMS(String body, String number, Context context) throws IOException {
    	int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
    	HttpParams httpParams = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
    	HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
    	DefaultHttpClient client = new DefaultHttpClient(httpParams);
    	
    	number = URLEncoder.encode(number, "utf8");
    	body = URLEncoder.encode(body, "utf8");
    	
    	String url = BASE_URL+"/messages/?user="+USER_ID+"&to="+number+"&body="+body;
    	
    	HttpPost request = new HttpPost(url);
    	
    	HttpResponse response = client.execute(request);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		response.getEntity().writeTo(out);
		out.close();
		String responseString = out.toString();
    	Log.d("SMSHELPER", responseString);

        return null;
		
//		Gson gson = new Gson();
//		RSMSMessage apiResponse = gson.fromJson(responseString, RSMSMessage.class);
//
//		if (apiResponse.success && apiResponse.data != null && apiResponse.data.size() > 0) {
//			SMSMessage sms = apiResponse.data.get(0);
//			if (sms.sid != null) {
//				sentNewSMS(sms, context);
//				return sms;
//			} else {
//				// Failed to send SMS
//				return null;
//			}
//	} else {
//			// Failed to send SMS
//            Log.d("SMSHELPER", "SEND FAIL: data size"+Integer.toString(apiResponse.data.size()));
//			return null;
//		}
    }

	public String sendSMS(String body, String number, Context context) throws IOException {
		ArrayList<SMSMessage> messages = new ArrayList<SMSMessage>();
		int numMessages = (body.length() / SMS_LENGTH) + 1;

		for (int i = 0; i < numMessages; i++) {
			String subBody = body.substring(i * SMS_LENGTH, Math.min((i + 1) * SMS_LENGTH, body.length()));
            sendSingleSMS(subBody, number, context);
//			SMSMessage sms = sendSingleSMS(subBody, number, context);
//			if (sms == null) {
//				// TODO: Save failed messages and let user retry them later
//				Log.w("sendSMS", "SMS Message is null");
//			} else {
//				messages.add(sms);
//			}
		}

		return number;
	}

	public Conversation receivedNewSMS(SMSMessage sms, Context context) {
        String conversationId;
        if (sms.direction.equals("inbound")) {
            conversationId = sms.from;
        } else {
            conversationId = sms.to;
        }
		sms.conversation_id = conversationId;
		db.addMessage(sms);
		Conversation conversation = db.getConversation(conversationId);
		if (conversation == null) {
			// Make a new conversation
			conversation = new Conversation();
			conversation.conversation_id = sms.from;
			conversation.contact = "";
			conversation.number = sms.from;
			conversation.name = "Name";
			conversation.draft = "";
			conversation.modified = new Date();
			conversation.count = 1;
			conversation.messages = new ArrayList<SMSMessage>();
			conversation.messages.add(sms);
			conversation.unseen = true;
			db.addConversation(conversation);
		} else {
			conversation.modified = new Date();
			ArrayList<SMSMessage> messages = db.getMessages(conversation.conversation_id);
			conversation.count = messages.size();
			conversation.unseen = true;
			db.updateConversation(conversation);
		}

		return conversation;
	}

	public void sentNewSMS(SMSMessage sms, Context context) {
		String conversationId = sms.to;
		sms.conversation_id = conversationId;
		db.addMessage(sms);

		Conversation conversation = db.getConversation(conversationId);
		if (conversation == null) {
			// Make a new conversation
			conversation = new Conversation();
			conversation.conversation_id = sms.to;
			conversation.contact = "";
			conversation.number = sms.to;
			conversation.name = "Gregory Schier";
			conversation.draft = "";
			conversation.modified = new Date();
			conversation.count = 1;
			conversation.messages = new ArrayList<SMSMessage>();
			conversation.messages.add(sms);
			db.addConversation(conversation);
		} else {
			conversation.modified = new Date();
			ArrayList<SMSMessage> messages = db.getMessages(conversation.conversation_id);
			conversation.count = messages.size();
			conversation.draft = "";
			db.updateConversation(conversation);
		}
	}

	public void restoreAllSMS(Context context) {
		try {
			ArrayList<SMSMessage> all = getAllSMS();
			for (int i = 0; i < all.size(); i++) {
				SMSMessage sms = all.get(i);
				if (sms.direction.equals("inbound")) {
					receivedNewSMS(sms, context);
				} else {
					sentNewSMS(sms, context);
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SMSMessage> getAllSMS() throws ClientProtocolException, IOException {
		ArrayList<SMSMessage> messages;
		int TIMEOUT_MILLISEC = 10000; // = 10 seconds
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
		DefaultHttpClient client = new DefaultHttpClient(httpParams);
		client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, 443),
				new UsernamePasswordCredentials(ACCOUNT_SID, AUTH_TOKEN));
		String url = BASE_URL + "/messages";

		HttpGet request = new HttpGet(url);

		HttpResponse response = client.execute(request);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		response.getEntity().writeTo(out);
		out.close();
		String responseString = out.toString();

		Gson gson = new Gson();

		RSMSMessage res = gson.fromJson(responseString, RSMSMessage.class);

		messages = res.data;
		Log.d("SMSHELPER", "ALL SMS");

		return messages;
	}
}
