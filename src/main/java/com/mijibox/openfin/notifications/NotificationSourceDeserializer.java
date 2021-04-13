package com.mijibox.openfin.notifications;

import java.lang.reflect.Type;

import javax.json.JsonObject;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import com.mijibox.openfin.bean.FinBeanUtils;

public class NotificationSourceDeserializer implements JsonbDeserializer<NotificationSource> {

	@Override
	public NotificationSource deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
		JsonObject sourceJson = parser.getObject();
		String sourceType = sourceJson.getString("type");
		if ("desktop".equals(sourceType)) {
			return FinBeanUtils.fromJsonObject(sourceJson, NotificationSourceDesktop.class);
		}
		else {
			return FinBeanUtils.fromJsonObject(sourceJson, NotificationSourceFeed.class);
		}
	}

}
