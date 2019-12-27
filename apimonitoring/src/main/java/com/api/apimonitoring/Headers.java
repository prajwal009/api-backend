package com.api.apimonitoring;

public class Headers {
	
	private String headerKey;
	private String headerValue;
	public String getHeaderKey() {
		return headerKey;
	}
	public void setHeaderKey(String headerKey) {
		this.headerKey = headerKey;
	}
	public String getHeaderValue() {
		return headerValue;
	}
	public void setHeaderValue(String headerValue) {
		this.headerValue = headerValue;
	}
	public Headers() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Headers(String headerKey, String headerValue) {
		super();
		this.headerKey = headerKey;
		this.headerValue = headerValue;
	}
	@Override
	public String toString() {
		return "Headers [headerKey=" + headerKey + ", headerValue=" + headerValue + "]";
	}
	
	

}
