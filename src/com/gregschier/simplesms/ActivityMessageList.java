package com.gregschier.simplesms;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.gregschier.sms.classes.Contact;
import com.gregschier.sms.classes.Conversation;
import com.gregschier.sms.helpers.ContactHelper;
import com.gregschier.sms.helpers.DatabaseHelper;
import com.gregschier.sms.helpers.SMSHelper;

public class ActivityMessageList extends FragmentActivity {

	private static final String SENDER_ID = "168971082725";
	private static final int PICK_CONTACT = 1337;
	private ActionBar actionBar;
	private FragmentConversation currentFragmentConversation;
	private FragmentConversationList currentFragmentConversationList;
	public DatabaseHelper db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		setContentView(R.layout.main);
		// Set up the action bar.
		this.db = DatabaseHelper.getInstance(this.getApplicationContext());

		if (savedInstanceState == null) {
			FragmentConversationList fragment = new FragmentConversationList();
			currentFragmentConversationList = fragment;
			getFragmentManager().beginTransaction().replace(R.id.container, (Fragment) fragment).commit();
		}

		actionBar.setTitle("");
		actionBar.setDisplayShowTitleEnabled(true);

		// Set up GCM
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, SENDER_ID);
			Log.i("ActivityMessageList", "REGISTERING");
		} else {
			// Already registered
			Log.i("ActivityMessageList", "GCM_ID: " + regId);
		}

		// Set up BroadcastManager to listen for update requests
		LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
				new IntentFilter("new-message"));
		
		// Setup UP button
		getFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {
			public void onBackStackChanged() {
				int backCount = getFragmentManager().getBackStackEntryCount();
				Log.i("MainActivity", "Backcount: "+backCount);
				if (backCount == 0) { 
					if (currentFragmentConversationList != null) {
						currentFragmentConversationList.refresh();
					}
					actionBar.setTitle("");
					ActionBar ab = getActionBar();
					ab.setHomeButtonEnabled(false);
					ab.setDisplayHomeAsUpEnabled(false);
				} else {
					ActionBar ab = getActionBar();
					ab.setHomeButtonEnabled(true); 
					ab.setDisplayHomeAsUpEnabled(true);
				}
				ActivityMessageList.this.invalidateOptionsMenu();
			}
		});
		
		// Restore missing SMS
//		deleteDatabase(DatabaseHelper.DB_NAME);
//		new RestoreSMSTask().execute(this);
	}

	private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			String conversationId = intent.getStringExtra("conversation_id");
			Log.d("receiver", "Got message: " + conversationId);
			ActivityMessageList.this.refreshFragments();
		}
	};
	
	public void refreshFragments() {
		if (ActivityMessageList.this.currentFragmentConversation != null) {
			ActivityMessageList.this.currentFragmentConversation.refresh();
		}
		if (ActivityMessageList.this.currentFragmentConversationList != null) {
			ActivityMessageList.this.currentFragmentConversationList.refresh();
		}
	}

	public void showConversation(Conversation conversation) {
		if (conversation != null) {
			ContactHelper contactHelper = ContactHelper.getInstance(this);
			Contact contact = contactHelper.getContact(conversation.number);
			actionBar.setTitle(contact.name);
			FragmentConversation fragment = new FragmentConversation();
			fragment.refresh(conversation);
			currentFragmentConversation = fragment;
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.addToBackStack("main");
			ft.add(R.id.container, (Fragment) fragment);
			ft.commit();
		}
	}
	
	public void goBack() {
		int backCount = getFragmentManager().getBackStackEntryCount();
		if (backCount == 0) {
			super.onBackPressed();
		} else {
			getFragmentManager().popBackStack();
		}
	}
	
	@Override
	public void onBackPressed() {
		goBack();
	}
	
	@Override
	protected void onDestroy() {
	  // Unregister since the activity is about to be closed.
	  LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
	  super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.message_list, menu);
		return true;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.i("ActivityMessageList", "Rotated");
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_message:
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
			startActivityForResult(intent, PICK_CONTACT);
			return true;

		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			getFragmentManager().popBackStack();
			break;
		}

		return (super.onOptionsItemSelected(item));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (intent == null) {
			return;
		}
		switch (requestCode) {
		case PICK_CONTACT:
			Cursor cursor = null;
			String phoneNumber = "";
			ArrayList<String> allNumbers = new ArrayList<String>();
			int phoneIdx = 0;
			Uri result = intent.getData();
			String id = result.getLastPathSegment();
			cursor = getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + "=?", new String[] { id },
					null);
			phoneIdx = cursor.getColumnIndex(Phone.DATA);
			if (cursor.moveToFirst()) {
				while (cursor.isAfterLast() == false) {
					phoneNumber = cursor.getString(phoneIdx);
					allNumbers.add(phoneNumber);
					cursor.moveToNext();
				}
			}
			cursor.close();

			final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
			AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMessageList.this);
			builder.setTitle("Choose a number");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					String selectedNumber = items[item].toString();
					selectedNumber = selectedNumber.replace("-", "");
					Conversation conversation = new Conversation();
					conversation.number = selectedNumber;
					conversation.count = 0;
					conversation.draft = "";
					ActivityMessageList.this.showConversation(conversation);
				}
			});

			AlertDialog alert = builder.create();
			if (allNumbers.size() > 1) {
				alert.show();
			} else if (allNumbers.size() == 1){
				// Don't show picker for just one
				String selectedNumber = phoneNumber.toString();
				selectedNumber = selectedNumber.replace("-", "");
				Conversation conversation = new Conversation();
				conversation.number = selectedNumber;
				conversation.count = 0;
				conversation.draft = "";
				ActivityMessageList.this.showConversation(conversation);
			} else {
                Toast.makeText(getApplicationContext(), "No Number(s) Found", Toast.LENGTH_LONG).show();
            }
			break;
		default:
			super.onActivityResult(requestCode, resultCode, intent);
		}
	}
	
	public class RestoreSMSTask extends AsyncTask<Context, Float, Boolean> {

		@Override
		protected Boolean doInBackground(Context... params) {
			SMSHelper smsHelper = new SMSHelper(params[0]);
			smsHelper.restoreAllSMS(params[0]);
			return true;
		}
		
	}
}
