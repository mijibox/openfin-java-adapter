package com.mijibox.openfin.bean;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class LayoutItem extends LayoutContainer {

	public final static String TYPE_ROW = "row";
	public final static String TYPE_COLUMN = "column";
	public final static String TYPE_STACK = "stack";
	public final static String TYPE_COMPONENT = "component";

	private String type;
	private String componentName;
	private ViewOptions componentState;
	private List<LayoutItem> content;
	
	@JsonbCreator
	public LayoutItem(@JsonbProperty("type")String type) {
		this.type = type;
		if (TYPE_COMPONENT.equals(type)) {
			this.componentName = "view";
		}
	}

	public String getType() {
		return type;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public ViewOptions getComponentState() {
		return componentState;
	}

	public void setComponentState(ViewOptions componentState) {
		this.componentState = componentState;
	}
}
