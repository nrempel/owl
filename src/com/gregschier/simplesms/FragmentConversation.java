package com.gregschier.simplesms;

import java.io.IOException;
import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gregschier.sms.classes.Conversation;
import com.gregschier.sms.classes.SMSMessage;
import com.gregschier.sms.helpers.DatabaseHelper;
import com.gregschier.sms.helpers.SMSHelper;

public class FragmentConversation extends ListFragment {
	protected EditText et_body;
	protected ImageButton btn_send;
	protected SMSHelper smsHelper = new SMSHelper(getActivity());
	protected Conversation conversation;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = (View) inflater.inflate(R.layout.conversation_view, container, false);
		et_body = (EditText) view.findViewById(R.id.et_body);
		btn_send = (ImageButton) view.findViewById(R.id.ib_send);

		btn_send.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SendSMSTask task = new SendSMSTask();
				task.execute(et_body.getText().toString(), FragmentConversation.this.conversation.number);
				et_body.setText("");
			}
		});
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (conversation != null) {
			refresh();
		}
	}

	@Override
	public void onStop() {
		// Save draft
		if (conversation == null) {
			super.onStop();
			return;
		}
		String body = et_body.getText().toString();

		// Don't save draft if there wasn't one, and still isn't
		if ((conversation.draft == null || conversation.draft.length() == 0) && (body == null || body.length() == 0)) {
			// Nothing yet
		} else {
			conversation.draft = body;
			DatabaseHelper db = DatabaseHelper.getInstance(this.getActivity());
			db.updateConversation(conversation);
		}

		super.onStop();
	}
	
	public void refresh(Conversation conversation) {
		this.conversation = conversation;
		refresh();
	}
	
	public void refresh() {
		if (conversation.unseen != null && conversation.unseen == true) {
			conversation.unseen = false;
			DatabaseHelper db = DatabaseHelper.getInstance(this.getActivity());
			db.updateConversation(conversation);
		}
		new ShowMessagesTask().execute(conversation.conversation_id);
		if (et_body != null && conversation.draft != null && conversation.draft.length() > 0) {
			et_body.setText(conversation.draft);
		}
	}

	public class ShowMessagesTask extends AsyncTask<String, Float, ArrayList<SMSMessage>> {
		@Override
		protected void onPostExecute(ArrayList<SMSMessage> result) {
			Context context = FragmentConversation.this.getActivity();
			if (context != null) {
				ListAdapterConversation adapter = new ListAdapterConversation(context, result);
				FragmentConversation.this.setListAdapter(adapter);
			}
			super.onPostExecute(result);
		}

		@Override
		protected ArrayList<SMSMessage> doInBackground(String... params) {
			if (params.length < 1) {
				return new ArrayList<SMSMessage>();
			}
			ActivityMessageList activity = (ActivityMessageList) getActivity();
			if (activity != null) {
				DatabaseHelper db = ((ActivityMessageList) getActivity()).db;
				ArrayList<SMSMessage> messages = db.getMessages(params[0]);
				return messages;
			} else {
				return new ArrayList<SMSMessage>();
			}

		}
	}

	private class SendSMSTask extends AsyncTask<String, Boolean, Boolean> {
		protected Boolean doInBackground(String... params) {
			try {
				String conversationId = FragmentConversation.this.smsHelper.sendSMS(params[0], params[1],
						(ActivityMessageList) getActivity());
				if (conversationId == null) {
					return false;
				}
//				FragmentConversation.this.conversation.conversation_id = messages.get(0).conversation_id;
				FragmentConversation.this.conversation.conversation_id = conversationId;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		protected void onPostExecute(Boolean success) {
			if (success) {
				// FragmentConversation.this.refresh();
				ActivityMessageList activity = (ActivityMessageList) FragmentConversation.this.getActivity();
				if (activity != null) {
					Log.d("FragmentConversation", "DONE SENDING");
					activity.refreshFragments();
				} else {
					Log.d("FragmentConversation", "DONE SENDING FAIL");
				}
			} else {
				Toast.makeText(FragmentConversation.this.getActivity(), "Failed to send message", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

}
