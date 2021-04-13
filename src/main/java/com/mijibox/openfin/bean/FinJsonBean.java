package com.mijibox.openfin.bean;

import javax.json.JsonObject;
import javax.json.bind.annotation.JsonbTransient;

public class FinJsonBean {
	@JsonbTransient
	protected JsonObject fromJson;

	/**
	 * Get the original JsonObject that this bean was deserialized from.
	 * 
	 * @return JsonObject
	 */
	@JsonbTransient
	public JsonObject getFromJson() {
		return this.fromJson;
	}

	@Override
	public String toString() {
		return FinBeanUtils.toJsonString(this);
	}
}
