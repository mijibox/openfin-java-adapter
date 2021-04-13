package com.mijibox.openfin.bean;

public class FrameInfo extends Identity {

	private EntityType entityType;
	private Identity parent;

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public Identity getParent() {
		return parent;
	}

	public void setParent(Identity parent) {
		this.parent = parent;
	}

}
