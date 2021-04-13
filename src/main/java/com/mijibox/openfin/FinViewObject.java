package com.mijibox.openfin;

import java.util.concurrent.CompletionStage;

import javax.json.Json;
import javax.json.JsonObject;

import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.ViewOptions;

public class FinViewObject extends FinWebContent {
	
	public final static String EVENT_BLURRED = "blurred";
	public final static String EVENT_CREATED = "created";
	public final static String EVENT_DESTROYED = "destroyed";
	public final static String EVENT_FOCUSED = "focused";
	public final static String EVENT_HIDDEN = "hidden";
	public final static String EVENT_SHOWN = "shown";
	public final static String EVENT_TARGET_CHANGED = "target-changed";	

	FinViewObject(FinConnectionImpl finConnection, Identity identity) {
		super(finConnection, identity);
	}

	public CompletionStage<FinWindowObject> getCurrentWindow() {
		return this.finConnection.sendMessage("get-view-window", FinBeanUtils.toJsonObject(identity)).thenApply(ack -> {
			return new FinWindowObject(this.finConnection,
					FinBeanUtils.fromJsonObject(ack.getData().asJsonObject(), Identity.class));
		});
	}

	public CompletionStage<Boolean> addEventListener(String eventType, FinEventListener listener) {
		return this.finConnection._subscriptionManager.addListener(this.identity, "view", eventType, listener);
	}

	public CompletionStage<Boolean> removeEventListener(String eventType, FinEventListener listener) {
		return this.finConnection._subscriptionManager.removeListener(this.identity, "view", eventType, listener);
	}

	public CompletionStage<Void> attach(Identity target) {
		return this.finConnection
				.sendMessage("attach-view", Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity))
						.add("target", FinBeanUtils.toJsonObject(target)).build())
				.thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error attach-view, reason: " + ack.getReason());
					}
				});
	}

	public CompletionStage<Void> destroy() {
		return this.finConnection.sendMessage("destroy-view", FinBeanUtils.toJsonObject(this.identity))
				.thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error destroy-view, reason: " + ack.getReason());
					}
				});
	}

	public CompletionStage<Void> show() {
		return this.finConnection.sendMessage("show-view", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error show-view, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<Void> hide() {
		return this.finConnection.sendMessage("hide-view", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error hide-view, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<ViewOptions> getOptions() {
		return this.finConnection.sendMessage("get-view-options", FinBeanUtils.toJsonObject(this.identity)).thenApply(ack -> {
			if (ack.isSuccess()) {
				return FinBeanUtils.fromJsonObject(ack.getData().asJsonObject(), ViewOptions.class);
			}
			else {
				throw new RuntimeException("error get-view-options, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<Void> updateOptions(ViewOptions options) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity)).add("options", FinBeanUtils.toJsonObject(options)).build();
		return this.finConnection.sendMessage("update-view-options", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error update-view-options, reason: " + ack.getReason());
			}
		});
	}
	

}
