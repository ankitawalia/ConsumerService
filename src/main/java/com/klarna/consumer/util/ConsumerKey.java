package com.klarna.consumer.util;

public class ConsumerKey {
	
	private String id;
	
	private String emailId;
	
	public ConsumerKey(final String id, final String emailId) {
		this.id = id;
		this.emailId = emailId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((emailId == null) ? 0 : emailId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ConsumerKey other = (ConsumerKey) obj;
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
	
	

}
