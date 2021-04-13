package com.mijibox.openfin.notifications;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import com.mijibox.openfin.bean.FinJsonBean;

public class NotificationIndicator extends FinJsonBean {
	public final static String TYPE_FAILURE = "failure";
	public final static String TYPE_SUCCESS = "success";
	public final static String TYPE_WARNING = "warning";

	private String type;
	private String text;

	/**
	 * Indicates the semantic intent behind the indicator - this determines the
	 * visual styling of the indicator when seen by the user.
	 * 
	 * @param type
	 *            indicator type can be one of the following: {@link #TYPE_FAILURE},
	 *            {@link #TYPE_SUCCESS}, {@link #TYPE_WARNING}
	 */
	@JsonbCreator
	public NotificationIndicator(@JsonbProperty("type")String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
