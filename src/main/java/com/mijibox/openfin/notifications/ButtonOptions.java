package com.mijibox.openfin.notifications;

import javax.json.JsonValue;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import com.mijibox.openfin.bean.FinJsonBean;

/**
 * Configuration options for constructing a button within a notification.
 * 
 * @author Anthony
 *
 */
public class ButtonOptions extends FinJsonBean {

	private JsonValue onClick;
	private String title;
	private String type;
	private String iconUrl;
	private Boolean cta;

	@JsonbCreator
	public ButtonOptions(@JsonbProperty("title") String title) {
		this.title = title;
	}

	public JsonValue getOnClick() {
		return onClick;
	}

	public void setOnClick(JsonValue onClick) {
		this.onClick = onClick;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public Boolean getCta() {
		return cta;
	}

	public void setCta(Boolean cta) {
		this.cta = cta;
	}

	public String getTitle() {
		return title;
	}
}
