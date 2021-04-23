package com.mijibox.openfin.bean;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class ApplicationOptions extends WindowOptions {
	
	protected Boolean disableIabSecureLogging;
	protected Boolean fdc3Api;
	protected String loadErrorMessage;
	protected WindowOptions mainWindowOptions;
	protected Integer maxViewPoolSize;
	protected Boolean nonPersistent;
	protected Boolean plugins;
	protected Boolean spellCheck;
	protected String uuid;
	protected Boolean webSecurity;
	
	@JsonbCreator
	public ApplicationOptions(@JsonbProperty("uuid")String uuid) {
		this(uuid, null);
		
	}

	public ApplicationOptions(String uuid, String name) {
		super(name == null ? uuid : name);
		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
	}

	public WindowOptions getMainWindowOptions() {
		return mainWindowOptions;
	}

	public void setMainWindowOptions(WindowOptions mainWindowOptions) {
		this.mainWindowOptions = mainWindowOptions;
	}

	public Boolean getDisableIabSecureLogging() {
		return disableIabSecureLogging;
	}

	public void setDisableIabSecureLogging(Boolean disableIabSecureLogging) {
		this.disableIabSecureLogging = disableIabSecureLogging;
	}

	public Boolean getNonPersistent() {
		return nonPersistent;
	}

	public void setNonPersistent(Boolean nonPersistent) {
		this.nonPersistent = nonPersistent;
	}

	public Boolean getSpellCheck() {
		return spellCheck;
	}

	public void setSpellCheck(Boolean spellCheck) {
		this.spellCheck = spellCheck;
	}

	public Boolean getPlugins() {
		return plugins;
	}

	public void setPlugins(Boolean plugins) {
		this.plugins = plugins;
	}

	public Boolean getWebSecurity() {
		return webSecurity;
	}

	public void setWebSecurity(Boolean webSecurity) {
		this.webSecurity = webSecurity;
	}

	public Integer getMaxViewPoolSize() {
		return maxViewPoolSize;
	}

	public void setMaxViewPoolSize(Integer maxViewPoolSize) {
		this.maxViewPoolSize = maxViewPoolSize;
	}

	public String getLoadErrorMessage() {
		return loadErrorMessage;
	}

	public void setLoadErrorMessage(String loadErrorMessage) {
		this.loadErrorMessage = loadErrorMessage;
	}

	public Boolean getFdc3Api() {
		return fdc3Api;
	}

	public void setFdc3Api(Boolean fdc3Api) {
		this.fdc3Api = fdc3Api;
	}

}
