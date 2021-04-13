package com.mijibox.openfin.bean;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class EntityTypeSerializer implements JsonbSerializer<EntityType> {

	@Override
	public void serialize(EntityType type, JsonGenerator generator, SerializationContext ctx) {
		
		switch (type) {
		case WINDOW:
			generator.write("window");
			break;
		case IFRAME:
			generator.write("iframe");
			break;
		case EXTERNAL:
			generator.write("external connection");
			break;
		case VIEW:
			generator.write("view");
			break;
		case UNKNOWN:
			generator.write("unknown");
			break;
		}
	}
}

