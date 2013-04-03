package com.gregschier.sms.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gregschier.sms.classes.Conversation;
import com.gregschier.sms.classes.SMSMessage;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 2;

	public static final String DB_NAME = "smsgcm";
	private static final String TABLE_CONVERSATIONS = "conversations";
	private static final String TABLE_SMS = "sms";
	private static final String TWILIO_DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";

	// CONVERSATION TABLE COLUMNS
	private static final String C_COL_ID = "conversation_id";
	private static final String C_COL_CONTACT = "contact";
	private static final String C_COL_NUMBER = "number";
	private static final String C_COL_MODIFIED = "modified";
	private static final String C_COL_UNSEEN = "unseen";
	private static final String C_COL_NAME = "name";
	private static final String C_COL_COUNT = "count";
	private static final String C_COL_DRAFT = "draft";
	
	// SMS TABLE COLUMNS
	private static final String S_COL_ID = "sms_id";
	private static final String S_COL_CONVERSATION_ID = "conversation_id";
	private static final String S_COL_ACCOUNT_SID = "account_sid";
	private static final String S_COL_API_VERSION = "api_version";
	private static final String S_COL_BODY = "body";
	private static final String S_COL_DATE_CREATED = "date_created";
	private static final String S_COL_DATE_SENT = "date_sent";
	private static final String S_COL_DATE_UPDATED = "date_updated";
	private static final String S_COL_DIRECTION = "direction";
	private static final String S_COL_FROM = "from_number";
	private static final String S_COL_PRICE = "price";
	private static final String S_COL_SID = "sid";
	private static final String S_COL_STATUS = "status";
	private static final String S_COL_TO = "to_number";
	private static final String S_COL_URI = "uri";

	private static final String CREATE_CONVERSATIONS_TABLE = "CREATE TABLE " + TABLE_CONVERSATIONS + " (" +
			C_COL_ID + " TEXT NOT NULL UNIQUE, " +
			C_COL_CONTACT + " TEXT, " +
			C_COL_NUMBER + " TEXT, " +
			C_COL_MODIFIED + " INTEGER, " +
			C_COL_UNSEEN + " TEXT, " +
			C_COL_NAME + " TEXT, " +
			C_COL_COUNT + " INTEGER, " +
			C_COL_DRAFT + " TEXT " +
		")";
	private static final String CREATE_SMS_TABLE = "CREATE TABLE " + TABLE_SMS + " (" +
			S_COL_ID + " TEXT NOT NULL UNIQUE, " +
			S_COL_CONVERSATION_ID + " TEXT, " +
			S_COL_ACCOUNT_SID + " TEXT, " +
			S_COL_API_VERSION + " TEXT, " +
			S_COL_BODY + " TEXT, " +
			S_COL_DATE_CREATED + " TEXT, " +
			S_COL_DATE_SENT + " TEXT, " +
			S_COL_DATE_UPDATED + " TEXT, " +
			S_COL_DIRECTION + " TEXT, " +
			S_COL_FROM + " TEXT, " +
			S_COL_PRICE + " TEXT, " +
			S_COL_SID + " TEXT, " +
			S_COL_STATUS + " TEXT, " +
			S_COL_TO + " TEXT, " +
			S_COL_URI + " TEXT " +
		")";
	
	private SimpleDateFormat sdf = new SimpleDateFormat(TWILIO_DATE_FORMAT);

	private DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	private static DatabaseHelper instance = null;
	
	public static DatabaseHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseHelper(context);
		}
		return instance;
	}

	public Boolean addConversation(Conversation conversation) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(C_COL_ID, conversation.conversation_id);
		values.put(C_COL_CONTACT, conversation.contact);
		values.put(C_COL_NUMBER, conversation.number);
		values.put(C_COL_MODIFIED, conversation.modified.getTime());
        if (conversation.unseen != null) {
		    values.put(C_COL_UNSEEN, conversation.unseen.toString());
        }
		values.put(C_COL_NAME, conversation.name);
		values.put(C_COL_COUNT, conversation.count);
		values.put(C_COL_DRAFT, conversation.draft);
		
		long rowId = -1;
		try {
			rowId = db.insert(TABLE_CONVERSATIONS, null, values);
		} catch (SQLiteConstraintException e) {
		}
		db.close();
		
		return rowId != -1;
	}
	
	public Boolean removeConversation(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		int numRows = db.delete(TABLE_CONVERSATIONS, C_COL_ID + "=?", new String[] { id });
		if (numRows == 0) {
			Log.w("ListDatabaseHelper", "Didn't delete any conversations");
		}
		db.close();
		return numRows != 0;
	}
	
	public Boolean updateConversation(Conversation conversation) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		// Only need to update values that are allowed to change
		values.put(C_COL_COUNT, conversation.count);
		if (conversation.messages.size() > 0) {
			SMSMessage sms = conversation.messages.get(conversation.messages.size()-1);
			values.put(C_COL_MODIFIED, sms.date_updated);
		}
		values.put(C_COL_DRAFT, conversation.draft);
		values.put(C_COL_UNSEEN, conversation.unseen.toString());
		
		int numRows = db.update(TABLE_CONVERSATIONS, values, C_COL_ID + "=?", new String[] { conversation.conversation_id });
		db.close();
		
		return numRows > 0;
	}
	
	public Conversation getConversation(String id) {
		Conversation conversation = null;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(
				TABLE_CONVERSATIONS,
				null,
				C_COL_ID+"=?",
				new String[] {id},
				null,
				null,
				null
		);
		if (cursor != null && cursor.getCount() == 1) {
			cursor.moveToFirst();
			conversation = new Conversation();
			conversation.conversation_id = colStr(C_COL_ID, cursor);
			conversation.contact = colStr(C_COL_CONTACT, cursor);
			conversation.number = colStr(C_COL_NUMBER, cursor);
			conversation.modified = new Date(colInt(C_COL_MODIFIED, cursor));
			conversation.unseen = Boolean.parseBoolean(colStr(C_COL_UNSEEN, cursor));
			conversation.name = colStr(C_COL_NAME, cursor);
			conversation.count = colInt(C_COL_COUNT, cursor);
			conversation.draft = colStr(C_COL_DRAFT, cursor);
			
			conversation.messages = getMessages(conversation.conversation_id);
			cursor.close();
		}
		db.close();
		return conversation;
	}

	public ArrayList<Conversation> getConversations() {
		ArrayList<Conversation> conversations = new ArrayList<Conversation>();

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(
				TABLE_CONVERSATIONS,
				null,
				C_COL_COUNT+">?",
				new String[] { "0" },
				null,
				null,
				C_COL_MODIFIED + " DESC"
		);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				Conversation c = new Conversation();
				c.conversation_id = colStr(C_COL_ID, cursor);
				c.conversation_id = colStr(C_COL_ID, cursor);
				c.contact = colStr(C_COL_CONTACT, cursor);
				c.number = colStr(C_COL_NUMBER, cursor);
				c.modified = new Date(colInt(C_COL_MODIFIED, cursor));
				c.unseen = Boolean.parseBoolean(colStr(C_COL_UNSEEN, cursor));
				c.name = colStr(C_COL_NAME, cursor);
				c.count = colInt(C_COL_COUNT, cursor);
				c.draft = colStr(C_COL_DRAFT, cursor);
				
				c.messages = getMessages(c.conversation_id);
				conversations.add(c);
			} while (cursor.moveToNext());
		}

		if (cursor != null) {
			cursor.close();
		}
		db.close();
		return conversations;
	}
	
	public Boolean addMessage(SMSMessage message) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(S_COL_ID, message.sid);
		values.put(S_COL_CONVERSATION_ID, message.conversation_id);
		values.put(S_COL_ACCOUNT_SID, message.account_sid);
		values.put(S_COL_API_VERSION, message.api_version);
		values.put(S_COL_BODY, message.body);
		try {
			if (message.date_created != null) {
				values.put(S_COL_DATE_CREATED, sdf.parse(message.date_created).getTime());
			}
		} catch(ParseException e) {
			Log.e("addMesage", "Failed to parse date created "+message.date_created);
		}
		try {
			if (message.date_sent != null) {
				values.put(S_COL_DATE_SENT, sdf.parse(message.date_sent).getTime());
			}
		} catch (ParseException e1) {
			Log.e("addMesage", "Failed to parse date sent "+message.date_sent);
		}
		try {
			if (message.date_updated != null) {
				values.put(S_COL_DATE_UPDATED, sdf.parse(message.date_updated).getTime());
			}
		} catch (ParseException e1) {
			Log.e("addMesage", "Failed to parse date updated "+message.date_updated);
		}
		values.put(S_COL_DIRECTION, message.direction);
		values.put(S_COL_FROM, message.from);
		values.put(S_COL_PRICE, message.price);
		values.put(S_COL_SID, message.sid);
		values.put(S_COL_STATUS, message.status);
		values.put(S_COL_TO, message.to);
		values.put(S_COL_URI, message.uri);
		
		long rowId = -1;
		try {
			rowId = db.insert(TABLE_SMS, null, values);
		} catch (SQLiteConstraintException e) {
		}
		db.close();
		return rowId != -1;
	}
	
	public Boolean removeMessage(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		int numRows = db.delete(TABLE_SMS, S_COL_ID + "=?", new String[] { id });
		if (numRows == 0) {
			Log.w("ListDatabaseHelper", "Didn't delete any messages");
		}
		db.close();
		return numRows != 0;
	}
	
	public ArrayList<SMSMessage> getMessages(String conversation_id) {
		ArrayList<SMSMessage> messages = new ArrayList<SMSMessage>();
		if (conversation_id == null) {
			return messages;
		}

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(
				TABLE_SMS,
				null,
				S_COL_CONVERSATION_ID+"=?",
				new String[] {conversation_id},
				null,
				null,
				S_COL_DATE_UPDATED + " ASC"
		);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				SMSMessage sms = new SMSMessage();
				sms.sid = colStr(S_COL_ID, cursor);
				sms.conversation_id = colStr(S_COL_CONVERSATION_ID, cursor);
				sms.account_sid = colStr(S_COL_ACCOUNT_SID, cursor);
				sms.api_version = colStr(S_COL_API_VERSION, cursor);
				sms.body = colStr(S_COL_BODY, cursor);
				sms.date_created = colStr(S_COL_DATE_CREATED, cursor);
				sms.date_sent = colStr(S_COL_DATE_SENT, cursor);
				sms.date_updated = colStr(S_COL_DATE_UPDATED, cursor);
				sms.direction = colStr(S_COL_DIRECTION, cursor);
				sms.from = colStr(S_COL_FROM, cursor);
				sms.price = colStr(S_COL_PRICE, cursor);
				sms.status = colStr(S_COL_STATUS, cursor);
				sms.to = colStr(S_COL_TO, cursor);
				sms.uri = colStr(S_COL_URI, cursor);
				messages.add(sms);
			} while (cursor.moveToNext());
		}

		if (cursor != null) {
			cursor.close();
		}
		db.close();
		return messages;
	}
	
	// HELPERS
	private String colStr(String col, Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(col));
	}
	private Integer colInt(String col, Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(col));
	}
	
	// VITAL FUNCTIONS

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CONVERSATIONS_TABLE);
		db.execSQL(CREATE_SMS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE "+TABLE_CONVERSATIONS+" ADD "+C_COL_UNSEEN+" TEXT");
	}

}
