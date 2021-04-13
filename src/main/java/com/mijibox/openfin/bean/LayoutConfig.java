package com.mijibox.openfin.bean;

import java.util.ArrayList;
import java.util.List;

public class LayoutConfig extends LayoutContainer {

	private LayoutSettings settings;
	private List<LayoutItem> content;

	public LayoutSettings getSettings() {
		return settings;
	}

	public void setSettings(LayoutSettings settings) {
		this.settings = settings;
	}

}
