package com.mijibox.openfin.bean;

public class PreloadScript extends FinJsonBean {

	public final static String STATE_LOAD_STARTED = "load-started";
	public final static String STATE_LOAD_FAILED = "load-failed";
	public final static String STATE_LOAD_SUCCEEDED = "load-succeeded";
	public final static String STATE_FAILED = "failed";
	public final static String STATE_SUCCEEDED = "succeeded";

	private Boolean mandatory;
	private String state;
	private String url;

	public Boolean getMandatory() {
		return mandatory;
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
