package com.mijibox.openfin.bean;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

public class Runtime extends FinJsonBean {
	
	private String version;
	@JsonbTransient
	private String[] arguments;
	private String fallbackVersion;
	private String futureVersion;
	private Boolean forceLatest;
	private Boolean enableCacheMigration;

	public Runtime() {
	}

	public String getVersion() {
		if (version == null) {
			version = "stable";
		}
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String[] getArguments() {
		return arguments;
	}

	public void setArguments(String ...arguments) {
		this.arguments = arguments;
	}

	@JsonbProperty("arguments")
	public String getArgumentsAsString() {
		if (this.arguments != null) {
			StringBuilder sb = new StringBuilder();
			for (String arg : this.arguments) {
				sb.append(arg).append(" ");
			}
			return sb.toString();
		}
		else {
			return null;
		}
	}

	public String getFallbackVersion() {
		return fallbackVersion;
	}

	public void setFallbackVersion(String fallbackVersion) {
		this.fallbackVersion = fallbackVersion;
	}

	public String getFutureVersion() {
		return futureVersion;
	}

	public void setFutureVersion(String futureVersion) {
		this.futureVersion = futureVersion;
	}

	public Boolean getForceLatest() {
		return forceLatest;
	}

	public void setForceLatest(Boolean forceLatest) {
		this.forceLatest = forceLatest;
	}

	public Boolean getEnableCacheMigration() {
		return enableCacheMigration;
	}

	public void setEnableCacheMigration(Boolean enableCacheMigration) {
		this.enableCacheMigration = enableCacheMigration;
	}
}
