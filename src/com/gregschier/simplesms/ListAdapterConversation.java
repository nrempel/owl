package com.gregschier.simplesms;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gregschier.sms.classes.Contact;
import com.gregschier.sms.classes.SMSMessage;
import com.gregschier.sms.helpers.ContactHelper;

public class ListAdapterConversation extends ArrayAdapter<SMSMessage> {

	private Context context;
	private ArrayList<SMSMessage> messages;

	public ListAdapterConversation(Context context, ArrayList<SMSMessage> messages) {
		super(context, R.layout.conversation_list_row, messages);
		Log.i("ListAdapterConversation", "Num Messages: "+messages.size());
		this.messages = messages;
		this.context = context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.conversation_list_row, parent, false);
		} else {
			rowView = convertView;
		}
		
		TextView tv_body = (TextView) rowView.findViewById(R.id.tv_last_message);
		TextView tv_contact = (TextView) rowView.findViewById(R.id.tv_contact);
		ImageView iv_photo = (ImageView) rowView.findViewById(R.id.iv_cover);
		SMSMessage sms = messages.get(position);
		
		if (sms.direction.equals("inbound")) {
			
		} else {
			
		}
		
		ContactHelper contactHelper = ContactHelper.getInstance(getContext());
		
		Contact contact = contactHelper.getContact(sms.from);
		
		tv_body.setText(sms.body);
		tv_contact.setText(contact.name);
		if (contact.photoUri != null) {
			iv_photo.setImageURI(contact.photoUri);
		}
		
		return rowView;
	}

}
