package com.gregschier.simplesms;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gregschier.sms.classes.Contact;
import com.gregschier.sms.classes.Conversation;
import com.gregschier.sms.classes.SMSMessage;
import com.gregschier.sms.helpers.ContactHelper;

public class ListAdapterConversationList extends ArrayAdapter<Conversation> {

	private Context context;
	private ArrayList<Conversation> conversations;

	public ListAdapterConversationList(Context context, ArrayList<Conversation> conversations) {
		super(context, R.layout.conversation_list_row, conversations);
		this.conversations = conversations;
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.conversation_list_view_row, parent, false);

		TextView tv_body = (TextView) rowView.findViewById(R.id.tv_last_message);
		TextView tv_count = (TextView) rowView.findViewById(R.id.tv_count);
		TextView tv_contact = (TextView) rowView.findViewById(R.id.tv_contact);
		ImageView iv_photo = (ImageView) rowView.findViewById(R.id.iv_cover);
		Conversation conversation = conversations.get(position);

		ContactHelper contactHelper = ContactHelper.getInstance(getContext());

		if (conversation != null && conversation.messages != null && conversation.messages.size() > 0) {
			Contact contact = contactHelper.getContact(conversation.number);
			
			int lastIndex = conversation.messages.size() - 1;
			SMSMessage lastMessage = conversation.messages.get(lastIndex);
			
			tv_body.setText(lastMessage.body);
			
			if (conversation.draft != null && conversation.draft.length() > 0) {
				tv_contact.setText(contact.name+" (Draft)");
			} else {
				tv_contact.setText(contact.name);
			}
				tv_count.setText(conversation.count.toString());
			
			if (contact.photoUri != null) {
				iv_photo.setImageURI(contact.photoUri);
			}
			
			if (conversation.unseen) {
				rowView.setBackgroundColor(Color.WHITE);
				tv_count.setTypeface(null, Typeface.BOLD);
				tv_contact.setTypeface(null, Typeface.BOLD);
			} else {
				tv_count.setTypeface(null, Typeface.NORMAL);
				tv_contact.setTypeface(null, Typeface.NORMAL);
			}
		}

		return rowView;
	}

}
