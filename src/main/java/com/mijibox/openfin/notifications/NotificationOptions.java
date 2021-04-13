package com.mijibox.openfin.notifications;

import java.util.Date;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.json.bind.annotation.JsonbTypeSerializer;

import com.mijibox.openfin.bean.DateDeserializer;
import com.mijibox.openfin.bean.DateSerializer;
import com.mijibox.openfin.bean.FinJsonBean;

/**
 * Configuration options for constructing a Notifications object.
 * 
 * @author Anthony
 *
 */
public class NotificationOptions extends FinJsonBean {

	public static final String STICKY_STICKY = "sticky";
	public static final String STICKY_TRANSIENT = "transient";

	private NotificationIndicator indicator;
	private ButtonOptions[] buttons;
	private JsonValue onClose;
	private JsonValue onExpire;
	private JsonValue onSelect;

	private String title;
	private String body;
	private String category;
	@JsonbTypeSerializer(DateSerializer.class)
	@JsonbTypeDeserializer(DateDeserializer.class)
	private Date date;
	@JsonbTypeSerializer(DateSerializer.class)
	@JsonbTypeDeserializer(DateDeserializer.class)
	private Date expires;
	private String id;
	private String icon;
	private String sticky;
	private JsonObject customData;

	@JsonbCreator
	public NotificationOptions(@JsonbProperty("title") String title, @JsonbProperty("body") String body,
			@JsonbProperty("category") String category) {
		this.title = title;
		this.body = body;
		this.category = category;
	}

	public NotificationIndicator getIndicator() {
		return indicator;
	}

	public void setIndicator(NotificationIndicator indicator) {
		this.indicator = indicator;
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public String getCategory() {
		return category;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getSticky() {
		return sticky;
	}

	public void setSticky(String sticky) {
		this.sticky = sticky;
	}

	public JsonObject getCustomData() {
		return customData;
	}

	public void setCustomData(JsonObject customData) {
		this.customData = customData;
	}

	public ButtonOptions[] getButtons() {
		return buttons;
	}

	public void setButtons(ButtonOptions... buttons) {
		this.buttons = buttons;
	}

	public JsonValue getOnClose() {
		return onClose;
	}

	public void setOnClose(JsonValue onClose) {
		this.onClose = onClose;
	}

	public JsonValue getOnExpire() {
		return onExpire;
	}

	public void setOnExpire(JsonValue onExpire) {
		this.onExpire = onExpire;
	}

	public JsonValue getOnSelect() {
		return onSelect;
	}

	public void setOnSelect(JsonValue onSelect) {
		this.onSelect = onSelect;
	}

	
}
