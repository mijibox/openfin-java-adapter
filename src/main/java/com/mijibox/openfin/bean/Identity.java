package com.mijibox.openfin.bean;

import java.util.Objects;

public class Identity extends FinJsonBean {
	protected String name;
	protected String uuid;
	
	public Identity() {
	}
	
	public Identity(String uuid) {
		this.uuid = uuid;
	}

	public Identity(String uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Identity other = (Identity) obj;
		return Objects.equals(this.name, other.name) 
				&& Objects.equals(this.uuid, other.uuid);
	}
}
