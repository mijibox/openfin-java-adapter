package com.mijibox.openfin.bean;

import java.util.Objects;

public class ClientIdentity extends Identity {
	private String endpointId;

	public String getEndpointId() {
		return endpointId;
	}

	public void setEndpointId(String endpointId) {
		this.endpointId = endpointId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, uuid, endpointId);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ClientIdentity other = (ClientIdentity) obj;
		return Objects.equals(this.name, other.name) 
				&& Objects.equals(this.uuid, other.uuid)
				&& Objects.equals(this.endpointId, other.endpointId);
	}
}
