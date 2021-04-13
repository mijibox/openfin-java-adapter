package com.mijibox.openfin.bean;

import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.json.bind.annotation.JsonbTypeSerializer;

@JsonbTypeSerializer(EntityTypeSerializer.class)
@JsonbTypeDeserializer(EntityTypeDeserializer.class)
public enum EntityType {
	WINDOW, 
	IFRAME, 
	EXTERNAL, 
	VIEW, 
	UNKNOWN;
}
