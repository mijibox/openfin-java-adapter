package com.mijibox.openfin.fdc3.context;

import javax.json.JsonObject;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class Context {
	private String type;
	private JsonObject id;
	private String name;
	
	@JsonbCreator
	public Context(@JsonbProperty("type")String type) {
		this.type = type;
	}

	public JsonObject getId() {
		return id;
	}

	public void setId(JsonObject id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

}
