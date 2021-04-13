package com.mijibox.openfin.bean;

import java.util.Date;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class DateSerializer implements JsonbSerializer<Date> {

	@Override
	public void serialize(Date obj, JsonGenerator generator, SerializationContext ctx) {
		generator.write(obj.getTime());
	}

}

