package com.gregschier.sms.classes;

import java.util.ArrayList;
import java.util.Date;

public class Conversation {
	public String conversation_id;
	public String contact;
	public String number;
	public String name;
	public String draft;
	public Date modified;
	public Integer count;
	public Boolean unseen;
	
	public ArrayList<SMSMessage> messages;
}
