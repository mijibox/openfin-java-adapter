package com.mijibox.openfin.bean;

import java.lang.reflect.Type;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

public class EntityTypeDeserializer implements JsonbDeserializer<EntityType> {
	@Override
	public EntityType deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
		String entityType = parser.getString();
		EntityType type = EntityType.UNKNOWN;
		switch (entityType) {
		case "window":
			type = EntityType.WINDOW;
			break;
		case "iframe":
			type = EntityType.IFRAME;
			break;
		case "external connection":
			type = EntityType.EXTERNAL;
			break;
		case "view":
			type = EntityType.VIEW;
			break;
		}
		return type;
	}
}
