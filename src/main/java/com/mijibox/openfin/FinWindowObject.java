package com.mijibox.openfin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.Bounds;
import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.FrameInfo;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.WindowBounds;
import com.mijibox.openfin.bean.WindowInfo;
import com.mijibox.openfin.bean.WindowOptions;
import com.mijibox.openfin.bean.WindowUpdatableOptions;

public class FinWindowObject extends FinWebContent {
	private final static Logger logger = LoggerFactory.getLogger(FinWindowObject.class);

	public enum Anchor {
		TOP_LEFT("top-left"),
		TOP_RIGHT("top-right"),
		BOTTOM_LEFT("bottom-left"),
		BOTTOM_RIGHT("bottom-right");

		private String anchor;

		Anchor(String anchor) {
			this.anchor = anchor;
		}
		
		@Override
		public String toString() {
			return this.anchor;
		}
	}

	public final static String EVENT_AUTH_REQUESTED = "auth-requested";
	public final static String EVENT_BEGIN_USER_BOUNDS_CHANGING = "begin-user-bounds-changing";
	public final static String EVENT_BLURRED = "blurred";
	public final static String EVENT_BOUNDS_CHANGED = "bounds-changed";
	public final static String EVENT_BOUNDS_CHANGING = "bounds-changing";
	public final static String EVENT_CLOSE_REQUESTED = "close-requested";
	public final static String EVENT_CLOSED = "closed";
	public final static String EVENT_CLOSING = "closing";
	public final static String EVENT_CRASHED = "crashed";
	public final static String EVENT_DISABLED_FRAME_BOUNDS_CHANGED = "disabled-frame-bounds-changed";
	public final static String EVENT_DISABLED_FRAME_BOUNDS_CHANGING = "disabled-frame-bounds-changing";
	public final static String EVENT_EMBEDDED = "embedded";
	public final static String EVENT_END_USER_BOUNDS_CHANGING = "end-user-bounds-changing";
	public final static String EVENT_EXTERNAL_PROCESS_EXITED = "external-process-exited";
	public final static String EVENT_EXTERNAL_PROCESS_STARTED = "external-process-started";
	public final static String EVENT_FILE_DOWNLOAD_COMPLETED = "file-download-completed";
	public final static String EVENT_FILE_DOWNLOAD_PROGRESS = "file-download-progress";
	public final static String EVENT_FILE_DOWNLOAD_STARTED = "file-download-started";
	public final static String EVENT_FOCUSED = "focused";
	public final static String EVENT_FRAME_DISABLED = "frame-disabled";
	public final static String EVENT_FRAME_ENABLED = "frame-enabled";
	public final static String EVENT_GROUP_CHANGED = "group-changed";
	public final static String EVENT_HIDDEN = "hidden";
	public final static String EVENT_INITIALIZED = "initialized";
	public final static String EVENT_MAXIMIZED = "maximized";
	public final static String EVENT_MINIMIZED = "minimized";
	public final static String EVENT_NAVIGATION_REJECTED = "navigation-rejected";
	public final static String EVENT_PRELOAD_SCRIPTS_STATE_CHANGED = "preload-scripts-state-changed";
	public final static String EVENT_PRELOAD_SCRIPTS_STATE_CHANGING = "preload-scripts-state-changing";
	public final static String EVENT_RELOADED = "reloaded";
	public final static String EVENT_RESOURCE_LOAD_FAILED = "resource-load-failed";
	public final static String EVENT_RESOURCE_RESPONSE_RECEIVED = "resource-response-received";
	public final static String EVENT_RESTORED = "restored";
	public final static String EVENT_SHOW_REQUESTED = "show-requested";
	public final static String EVENT_SHOWN = "shown";
	
	public final static String STATE_MINIMIZED = "minimized";
	public final static String STATE_MAXIMIZED = "maximized";
	public final static String STATE_NORMAL = "normal";

	FinWindowObject(FinConnectionImpl finConnection, Identity identity) {
		super(finConnection, identity);
	}

	/**
	 * Adds a listener to the end of the listeners list for the specified event.
	 * @param eventType
	 * @param listener
	 * @return
	 */
	public CompletionStage<Boolean> addEventListener(String eventType, FinEventListener listener) {
		return this.finConnection._subscriptionManager.addListener(this.identity, "window", eventType, listener);
	}

	public CompletionStage<Boolean> removeEventListener(String eventType, FinEventListener listener) {
		return this.finConnection._subscriptionManager.removeListener(this.identity, "window", eventType, listener);
	}

	/**
	 * Removes focus from the window.
	 * @return
	 */
	public CompletionStage<Void> blur() {
		return this.finConnection.sendMessage("blur-window", FinBeanUtils.toJsonObject(this.identity))
				.thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error blur-window, reason: " + ack.getReason());
					}
				});
	}

	/**
	 * Brings the window to the front of the window stack.
	 * @return
	 */
	public CompletionStage<Void> bringToFront() {
		return this.finConnection.sendMessage("bring-window-to-front", FinBeanUtils.toJsonObject(this.identity))
				.thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error bring-window-to-front, reason: " + ack.getReason());
					}
				});
	}

	/**
	 * Centers the window on its current screen.
	 * @return
	 */
	public CompletionStage<Void> center() {
		return this.finConnection.sendMessage("center-window", FinBeanUtils.toJsonObject(this.identity))
				.thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error center-window, reason: " + ack.getReason());
					}
				});
	}

	public CompletionStage<Void> flash() {
		return this.finConnection.sendMessage("flash-window", FinBeanUtils.toJsonObject(this.identity))
				.thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error flash-window, reason: " + ack.getReason());
					}
				});
	}

	public CompletionStage<Void> stopFlashing() {
		return this.finConnection.sendMessage("stop-flash-window", FinBeanUtils.toJsonObject(this.identity))
				.thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error stop-flash-window, reason: " + ack.getReason());
					}
				});
	}

	/**
	 * closes the window application.
	 * @return
	 */
	public CompletionStage<Void> close() {
		return this.close(null);
	}

	public CompletionStage<Void> close(Boolean force) {
		JsonObjectBuilder builder = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity));
		if (force != null) {
			builder = builder.add("force", force);
		}
		return this.finConnection.sendMessage("close-window", builder.build()).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error close-window, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<Void> hide() {
		return this.finConnection.sendMessage("hide-window", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error hide-window, reason: " + ack.getReason());
			}
		});
	}


	public CompletionStage<Void> show() {
		return this.show(null);
	}

	public CompletionStage<Void> show(Boolean force) {
		JsonObjectBuilder builder = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity));
		if (force != null) {
			builder = builder.add("force", force);
		}
		return this.finConnection.sendMessage("show-window", builder.build()).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error show-window, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<String> getNativeId() {
		return this.finConnection.sendMessage("get-window-native-id", FinBeanUtils.toJsonObject(this.identity)).thenApply(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error get-window-native-id, reason: " + ack.getReason());
			}
			else {
				return ((JsonString)ack.getData()).getString();
			}
		});
	}

	public CompletionStage<WindowOptions> getOptions() {
		return this.finConnection.sendMessage("get-window-options", FinBeanUtils.toJsonObject(this.identity)).thenApply(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error get-window-options, reason: " + ack.getReason());
			}
			else {
				return FinBeanUtils.fromJsonObject(ack.getData().asJsonObject(), WindowOptions.class);
			}
		});
	}
	
	public CompletionStage<FinLayoutObject> getLayout() {
		return this.finConnection._layout.wrap(this.identity);
	}

	public CompletionStage<Void> disableUserMovement() {
		return this.finConnection.sendMessage("disable-window-frame", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error disable-window-frame, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<Void> enableUserMovement() {
		return this.finConnection.sendMessage("enable-window-frame", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error enable-window-frame, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<FrameInfo[]> getAllFrames() {
		return this.finConnection.sendMessage("get-all-frames", FinBeanUtils.toJsonObject(this.identity)).thenApply(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error get-all-frames, reason: " + ack.getReason());
			}
			else {
				JsonArray arr = ack.getData().asJsonArray();
				FrameInfo[] frameInfos = new FrameInfo[arr.size()];
				for (int i=0; i<arr.size(); i++) {
					frameInfos[i] = FinBeanUtils.fromJsonObject(arr.get(i).asJsonObject(), FrameInfo.class);
				}
				return frameInfos;
			}
		});
	}
	
	public CompletionStage<WindowBounds> getBounds() {
		return this.finConnection.sendMessage("get-window-bounds", FinBeanUtils.toJsonObject(this.identity)).thenApply(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error get-window-bounds, reason: " + ack.getReason());
			}
			else {
				return FinBeanUtils.fromJsonObject(ack.getData().asJsonObject(), WindowBounds.class);
			}
		});
	}

	public CompletionStage<FinWindowObject[]> getGroup() {
		return this.finConnection.sendMessage("get-window-group", Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity)).add("crossApp", true).build()).thenApply(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error get-window-group, reason: " + ack.getReason());
			}
			else {
				JsonArray arr = ack.getData().asJsonArray();
				List<FinWindowObject> wins = new ArrayList<>();
				for (int i=0; i<arr.size(); i++) {
					JsonObject winInfo = arr.get(i).asJsonObject();
					if (winInfo.containsKey("isExternalWindow") && JsonValue.TRUE.equals(winInfo.get("isExternalWindow"))) {
						//need to deal with external window
					}
					else {
						try {
							FinWindowObject winObj = this.finConnection._window.wrap(FinBeanUtils.fromJsonObject(winInfo, Identity.class)).toCompletableFuture().get();
							wins.add(winObj);
						}
						catch (InterruptedException | ExecutionException e) {
						}
					}
				}
				return wins.toArray(new FinWindowObject[wins.size()]);
			}
		});
	}

	public CompletionStage<WindowInfo> getInfo() {
		return this.finConnection.sendMessage("get-window-info", FinBeanUtils.toJsonObject(this.identity)).thenApply(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error get-window-info, reason: " + ack.getReason());
			}
			else {
				return FinBeanUtils.fromJsonObject(ack.getData().asJsonObject(), WindowInfo.class);
			}
		});
	}
	
	public CompletionStage<FinApplicationObject> getParentApplication() {
		return this.finConnection._application.wrap(new Identity(this.getIdentity().getUuid()));
	}

	public CompletionStage<String> getState() {
		return this.finConnection.sendMessage("get-window-state", FinBeanUtils.toJsonObject(this.identity)).thenApply(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error get-window-state, reason: " + ack.getReason());
			}
			else {
				return ((JsonString)ack.getData()).getString();
			}
		});
	}
	
	public boolean isMainWindow() {
		return Objects.equals(this.identity.getUuid(), this.identity.getName());
	}
	
	public CompletionStage<Boolean> isShowing() {
		return this.finConnection.sendMessage("is-window-showing", FinBeanUtils.toJsonObject(this.identity)).thenApply(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error is-window-showing, reason: " + ack.getReason());
			}
			else {
				return JsonValue.TRUE.equals(ack.getData());
			}
		});
	}
	
	public CompletionStage<Void> joinGroup(FinWindowObject target) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity))
				.add("groupingUuid", target.getIdentity().getUuid()).add("groupingWindowName", target.getIdentity().getName()).build();
		return this.finConnection.sendMessage("join-window-group", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error join-window-group, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<Void> mergeGroups(FinWindowObject target) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity))
				.add("groupingUuid", target.getIdentity().getUuid()).add("groupingWindowName", target.getIdentity().getName()).build();
		return this.finConnection.sendMessage("merge-window-groups", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error merge-window-groups, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<Void> leaveGroup() {
		return this.finConnection.sendMessage("leave-window-group", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error leave-window-group, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<Void> maximize() {
		return this.finConnection.sendMessage("maximize-window", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error maximize-window, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<Void> minimize() {
		return this.finConnection.sendMessage("minimize-window", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error minimize-window, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<Void> restore() {
		return this.finConnection.sendMessage("restore-window", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error restore-window, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<Void> moveBy(int deltaX, int deltaY) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity)).add("deltaLeft", deltaX).add("deltaTop", deltaY).build();
		return this.finConnection.sendMessage("move-window-by", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error move-window-by, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<Void> moveTo(int x, int y) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity)).add("left", x).add("top", y).build();
		return this.finConnection.sendMessage("move-window", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error move-window, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<Void> resizeBy(int deltaWidth, int deltaHeight) {
		return this.resizeBy(deltaWidth, deltaHeight, Anchor.TOP_LEFT);
	}

	public CompletionStage<Void> resizeBy(int deltaWidth, int deltaHeight, Anchor anchor) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity))
				.add("deltaWidth", deltaWidth).add("deltaHeight", deltaHeight)
				.add("anchor", anchor.toString()).build();
		return this.finConnection.sendMessage("resize-window-by", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error resize-window-by, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<Void> resizeTo(int width, int height) {
		return this.resizeTo(width, height, Anchor.TOP_LEFT);
	}

	public CompletionStage<Void> resizeTo(int width, int height, Anchor anchor) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity))
				.add("width", width).add("height", height)
				.add("anchor", anchor.toString()).build();
		return this.finConnection.sendMessage("resize-window", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error resize-window, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<Void> setAsForeground() {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity))
				.build();
		return this.finConnection.sendMessage("set-foreground-window", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error set-foreground-window, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<Void> setBounds(Bounds newBounds) {
		JsonObjectBuilder builder = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity));
		if (newBounds.getTop() != null) {
			builder.add("top", newBounds.getTop());
		}
		if (newBounds.getLeft() != null) {
			builder.add("left", newBounds.getLeft());
		}
		if (newBounds.getWidth() != null) {
			builder.add("width", newBounds.getWidth());
		}
		if (newBounds.getHeight() != null) {
			builder.add("height", newBounds.getHeight());
		}
		return this.finConnection.sendMessage("set-window-bounds", builder.build()).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error set-window-bounds, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<Void> showAt(int left, int top) {
		return this.showAt(left, top, null);
	}
	
	public CompletionStage<Void> showAt(int left, int top, Boolean force) {
		JsonObjectBuilder builder = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity)).add("left", left).add("top", top);
		if (force != null) {
			builder.add("force", force);
		}
		return this.finConnection.sendMessage("show-at-window", builder.build()).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error show-at-window, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<Void> updateOptions(WindowUpdatableOptions options) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity)).add("options", FinBeanUtils.toJsonObject(options)).build();
		return this.finConnection.sendMessage("update-window-options", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error update-window-options, reason: " + ack.getReason());
			}
		});
	}
	
}
