package com.mijibox.openfin.bean;

import java.util.Objects;

public class RoutingInfo extends ProviderIdentity {
	private String endpointId;

	public String getEndpointId() {
		return endpointId;
	}

	public void setEndpointId(String endpointId) {
		this.endpointId = endpointId;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, uuid, channelId, channelName, isExternal, endpointId);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RoutingInfo other = (RoutingInfo) obj;
		return Objects.equals(this.name, other.name) 
				&& Objects.equals(this.uuid, other.uuid)
				&& Objects.equals(this.channelId, other.channelId)
				&& Objects.equals(this.channelName, other.channelName)
				&& Objects.equals(this.endpointId, other.endpointId)
				&& Objects.equals(this.isExternal, other.isExternal);
	}

}
