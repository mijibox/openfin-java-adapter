package com.mijibox.openfin.bean;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class Hotkey extends FinJsonBean {

	private String keys;
	private Boolean preventDefault;

	@JsonbCreator
	public Hotkey(@JsonbProperty("keys") String keys) {
		this.keys = keys;
	}

	public Boolean getPreventDefault() {
		return preventDefault;
	}

	public void setPreventDefault(Boolean preventDefault) {
		this.preventDefault = preventDefault;
	}

	public String getKeys() {
		return keys;
	}

}
