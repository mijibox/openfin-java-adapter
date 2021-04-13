package com.mijibox.openfin.bean;

import javax.json.JsonValue;

public class ChannelConnectionOptions extends FinJsonBean {
	private JsonValue payload;
	private Boolean wait;
	
	public ChannelConnectionOptions() {
		
	}
	
	public ChannelConnectionOptions(boolean wait, JsonValue payload) {
		this.wait = wait;
		this.payload = payload;
	}

	public JsonValue getPayload() {
		return payload;
	}

	public void setPayload(JsonValue payload) {
		this.payload = payload;
	}

	public Boolean getWait() {
		return wait;
	}

	public void setWait(Boolean wait) {
		this.wait = wait;
	}

	public boolean isWait() {
		return this.wait == null ? true : this.wait.booleanValue();
	}

}
