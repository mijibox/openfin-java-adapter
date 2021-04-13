package com.mijibox.openfin.bean;

import javax.json.JsonObject;
import javax.json.JsonValue;

public class Ack extends FinJsonBean {

	private Boolean success;
	private JsonValue data;
	private String reason;
	private String action;
	private JsonObject error;

	public boolean isSuccess() {
		Boolean b = this.getSuccess();
		return b == null ? false : b.booleanValue();
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public JsonValue getData() {
		return data;
	}

	public void setData(JsonValue data) {
		this.data = data;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public JsonObject getError() {
		return error;
	}

	public void setError(JsonObject error) {
		this.error = error;
	}
}
