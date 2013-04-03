package com.gregschier.sms.helpers;

import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

import com.gregschier.sms.classes.Contact;

public class ContactHelper {
	
	private static ContactHelper instance = null;
	private Context context;
	private HashMap<String, Contact> contactCache = new HashMap<String, Contact>();
	
	private ContactHelper(Context context) {
		this.context = context;
	}
	
	public static ContactHelper getInstance(Context context) { 
		if (instance == null) {
			instance = new ContactHelper(context);
		}
		return instance;
	}

	public Contact getContact(String number) {
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		Contact contact = contactCache.get(number);
		if (contact != null) {
//			Log.i("ContactHelper", "Contact Cache Hit");
			return contact;
		} else {
			contact = new Contact();
			contact.number = number;
//			Log.i("ContactHelper", "Contact Cache Miss");
		}

		ContentResolver contentResolver = context.getContentResolver();
		Cursor contactLookup = contentResolver.query(uri, new String[] { BaseColumns._ID,
				ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

		try {
			if (contactLookup != null && contactLookup.getCount() > 0) {
				contactLookup.moveToNext();
				contact.name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
				contact.id = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
				contact.photoUri = getPhotoUri(contact.id);
			}
		} finally {
			if (contactLookup != null) {
				contactLookup.close();
			}
		}
		contactCache.put(number, contact);
		if (contact.name == null) {
			contact.name = number;
		}

		return contact;
	}
	
	public Uri getPhotoUri(String contactId) {
	    try {
	        Cursor cur = context.getContentResolver().query(
	                ContactsContract.Data.CONTENT_URI,
	                null,
	                ContactsContract.Data.CONTACT_ID + "=" + contactId + " AND "
	                        + ContactsContract.Data.MIMETYPE + "='"
	                        + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
	                null);
	        if (cur != null) {
	            if (!cur.moveToFirst()) {
	                return null; // no photo
	            }
	            cur.close();
	        } else {
	            return null; // error in cursor process
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
	    return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
	}
}
