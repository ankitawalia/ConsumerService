package com.klarna.consumer.util;

public class ConsumerKeyIndex {
	
	private String id;
	
	private String emailId;
	
	public ConsumerKeyIndex(final String id, final String emailId) {
		this.id = id;
		this.emailId = emailId;
	}

	@Override
	public int hashCode() {
		return 100;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConsumerKeyIndex other = (ConsumerKeyIndex) obj;
		if (emailId == null && id == null) {
			return false;
		} else if(emailId == null && id != null){
			if (id.equals(other.id))
				return true;
		} else if(emailId != null && id == null){
			if (emailId.equals(other.emailId))
				return true;
		} else if(emailId != null && id != null){
			if (emailId.equals(other.emailId) && id.equals(other.id))
				return true;
		}
		
		return false;
	}

	public String getId() {
		return id;
	}

	public String getEmailId() {
		return emailId;
	}
	
	

}
