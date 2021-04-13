package com.mijibox.openfin.bean;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class Service extends FinJsonBean {

	private String name;
	private String manifestUrl;
	
	@JsonbCreator
	public Service(@JsonbProperty("name")String name) {
		this.name = name;
	}

	public Service(String name, String manifestUrl) {
		this.name = name;
		this.manifestUrl = manifestUrl;
	}

	public String getName() {
		return name;
	}

	public String getManifestUrl() {
		return manifestUrl;
	}

	public void setManifestUrl(String manifestUrl) {
		this.manifestUrl = manifestUrl;
	}

}
