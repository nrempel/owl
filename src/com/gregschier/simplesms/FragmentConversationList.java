package com.gregschier.simplesms;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gregschier.sms.classes.Conversation;
import com.gregschier.sms.helpers.DatabaseHelper;

public class FragmentConversationList extends ListFragment {
	ArrayList<Conversation> conversations;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = (View) inflater.inflate(R.layout.conversation_list_view, container, false);

		refresh();
		return view;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Conversation conversation = conversations.get(position);
		if (conversation != null) {
			ActivityMessageList activity = (ActivityMessageList) getActivity();
			activity.showConversation(conversation);
		}
		
		super.onListItemClick(l, v, position, id);
	}
	
	public void refresh() {
		new RefreshTask().execute();
	}

	public class RefreshTask extends AsyncTask<String, Float, ArrayList<Conversation>> {

		@Override
		protected void onPostExecute(ArrayList<Conversation> result) {
			Context context = FragmentConversationList.this.getActivity();
			if (context != null) {
				ListAdapterConversationList adapter = new ListAdapterConversationList(context, result);
				FragmentConversationList.this.setListAdapter(adapter);
			}
			super.onPostExecute(result);
		}

		@Override
		protected ArrayList<Conversation> doInBackground(String... params) {
			ArrayList<Conversation> conversations = new ArrayList<Conversation>();
			ActivityMessageList activity = ((ActivityMessageList)FragmentConversationList.this.getActivity());
			if (activity != null) {
				DatabaseHelper db = activity.db;
				conversations = db.getConversations();
				FragmentConversationList.this.conversations = conversations;
				
			}

			return conversations;
		}

	}

}
