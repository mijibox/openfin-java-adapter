package com.mijibox.openfin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.ApplicationInfo;
import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.ShortCutConfig;
import com.mijibox.openfin.bean.TrayIconInfo;
import com.mijibox.openfin.bean.WindowOptions;

public class FinApplicationObject extends FinInstanceObject {
	
	final static Logger logger = LoggerFactory.getLogger(FinApplicationObject.class);
	
	public final static String EVENT_CLOSED = "closed";
	public final static String EVENT_CONNECTED = "connected";
	public final static String EVENT_CRASHED = "crashed";
	public final static String EVENT_INITIALIZED = "initialized";
	public final static String EVENT_MANIFEST_CHANGED = "manifest-changed";
	public final static String EVENT_NOT_RESPONDING = "not-responding";
	public final static String EVENT_RESPONDING = "responding";
	public final static String EVENT_RUN_REQUESTED = "run-requested";
	public final static String EVENT_STARTED = "started";
	public final static String EVENT_TRAY_ICON_CLICKED = "tray-icon-clicked";
	public final static String EVENT_WINDOW_ALERT_REQUESTED = "window-alert-requested";
	public final static String EVENT_WINDOW_AUTH_REQUESTED = "window-auth-requested";
	public final static String EVENT_WINDOW_BLURRED = "window-blurred";
	public final static String EVENT_WINDOW_BOUNDS_CHANGED = "window-bounds-changed";
	public final static String EVENT_WINDOW_BOUNDS_CHANGING = "window-bounds-changing";
	public final static String EVENT_WINDOW_CERTIFICATE_SELECTION_SHOWN = "window-certificate-selection-shown";
	public final static String EVENT_WINDOW_CLOSED = "window-closed";
	public final static String EVENT_WINDOW_CLOSING = "window-closing";
	public final static String EVENT_WINDOW_CRASHED = "window-crashed";
	public final static String EVENT_WINDOW_CREATED = "window-created";
	public final static String EVENT_WINDOW_DID_CHANGE_THEME_COLOR = "window-did-change-theme-color";
	public final static String EVENT_WINDOW_DISABLED_MOVEMENT_BOUNDS_CHANGED = "window-disabled-movement-bounds-changed";
	public final static String EVENT_WINDOW_DISABLED_MOVEMENT_BOUNDS_CHANGING = "window-disabled-movement-bounds-changing";
	public final static String EVENT_WINDOW_EMBEDDED = "window-embedded";
	public final static String EVENT_WINDOW_END_LOAD = "window-end-load";
	public final static String EVENT_WINDOW_EXTERNAL_PROCESS_EXITED = "window-external-process-exited";
	public final static String EVENT_WINDOW_EXTERNAL_PROCESS_STARTED = "window-external-process-started";
	public final static String EVENT_WINDOW_FILE_DOWNLOAD_COMPLETED = "window-file-download-completed";
	public final static String EVENT_WINDOW_FILE_DOWNLOAD_PROGRESS = "window-file-download-progress";
	public final static String EVENT_WINDOW_FILE_DOWNLOAD_STARTED = "window-file-download-started";
	public final static String EVENT_WINDOW_FOCUSED = "window-focused";
	public final static String EVENT_WINDOW_GROUP_CHANGED = "window-group-changed";
	public final static String EVENT_WINDOW_HIDDEN = "window-hidden";
	public final static String EVENT_WINDOW_INITIALIZED = "window-initialized";
	public final static String EVENT_WINDOW_MAXIMIZED = "window-maximized";
	public final static String EVENT_WINDOW_MINIMIZED = "window-minimized";
	public final static String EVENT_WINDOW_OPTIONS_CHANGED = "window-options-changed";
	public final static String EVENT_WINDOW_NAVIGATION_REJECTED = "window-navigation-rejected";
	public final static String EVENT_WINDOW_NOT_RESPONDING = "window-not-responding";
	public final static String EVENT_WINDOW_PAGE_FAVICON_UPDATED = "window-page-favicon-updated";
	public final static String EVENT_WINDOW_PAGE_TITLE_UPDATED = "window-page-title-updated";
	public final static String EVENT_WINDOW_PERFORMANCE_REPORT = "window-performance-report";
	public final static String EVENT_WINDOW_PRELOAD_SCRIPTS_STATE_CHANGED = "window-preload-scripts-state-changed";
	public final static String EVENT_WINDOW_PRELOAD_SCRIPTS_STATE_CHANGING = "window-preload-scripts-state-changing";
	public final static String EVENT_WINDOW_RELOADED = "window-reloaded";
	public final static String EVENT_WINDOW_RESOURCE_LOAD_FAILED = "window-resource-load-failed";
	public final static String EVENT_WINDOW_RESOURCE_RESPONSE_RECEIVED = "window-resource-response-received";
	public final static String EVENT_WINDOW_RESPONDING = "window-responding";
	public final static String EVENT_WINDOW_RESTORED = "window-restored";
	public final static String EVENT_WINDOW_SHOW_REQUESTED = "window-show-requested";
	public final static String EVENT_WINDOW_SHOWN = "window-shown";
	public final static String EVENT_WINDOW_START_LOAD = "window-start-load";
	public final static String EVENT_WINDOW_USER_MOVEMENT_DISABLED = "window-user-movement-disabled";
	public final static String EVENT_WINDOW_USER_MOVEMENT_ENABLED = "window-user-movement-enabled";
	public final static String EVENT_WINDOW_WILL_MOVE = "window-will-move";
	public final static String EVENT_WINDOW_WILL_RESIZE = "window-will-resize";
	public final static String EVENT_VIEW_ATTACHED = "view-attached";
	public final static String EVENT_VIEW_CERTIFICATE_SELECTION_SHOWN = "view-certificate-selection-shown";
	public final static String EVENT_VIEW_CRASHED = "view-crashed";
	public final static String EVENT_VIEW_CREATED = "view-created";
	public final static String EVENT_VIEW_DESTROYED = "view-destroyed";
	public final static String EVENT_VIEW_DETACHED = "view-detached";
	public final static String EVENT_VIEW_DID_CHANGE_THEME_COLOR = "view-did-change-theme-color";
	public final static String EVENT_VIEW_FILE_DOWNLOAD_COMPLETED = "view-file-download-completed";
	public final static String EVENT_VIEW_FILE_DOWNLOAD_PROGRESS = "view-file-download-progress";
	public final static String EVENT_VIEW_FILE_DOWNLOAD_STARTED = "view-file-download-started";
	public final static String EVENT_VIEW_HIDDEN = "view-hidden";
	public final static String EVENT_VIEW_PAGE_FAVICON_UPDATED = "view-page-favicon-updated";
	public final static String EVENT_VIEW_PAGE_TITLE_UPDATED = "view-page-title-updated";
	public final static String EVENT_VIEW_RESOURCE_LOAD_FAILED = "view-resource-load-failed";
	public final static String EVENT_VIEW_RESOURCE_RESPONSE_RECEIVED = "view-resource-response-received";
	public final static String EVENT_VIEW_SHOWN = "view-shown";

	private FinWindowObject window;

	FinApplicationObject(FinConnectionImpl finConnection, Identity identity) {
		super(finConnection, identity);
	}

	/**
	 * Adds a listener to the end of the listeners array for the specified event.
	 * @param eventType
	 * @param listener
	 * @return
	 */
	public CompletionStage<Boolean> addEventListener(String eventType, FinEventListener listener) {
		return this.finConnection._subscriptionManager.addListener(this.identity, "application", eventType, listener);
	}
	
	public CompletionStage<Boolean> removeEventListener(String eventType, FinEventListener listener) {
		return  this.finConnection._subscriptionManager.removeListener(this.identity, "application", eventType, listener);
	}
	
	/**
	 * Determines if the application is currently running.
	 * @return
	 */
	public CompletionStage<Boolean> isRunning() {
		return this.finConnection.sendMessage("is-application-running", FinBeanUtils.toJsonObject(identity)).thenApply(ack ->{
			if (ack.isSuccess()) {
				JsonValue data = ack.getData();
				if (JsonValue.ValueType.TRUE.equals(data.getValueType())) {
					return true;
				}
				else {
					return false;
				}
			}
			else {
				throw new RuntimeException("error checking if application isRunning, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<Void> quit() {
		return this.quit(null);
	}
	
	/**
	 * Closes the application and any child windows created by the application.
	 * @param force
	 * @return
	 */
	public CompletionStage<Void> quit(Boolean force) {
		JsonObjectBuilder builder = Json.createObjectBuilder(FinBeanUtils.toJsonObject(identity));
		if (force != null) {
			builder.add("force", force);
		}
		JsonObject payload = builder.build();
		
		return this.finConnection.sendMessage("close-application", payload).thenAccept(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error close-application, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<Void> terminate() {
		return this.finConnection.sendMessage("terminate-application", FinBeanUtils.toJsonObject(identity)).thenAccept(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error quitting application, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Retrieves information about the application.
	 * @return
	 */
	public CompletionStage<ApplicationInfo> getInfo() {
		return this.finConnection.sendMessage("get-info", FinBeanUtils.toJsonObject(identity)).thenApply(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error quitting application, reason: " + ack.getReason());
			}
			else {
				return FinBeanUtils.fromJsonObject((JsonObject)ack.getData(), ApplicationInfo.class);
			}
		});
	}
	
	/**
	 * Retrieves a list of wrapped OfWindowObject for each of the application’s child windows.
	 * @return
	 */
	public CompletionStage<List<FinWindowObject>> getChildWindows() {
		return this.finConnection.sendMessage("get-child-windows", FinBeanUtils.toJsonObject(this.identity)).thenApply(ack ->{
			if (ack.isSuccess()) {
				logger.info("getChildWindows: {}", ack.getData());
				JsonArray winNames = ack.getData().asJsonArray();
				ArrayList<FinWindowObject> winList = new ArrayList<>(winNames.size());
				FinWindow ofWin = new FinWindow(this.finConnection);
				winNames.forEach(v->{
					String winName = ((JsonString)v).getString();
					try {
						winList.add(ofWin.wrap(new Identity(this.identity.getUuid(), winName)).toCompletableFuture().get(5, TimeUnit.SECONDS));
					}
					catch (InterruptedException | ExecutionException | TimeoutException e) {
						e.printStackTrace();
					}
				});
				return winList;
			}
			else {
				throw new RuntimeException("error getChildWindows, reason: " + ack.getReason());
			}
		});
	}
	/**
	 * Returns an instance of the main Window of the application
	 * @return
	 */
	public FinWindowObject getWindow() {
		if (this.window == null) {
			this.window = new FinWindowObject(this.finConnection, new Identity(this.identity.getUuid(), this.identity.getUuid()));
		}
		return this.window;
	}
	
	/**
	 * Retrieves an list of active window groups for all of the application's windows. Each group is represented as a list of wrapped OfWindowObject.
	 * @return
	 */
	public CompletionStage<List<List<FinWindowObject>>> getGroups() {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity)).add("crossApp", true).build();
		return this.finConnection.sendMessage("get-application-groups", payload).thenApply(ack-> {
			if (ack.isSuccess()) {
				List<List<FinWindowObject>> winGrps = new ArrayList<>();
				JsonArray grps = ack.getData().asJsonArray();
				grps.forEach(v->{
					ArrayList<FinWindowObject> winList = new ArrayList<>();
					winGrps.add(winList);
					v.asJsonArray().forEach(i->{
						Identity winIdentity = FinBeanUtils.fromJsonObject(i.asJsonObject(), Identity.class);
						try {
							winList.add(this.finConnection._window.wrap(winIdentity).toCompletableFuture().get(5, TimeUnit.SECONDS));
						}
						catch (InterruptedException | ExecutionException | TimeoutException e) {
							e.printStackTrace();
						}
					});
					
				});
				return winGrps;
			}
			else
			{
				throw new RuntimeException("error getGroups, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Retrieves the JSON manifest that was used to create the application. Throws RuntimeException if the application was not created from a manifest.
	 * @return
	 */
	public CompletionStage<JsonObject> getManifest() {
		return this.finConnection.sendMessage("get-application-manifest", FinBeanUtils.toJsonObject(identity)).thenApply(ack->{
			if (ack.isSuccess()) {
				return ack.getData().asJsonObject();
			}
			else {
				throw new RuntimeException("error getManifest, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Retrieves UUID of the application that launches this application. Throws RuntimeException if the application was created from a manifest.
	 * @return
	 */
	public CompletionStage<String> getParentUuid() {
		return this.finConnection.sendMessage("get-parent-application", FinBeanUtils.toJsonObject(identity)).thenApply(ack->{
			if (ack.isSuccess()) {
				return ((JsonString)ack.getData()).getString();
			}
			else {
				throw new RuntimeException("error getParentUuid, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<FinWindowObject> createChildWindow(WindowOptions winOpts) {
		CompletableFuture<FinWindowObject> childWinFuture = new CompletableFuture<>();
		JsonObject payload = Json.createObjectBuilder().add("targetUuid", this.identity.getUuid()).add("windowOptions", FinBeanUtils.toJsonObject(winOpts)).build();
		this.finConnection.sendMessage("create-child-window", payload).thenAccept(ack->{
			if (ack.isSuccess()) {
				FinWindowObject childWinObj = new FinWindowObject(this.finConnection, new Identity(this.identity.getUuid(), winOpts.getName()));
				childWinObj.addEventListener(FinWindowObject.EVENT_INITIALIZED, e->{
					//return when it's initialized.
					childWinFuture.complete(childWinObj);
				});
			}
			else {
				childWinFuture.completeExceptionally(new RuntimeException("error createChildWindow, reason: " + ack.getReason()));
			}
		});
		return childWinFuture;
	}
	
	public CompletionStage<Void> registerUser(String userName, String appName) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(identity))
				.add("userName", userName)
				.add("appName", appName).build();
		return this.finConnection.sendMessage("register-user", payload).thenAccept(ack->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error registerUser, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<ShortCutConfig> getShortCuts() {
		return this.finConnection.sendMessage("get-shortcuts", FinBeanUtils.toJsonObject(this.identity)).thenApply(ack->{
			if (ack.isSuccess()) {
				return FinBeanUtils.fromJsonObject(ack.getData().asJsonObject(), ShortCutConfig.class);
			}
			else {
				throw new RuntimeException("error get-shortcuts, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<Void> setTrayIcon(String icon) {
		return this.finConnection.sendMessage("set-tray-icon", Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.identity)).add("enabledIcon", icon).build()).thenAccept(ack->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error get-tray-icon-info, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<TrayIconInfo> getTrayIconInfo() {
		return this.finConnection.sendMessage("get-tray-icon-info", FinBeanUtils.toJsonObject(this.identity)).thenApply(ack->{
			if (ack.isSuccess()) {
				return FinBeanUtils.fromJsonObject(ack.getData().asJsonObject(), TrayIconInfo.class);
			}
			else {
				throw new RuntimeException("error get-tray-icon-info, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Returns the current zoom level of the application.
	 * @return
	 */
	public CompletionStage<Integer> getZoomLevel() {
		return this.finConnection.sendMessage("get-application-zoom-level", FinBeanUtils.toJsonObject(this.identity)).thenApply(ack->{
			if (ack.isSuccess()) {
				return ((JsonNumber)ack.getData()).intValue();
			}
			else {
				throw new RuntimeException("error getZoomLevel, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<Void> setZoomLevel(int zoomLevel) {
		JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(identity))
				.add("level", zoomLevel).build();
		return this.finConnection.sendMessage("set-application-zoom-level", payload).thenAccept(ack->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error setZoomLevel, reason: " + ack.getReason());
			}
		});
	}
	
}
