package com.mijibox.openfin.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

public class FinBeanUtils {

	static Jsonb jsonb;
	
	static {
		JsonbConfig config = new JsonbConfig().withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
			@Override
			public boolean isVisible(Field field) {
				return true;
			}

			@Override
			public boolean isVisible(Method method) {
				return true;
			}
		});
		jsonb = JsonbBuilder.create(config);
	}
	
	public static String toJsonString(Object obj) {
		return jsonb.toJson(obj);
	}
	
	public static JsonObject toJsonObject(Object obj) {
		return jsonb.fromJson(jsonb.toJson(obj), JsonObject.class);
	}
	
	private static void populateJsonObject(FinJsonBean obj, JsonObject jsonObj) {
		if (obj != null && obj.fromJson == null) {
			obj.fromJson = jsonObj;
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field f: fields) {
				try {
					f.setAccessible(true);
					Object fieldObj = f.get(obj);
					if (fieldObj != null && fieldObj instanceof FinJsonBean) {
						populateJsonObject((FinJsonBean) fieldObj, jsonObj.getJsonObject(f.getName()));
					}
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static <T> T fromJsonString(String json, Class<T> clazz) {
		T obj = jsonb.fromJson(json, clazz);
		if (obj instanceof FinJsonBean) {
			populateJsonObject((FinJsonBean) obj, jsonb.fromJson(json, JsonObject.class));
		}
		return obj;
	}

	public static <T> T fromJsonObject(JsonObject jsonObject, Class<T> clazz) {
		T obj = jsonb.fromJson(jsonObject.toString(), clazz);
		if (obj instanceof FinJsonBean) {
			populateJsonObject((FinJsonBean) obj, jsonObject);
		}
		return obj;
	}
}
