package com.mijibox.openfin.bean;

import java.util.ArrayList;
import java.util.List;

public class LayoutContainer extends FinJsonBean {
	
	private List<LayoutItem> content;

	public LayoutContainer() {
	}
	
	public List<LayoutItem> getContent() {
		return content;
	}

	public void setContent(List<LayoutItem> content) {
		this.content = content;
	}
	
	public void add(LayoutItem item) {
		if (this.content == null) {
			this.content = new ArrayList<>();
		}
		this.content.add(item);
	}
	
	public void remove(LayoutItem item) {
		if (this.content != null) {
			this.remove(item);
		}
	}

}
