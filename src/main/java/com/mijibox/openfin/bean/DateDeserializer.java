package com.mijibox.openfin.bean;

import java.lang.reflect.Type;
import java.util.Date;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

public class DateDeserializer implements JsonbDeserializer<Date> {
	@Override
	public Date deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
		long dateInMills = parser.getLong();
		return new Date(dateInMills);
	}
}
