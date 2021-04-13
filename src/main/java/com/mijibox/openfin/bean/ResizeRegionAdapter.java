package com.mijibox.openfin.bean;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.adapter.JsonbAdapter;

import com.mijibox.openfin.bean.ResizeRegion.Side;

public class ResizeRegionAdapter implements JsonbAdapter<ResizeRegion, JsonObject> {

	@Override
	public JsonObject adaptToJson(ResizeRegion obj) throws Exception {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (obj.getBottomRightCorner() != null) {
			builder.add("bottomRightCorner", obj.getBottomRightCorner());
		}
		if (obj.getSize() != null) {
			builder.add("size", obj.getSize());
		}
		JsonObjectBuilder sidesBuilder = Json.createObjectBuilder();
		if (obj.getResizableSide(Side.Top) != null) {
			sidesBuilder.add("top", obj.getResizableSide(Side.Top));
		}
		if (obj.getResizableSide(Side.Bottom) != null) {
			sidesBuilder.add("bottom", obj.getResizableSide(Side.Bottom));
		}
		if (obj.getResizableSide(Side.Left) != null) {
			sidesBuilder.add("left", obj.getResizableSide(Side.Left));
		}
		if (obj.getResizableSide(Side.Right) != null) {
			sidesBuilder.add("right", obj.getResizableSide(Side.Right));
		}
		builder.add("sides", sidesBuilder.build());
		return builder.build();
	}

	@Override
	public ResizeRegion adaptFromJson(JsonObject obj) throws Exception {
		ResizeRegion resizeRegion = new ResizeRegion();
		if (obj.containsKey("bottomRightCorner")) {
			resizeRegion.setBottomRightCorner(obj.getInt("bottomRightCorner"));
		}
		if (obj.containsKey("size")) {
			resizeRegion.setSize(obj.getInt("size"));
		}
		if (obj.containsKey("sides")) {
			JsonObject sidesObj = obj.getJsonObject("sides");
			if (sidesObj.containsKey("top")) {
				resizeRegion.setResizableSide(Side.Top, sidesObj.getBoolean("top"));
			}
			if (sidesObj.containsKey("bottom")) {
				resizeRegion.setResizableSide(Side.Bottom, sidesObj.getBoolean("bottom"));
			}
			if (sidesObj.containsKey("left")) {
				resizeRegion.setResizableSide(Side.Left, sidesObj.getBoolean("left"));
			}
			if (sidesObj.containsKey("right")) {
				resizeRegion.setResizableSide(Side.Right, sidesObj.getBoolean("right"));
			}
		}
		return resizeRegion;
	}

}
