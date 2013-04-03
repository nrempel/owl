package com.gregschier.sms.classes;

public class SMSMessage implements APIResource {
	public String account_sid;
	public String api_version;
	public String body;
	public String date_created;
	public String date_sent;
	public String date_updated;
	public String direction;
	public String from;
	public String price;
	public String sid;
	public String status;
	public String to;
	public String uri;
	
	// EXTRA PROPERTIES
	public String conversation_id;
	public String read_date;
}
