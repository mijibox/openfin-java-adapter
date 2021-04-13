package com.mijibox.openfin.bean;

public class ClearCacheOption extends FinJsonBean {

	private Boolean appcache;
	private Boolean cache;
	private Boolean cookies;
	private Boolean localStorage;

	public Boolean getAppcache() {
		return appcache;
	}

	public void setAppcache(Boolean appcache) {
		this.appcache = appcache;
	}

	public Boolean getCache() {
		return cache;
	}

	public void setCache(Boolean cache) {
		this.cache = cache;
	}

	public Boolean getCookies() {
		return cookies;
	}

	public void setCookies(Boolean cookies) {
		this.cookies = cookies;
	}

	public Boolean getLocalStorage() {
		return localStorage;
	}

	public void setLocalStorage(Boolean localStorage) {
		this.localStorage = localStorage;
	}
}
