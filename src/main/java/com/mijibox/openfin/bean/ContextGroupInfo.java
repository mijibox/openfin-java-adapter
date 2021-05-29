package com.mijibox.openfin.bean;

public class ContextGroupInfo extends FinJsonBean {
	
	private String id;
	private DisplayMetadata displayMetadata;

	public ContextGroupInfo() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DisplayMetadata getDisplayMetadata() {
		return displayMetadata;
	}

	public void setDisplayMetadata(DisplayMetadata displayMetadata) {
		this.displayMetadata = displayMetadata;
	}

}
