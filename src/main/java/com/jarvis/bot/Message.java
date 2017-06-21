package com.jarvis.bot;

public class Message {
	private String userId;
	private String message;
	private long timestamp;
	
	public Message(String userId, String message){
		this.userId=userId;
		this.message=message;
		this.timestamp=System.currentTimeMillis();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
