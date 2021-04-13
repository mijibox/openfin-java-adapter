package com.mijibox.openfin.bean;

import java.util.Map;

import javax.json.JsonObject;
import javax.json.bind.annotation.JsonbProperty;

public class RuntimeInfo extends FinJsonBean {
	private String architecture;
	private String manifestUrl;
	private String version;
	private Integer port;
	private String securityRealm;
	private String chromeVersion;
	private Map<String, Object> args;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getArchitecture() {
		return architecture;
	}

	public void setArchitecture(String architecture) {
		this.architecture = architecture;
	}

	public String getManifestUrl() {
		return manifestUrl;
	}

	public void setManifestUrl(String manifestUrl) {
		this.manifestUrl = manifestUrl;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getSecurityRealm() {
		return securityRealm;
	}

	public void setSecurityRealm(String securityRealm) {
		this.securityRealm = securityRealm;
	}

	public String getChromeVersion() {
		return chromeVersion;
	}

	public void setChromeVersion(String chromeVersion) {
		this.chromeVersion = chromeVersion;
	}

	public Map<String, Object> getArgs() {
		return args;
	}

	public void setArgs(Map<String, Object> args) {
		this.args = args;
	}
}
