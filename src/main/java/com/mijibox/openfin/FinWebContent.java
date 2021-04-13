package com.mijibox.openfin;

import java.util.concurrent.CompletionStage;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.CapturePageOptions;
import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.FindInPageOptions;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.PrintOptions;
import com.mijibox.openfin.bean.PrinterInfo;

public class FinWebContent extends FinInstanceObject {
	private final static Logger logger = LoggerFactory.getLogger(FinWebContent.class);

	public enum FindInPageAction {
		CLEAR_SELECTION("clearSelection"),
		KEEP_SELECTION("keepSelection"),
		ACTIVATE_SELECTION("activateSelection");

		private String action;

		FindInPageAction(String action) {
			this.action = action;
		}
		
		@Override
		public String toString() {
			return this.action;
		}
	}

	FinWebContent(FinConnectionImpl finConnection, Identity identity) {
		super(finConnection, identity);
	}

	public CompletionStage<String> capturePage(CapturePageOptions options) {
		JsonObjectBuilder builder = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity));
		if (options != null) {
			builder.add("options", FinBeanUtils.toJsonObject(options));
		}
		return this.finConnection
				.sendMessage("capture-page", builder.build())
				.thenApply(ack -> {
					if (ack.isSuccess()) {
						return ((JsonString) ack.getData()).getString();
					}
					else {
						throw new RuntimeException("error execute-javascript-in-window, reason: " + ack.getReason());
					}
				});
	}

	/**
	 * Executes Javascript on the window, restricted to windows you own or windows
	 * owned by applications you have created.
	 * 
	 * @param code
	 * @return
	 */
	public CompletionStage<JsonValue> executeJavaScript(String code) {
		return this.finConnection
				.sendMessage("execute-javascript-in-window",
						Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity)).add("code", code).build())
				.thenApply(ack -> {
					if (ack.isSuccess()) {
						return ack.getData();
					}
					else {
						throw new RuntimeException("error execute-javascript-in-window, reason: " + ack.getReason());
					}
				});
	}

	public CompletionStage<Void> findInPage(String searchTerm, FindInPageOptions options) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity))
				.add("searchTerm", searchTerm)
				.add("options", options == null ? JsonValue.NULL : FinBeanUtils.toJsonObject(options)).build();
		return this.finConnection.sendMessage("find-in-page", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error find-in-page, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<Void> focus() {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity))
				.add("emitSynthFocused", true).build();
		return this.finConnection.sendMessage("focus-window", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error focus-window, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<PrinterInfo[]> getPrinters() {
		return this.finConnection.sendMessage("get-printers", FinBeanUtils.toJsonObject(this.identity)).thenApply(ack -> {
			if (ack.isSuccess()) {
				JsonArray printers = ack.getData().asJsonArray();
				PrinterInfo[] printerInfos = new PrinterInfo[printers.size()];
				for (int i = 0; i < printerInfos.length; i++) {
					printerInfos[i] = FinBeanUtils.fromJsonObject(printers.get(i).asJsonObject(), PrinterInfo.class);
				}
				return printerInfos;
			}
			else {
				throw new RuntimeException("error get-printers, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<Integer> getZoomLevel() {
		return this.finConnection.sendMessage("get-zoom-level", FinBeanUtils.toJsonObject(this.identity))
				.thenApply(ack -> {
					if (ack.isSuccess()) {
						return ((JsonNumber) ack.getData()).intValue();
					}
					else {
						throw new RuntimeException("error get-zoom-level, reason: " + ack.getReason());
					}
				});
	}

	public CompletionStage<Void> navigateBack() {
		return this.finConnection.sendMessage("navigate-window-back", FinBeanUtils.toJsonObject(this.identity))
				.thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error navigate-window-back, reason: " + ack.getReason());
					}
				});
	}

	public CompletionStage<Void> navigateForward() {
		return this.finConnection.sendMessage("navigate-window-forward", FinBeanUtils.toJsonObject(this.identity))
				.thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error navigate-window-forward, reason: " + ack.getReason());
					}
				});
	}

	public CompletionStage<Void> navigate(String url) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity)).add("url", url).build();
		return this.finConnection.sendMessage("navigate-window", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error navigate, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<Void> print(PrintOptions options) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity))
				.add("options", options == null ? JsonValue.NULL : FinBeanUtils.toJsonObject(options)).build();
		return this.finConnection.sendMessage("print", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error print, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<Void> reload(boolean ignoreCache) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity))
				.add("ignoreCache", ignoreCache).build();
		return this.finConnection.sendMessage("reload-window", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error reload-window, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<Void> setZoomLevel(int zoomLevel) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(identity)).add("level", zoomLevel)
				.build();
		return this.finConnection.sendMessage("set-zoom-level", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error set-zoom-level, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<Void> showDeveloperTools() {
		return this.finConnection._system.showDeveloperTools(this.identity);
	}

	public CompletionStage<Void> stopFindInPage(FindInPageAction action) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity)).add("action", action.toString())
				.build();
		return this.finConnection.sendMessage("stop-find-in-page", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error stop-find-in-page, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<Void> stopNavigation() {
		return this.finConnection.sendMessage("stop-window-navigation", FinBeanUtils.toJsonObject(this.identity))
				.thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error stop-window-navigation, reason: " + ack.getReason());
					}
				});
	}
}
