package com.mijibox.openfin.bean;

import java.util.Objects;

import javax.json.bind.annotation.JsonbProperty;

public class ProviderIdentity extends Identity {
	protected String channelId;
	protected String channelName;
	@JsonbProperty("isExternal")
	protected Boolean external;

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public boolean isExternal() {
		return this.getExternal() == null ? false : this.getExternal().booleanValue();
	}

	public Boolean getExternal() {
		return external;
	}

	public void setExternal(Boolean external) {
		this.external = external;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, uuid, channelId, channelName, external);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ProviderIdentity other = (ProviderIdentity) obj;
		return Objects.equals(this.name, other.name) 
				&& Objects.equals(this.uuid, other.uuid)
				&& Objects.equals(this.channelId, other.channelId)
				&& Objects.equals(this.channelName, other.channelName)
				&& Objects.equals(this.external, other.external);
	}


}
