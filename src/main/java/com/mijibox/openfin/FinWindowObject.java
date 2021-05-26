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
	 * Adds a listener to the end of the listener list for the specified event.
	 * @param eventType The type of the event.
	 * @param listener The listener to be added.
	 * @return new CompletionStage of the result in boolean, true if the listener is appended at the end of the listener list.
	 */
	public CompletionStage<Boolean> addEventListener(String eventType, FinEventListener listener) {
		return this.finConnection._subscriptionManager.addListener(this.identity, "window", eventType, listener);
	}

	/**
	 * Removes the listener from the listener list.
	 * @param eventType The type of the event.
	 * @param listener The listener to be removed.
	 * @return new CompletionStage of the result in boolean, true if the listener is removed from the listener list.
	 */
	public CompletionStage<Boolean> removeEventListener(String eventType, FinEventListener listener) {
		return this.finConnection._subscriptionManager.removeListener(this.identity, "window", eventType, listener);
	}

	/**
	 * Removes focus from the window.
	 * @return A new CompletionStage for the task.
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
	 * @return A new CompletionStage for the task.
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
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> center() {
		return this.finConnection.sendMessage("center-window", FinBeanUtils.toJsonObject(this.identity))
				.thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error center-window, reason: " + ack.getReason());
					}
				});
	}

	/**
	 * Flashes the window’s frame and taskbar icon until stopFlashing is called or until a focus event is fired.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> flash() {
		return this.finConnection.sendMessage("flash-window", FinBeanUtils.toJsonObject(this.identity))
				.thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error flash-window, reason: " + ack.getReason());
					}
				});
	}

	/**
	 * Stops the taskbar icon from flashing.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> stopFlashing() {
		return this.finConnection.sendMessage("stop-flash-window", FinBeanUtils.toJsonObject(this.identity))
				.thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error stop-flash-window, reason: " + ack.getReason());
					}
				});
	}

	/**
	 * Closes the window.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> close() {
		return this.close(null);
	}

	/**
	 * Closes the window.
	 * @param force Close will be prevented from closing when force is false and ‘close-requested’ has been subscribed.
	 * @return A new CompletionStage for the task.
	 */
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

	/**
	 * Hides the window.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> hide() {
		return this.finConnection.sendMessage("hide-window", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error hide-window, reason: " + ack.getReason());
			}
		});
	}


	/**
	 * Shows the window if it is hidden.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> show() {
		return this.show(null);
	}

	/**
	 * Shows the window if it is hidden.
	 * @param force Show will be prevented from showing when force is false and ‘show-requested’ has been subscribed.
	 * @return A new CompletionStage for the task.
	 */
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

	/**
	 * Returns the native OS level Id. In Windows, it will return the Windows handle.
	 * @return A new CompletionStage for the native ID of the window.
	 */
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

	/**
	 * Gets the current settings of the window.
	 * @return A new CompletionStage for the settings of the window.
	 */
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

	/**
	 * Retrieves the window's Layout.
	 * @return A new CompletionStage for the layout of the window.
	 */
	public CompletionStage<FinLayoutObject> getLayout() {
		return this.finConnection._layout.wrap(this.identity);
	}

	/**
	 * Prevents a user from changing a window's size/position when using the window's frame.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> disableUserMovement() {
		return this.finConnection.sendMessage("disable-window-frame", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error disable-window-frame, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Re-enables user changes to a window's size/position when using the window's frame.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> enableUserMovement() {
		return this.finConnection.sendMessage("enable-window-frame", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error enable-window-frame, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Retrieves an array of frame info objects representing the main frame and any iframes that are currently on the page.
	 * @return A new CompletionStage for the array of frame information objects.
	 */
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
	
	/**
	 * Gets the current bounds of the window.
	 * @return A new CompletionStage for the bounds of the window.
	 */
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

	/**
	 * Retrieves an array containing window objects that are grouped with this window.
	 * @return A new CompletionStage for the array of the window objects.
	 */
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

	/**
	 * Gets an information object for the window.
	 * @return A new CompletionStage for the information object of the window.
	 */
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
	
	/**
	 * Gets the parent application.
	 * @return A new CompletionStage of the parent application.
	 */
	public CompletionStage<FinApplicationObject> getParentApplication() {
		return this.finConnection._application.wrap(new Identity(this.getIdentity().getUuid()));
	}

	/**
	 * Gets the current state of the window.
	 * @return The state of the window ("minimized", "maximized", or "restored"). 
	 */
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
	
	/**
	 * Determines if the window is a main window.
	 * @return true if the window is the main window of an application.
	 */
	public boolean isMainWindow() {
		return Objects.equals(this.identity.getUuid(), this.identity.getName());
	}
	
	/**
	 * Determines if the window is currently showing.
	 * @return A new CompletionStage of the visibility of the window.
	 */
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
	
	/**
	 * Joins the same window group as the specified window. 
	 * @param target The window whose group is to be joined.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> joinGroup(FinWindowObject target) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity))
				.add("groupingUuid", target.getIdentity().getUuid()).add("groupingWindowName", target.getIdentity().getName()).build();
		return this.finConnection.sendMessage("join-window-group", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error join-window-group, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Merges the instance's window group with the same window group as the specified window
	 * @param target The window whose group is to be merged with.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> mergeGroups(FinWindowObject target) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity))
				.add("groupingUuid", target.getIdentity().getUuid()).add("groupingWindowName", target.getIdentity().getName()).build();
		return this.finConnection.sendMessage("merge-window-groups", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error merge-window-groups, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Leaves the current window group so that the window can be move independently of those in the group.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> leaveGroup() {
		return this.finConnection.sendMessage("leave-window-group", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error leave-window-group, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Maximizes the window.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> maximize() {
		return this.finConnection.sendMessage("maximize-window", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error maximize-window, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Minimizes the window.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> minimize() {
		return this.finConnection.sendMessage("minimize-window", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error minimize-window, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Restores the window to its normal state.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> restore() {
		return this.finConnection.sendMessage("restore-window", FinBeanUtils.toJsonObject(this.identity)).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error restore-window, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Moves the window by a specified amount. 
	 * @param deltaX The change in the X coordinate of the new location.
	 * @param deltaY The change in the Y coordinate of the new location.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> moveBy(int deltaX, int deltaY) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity)).add("deltaLeft", deltaX).add("deltaTop", deltaY).build();
		return this.finConnection.sendMessage("move-window-by", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error move-window-by, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Moves the window to a specified location.
	 * @param x The X coordinate of the new location.
	 * @param y The Y coordinate of the new location
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> moveTo(int x, int y) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity)).add("left", x).add("top", y).build();
		return this.finConnection.sendMessage("move-window", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error move-window, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Resizes the window by a specified amount.
	 * @param deltaWidth The change in the width of the window.
	 * @param deltaHeight The change in the height of the window.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> resizeBy(int deltaWidth, int deltaHeight) {
		return this.resizeBy(deltaWidth, deltaHeight, Anchor.TOP_LEFT);
	}

	/**
	 * Resizes the window by a specified amount.
	 * @param deltaWidth The change in the width of the window.
	 * @param deltaHeight The change in the height of the window.
	 * @param anchor Specifies a corner to remain fixed during the resize.
	 * @return A new CompletionStage for the task.
	 */
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

	/**
	 * Resizes the window to the specified dimensions.
	 * @param width The new width of the window.
	 * @param height The new height of the window.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> resizeTo(int width, int height) {
		return this.resizeTo(width, height, Anchor.TOP_LEFT);
	}

	/**
	 * Resizes the window to the specified dimensions.
	 * @param width The new width of the window.
	 * @param height The new height of the window.
	 * @param anchor Specifies a corner to remain fixed during the resize. 
	 * @return A new CompletionStage for the task.
	 */
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

	/**
	 * Brings the window to the front of the entire stack and gives it focus.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> setAsForeground() {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity))
				.build();
		return this.finConnection.sendMessage("set-foreground-window", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error set-foreground-window, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Sets the window's size and position.
	 * @param newBounds The new size and position.
	 * @return A new CompletionStage for the task.
	 */
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

	/**
	 * Show the window at specified location.
	 * @param left The left position of the window.
	 * @param top The top position of the window.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> showAt(int left, int top) {
		return this.showAt(left, top, null);
	}
	
	/**
	 * Show the window at specified location.
	 * @param left The left position of the window.
	 * @param top The top position of the window.
	 * @param force The window will be prevented from showing when force is false and ‘show-requested’ has been subscribed.
	 * @return A new CompletionStage for the task.
	 */
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
	
	/**
	 * Updates the window using the passed options.
	 * @param options The options to update.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> updateOptions(WindowUpdatableOptions options) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity)).add("options", FinBeanUtils.toJsonObject(options)).build();
		return this.finConnection.sendMessage("update-window-options", payload).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error update-window-options, reason: " + ack.getReason());
			}
		});
	}
	
}
