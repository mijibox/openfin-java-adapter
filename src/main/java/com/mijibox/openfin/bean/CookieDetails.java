package com.mijibox.openfin.bean;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class CookieDetails {
	
	public final static String SAME_SITE_UNSPECIFIED = "unspecified";
	public final static String SAME_SITE_NO_RESTRICTION = "no_restriction";
	public final static String SAME_SITE_LAX = "lax";
	public final static String SAME_SITE_STRICT = "strict";
	
	private Integer ttl;
	private String url;
	private String value;
	private String domain;
	private String path;
	private Boolean secure;
	private Boolean httpOnly;
	private String sameSite;
	private String name;

	@JsonbCreator
	public CookieDetails(@JsonbProperty("url")String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTtl() {
		return ttl;
	}

	public void setTtl(Integer ttl) {
		this.ttl = ttl;
	}

	public String getUrl() {
		return url;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Boolean getSecure() {
		return secure;
	}

	public void setSecure(Boolean secure) {
		this.secure = secure;
	}

	public Boolean getHttpOnly() {
		return httpOnly;
	}

	public void setHttpOnly(Boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	public String getSameSite() {
		return sameSite;
	}

	public void setSameSite(String sameSite) {
		this.sameSite = sameSite;
	}

}
