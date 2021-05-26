package com.mijibox.openfin;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

import com.mijibox.openfin.bean.AppInfo;
import com.mijibox.openfin.bean.ClearCacheOption;
import com.mijibox.openfin.bean.CookieDetails;
import com.mijibox.openfin.bean.CookieInfo;
import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.MonitorInfo;
import com.mijibox.openfin.bean.RuntimeInfo;
import com.mijibox.openfin.bean.RvmInfo;
import com.mijibox.openfin.bean.WinInfo;

public class FinSystem extends FinApiObject {

	FinSystem(FinConnectionImpl finConnection) {
		super(finConnection);
	}

	/**
	 * Returns the version of the runtime. The version contains the major, minor, build and revision numbers.
	 * @return new CompletionStage of the version.
	 */
	public CompletionStage<String> getVersion() {
		return this.finConnection.sendMessage("get-version", JsonValue.EMPTY_JSON_OBJECT).thenApply(ack ->{
			if (ack.isSuccess()) {
				return ((JsonString)ack.getData()).getString();
			}
			else {
				throw new RuntimeException("error getVersion, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Retrieves the command line argument string that started OpenFin Runtime.
	 * @return new CompletionStage of the command line arguments.
	 */
	public CompletionStage<String> getCommandLineArguments() {
		return this.finConnection.sendMessage("get-command-line-arguments").thenApply(ack ->{
			if (ack.isSuccess()) {
				return ((JsonString)ack.getData()).getString();
			}
			else {
				throw new RuntimeException("error getCommandLineArguments, reason: " + ack.getReason());
			}
		});
	}
	/**
	 * Adds a listener to the end of the listeners list for the specified event.
	 * 
	 * @param eventType The type of the event.
	 * @param listener The listener to be added.
	 * @return new CompletionStage of the result in boolean, true if the listener is added at the end of the listener list.
	 */
	public CompletionStage<Boolean> addEventListener(String eventType, FinEventListener listener) {
		return this.finConnection._subscriptionManager.addListener("system", eventType, listener);
	}
	
	/**
	 * Removes the listener from the listener list for the specified event.
	 * @param eventType The type of the event.
	 * @param listener The listener to be removed.
	 * @return new CompletionStage of the result in boolean, true if the listener is removed from the listener list.
	 */
	public CompletionStage<Boolean> removeEventListener(String eventType, FinEventListener listener) {
		return this.finConnection._subscriptionManager.removeListener("system", eventType, listener);
	}
	
	/**
	 * Clears cached data containing application resource files (images, HTML, JavaScript files), cookies, and items stored in the Local Storage.
	 * 
	 * @param opts Options used to clear the cache.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> clearCache(ClearCacheOption opts) {
		return this.finConnection.sendMessage("clear-cache", opts == null ? JsonValue.EMPTY_JSON_OBJECT : FinBeanUtils.toJsonObject(opts)).thenAccept(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error clear-cache, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Clears all cached data when OpenFin Runtime exits.
	 * 
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> deleteCacheOnExit() {
		return this.finConnection.sendMessage("delete-cache-request").thenAccept(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error delete-cache-request, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Exits the Runtime.
	 * 
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> exit() {
		return this.finConnection.sendMessage("exit-desktop").thenAccept(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error exit-desktop, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Writes any unwritten cookies data to disk.
	 * 
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> flushCookieStore() {
		return this.finConnection.sendMessage("flush-cookie-store").thenAccept(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error flush-cookie-store, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Retrieves an array of data for all applications.
	 * 
	 * @return new CompletionStage of all application info.
	 */
	public CompletionStage<AppInfo[]> getAllApplications() {
		return this.finConnection.sendMessage("get-all-applications").thenApply(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error get-all-applications, reason: " + ack.getReason());
			}
			else {
				JsonArray appArray = ack.getData().asJsonArray();
				AppInfo[] appInfos = new AppInfo[appArray.size()];
				for (int i=0; i<appArray.size(); i++) {
					appInfos[i] = FinBeanUtils.fromJsonObject(appArray.get(i).asJsonObject(), AppInfo.class);
				}
				return appInfos;
			}
		});
	}

	/**
	 * Retrieves an array of data (name, ids, bounds) for all application windows.
	 * @return new CompletionStage of all window info.
	 */
	public CompletionStage<WinInfo[]> getAllWindows() {
		return this.finConnection.sendMessage("get-all-windows").thenApply(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error get-all-windows, reason: " + ack.getReason());
			}
			else {
				JsonArray winArray = ack.getData().asJsonArray();
				WinInfo[] winInfos = new WinInfo[winArray.size()];
				for (int i=0; i<winArray.size(); i++) {
					winInfos[i] = FinBeanUtils.fromJsonObject(winArray.get(i).asJsonObject(), WinInfo.class);
				}
				return winInfos;
			}
		});
	}

	/**
	 * Returns information about the running Runtime in an object.
	 * @return new CompletionStage of the runtime info.
	 */
	public CompletionStage<RuntimeInfo> getRuntimeInfo() {
		return this.finConnection.sendMessage("get-runtime-info").thenApply(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error get-runtime-info, reason: " + ack.getReason());
			}
			else {
				return FinBeanUtils.fromJsonObject(ack.getData().asJsonObject(), RuntimeInfo.class);
			}
		});
	}
	
	/**
	 * Returns information about the running RVM in an object.
	 * @return new CompletionStage of RVM info.
	 */
	public CompletionStage<RvmInfo> getRvmInfo() {
		return this.finConnection.sendMessage("get-rvm-info").thenApply(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error get-rvm-info, reason: " + ack.getReason());
			}
			else {
				return FinBeanUtils.fromJsonObject(ack.getData().asJsonObject(), RvmInfo.class);
			}
		});
	}

	/**
	 * Returns the mouse in virtual screen coordinates.
	 * @return new CompletionStage of mouse position.
	 */
	public CompletionStage<Point> getMousePosition() {
		return this.finConnection.sendMessage("get-mouse-position").thenApply(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error getMousePosition, reason: " + ack.getReason());
			}
			else {
				JsonObject obj = ack.getData().asJsonObject();
				return new Point(obj.getInt("left"), obj.getInt("top"));
			}
		});
	}
	
	/**
	 * Returns a unique identifier (UUID) provided by the machine.
	 * @return new CompletionStage of machine ID.
	 */
	public CompletionStage<String> getMachineId() {
		return this.finConnection.sendMessage("get-machine-id").thenApply(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error getMachineId, reason: " + ack.getReason());
			}
			else {
				return ((JsonString)ack.getData()).getString();
			}
		});
	}
	
	/**
	 * Opens the passed URL in the default web browser. It only supports http(s) and fin(s) protocols by default. File protocol and file path are not supported.
	 * @param url The URL to open in the browser.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> openUrlWithBrowser(String url) {
		return this.finConnection.sendMessage("open-url-with-browser", Json.createObjectBuilder().add("url", url).build()).thenAccept(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error openUrlWithBrowser, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Sets the cookie.
	 * @param cookie detail of the cookie.
	 * @return new CompletionStage of the task.
	 */
	public CompletionStage<Void> setCookie(CookieDetails cookie) {
		return this.finConnection.sendMessage("set-cookie", FinBeanUtils.toJsonObject(cookie)).thenAccept(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error setCookie, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Get additional info of cookies.
	 * @param url The URL of the cookie.
	 * @param name The name of the cook.e
	 * @return new CompletionStage of the matching cookies.
	 */
	public CompletionStage<List<CookieInfo>> getCookies(String url, String name) {
		JsonObjectBuilder builder = Json.createObjectBuilder().add("url", url).add("name", name);
		return this.finConnection.sendMessage("get-cookies", builder.build()).thenApply(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error setCookie, reason: " + ack.getReason());
			}
			else {
				JsonArray cookies = ack.getData().asJsonArray();
				ArrayList<CookieInfo> cookieList = new ArrayList<>(cookies.size());
				cookies.forEach(c->{
					cookieList.add(FinBeanUtils.fromJsonObject(c.asJsonObject(), CookieInfo.class));
				});
				
				return cookieList;
			}
		});
	}
	
	/**
	 * Returns a hex encoded hash of the machine id and the currently logged in user
	 * name. This is the recommended way to uniquely identify a user / machine
	 * combination.
	 * 
	 * @return new CompletionStage of the unique user ID. 
	 */
	public CompletionStage<String> getUniqueUserId() {
		return this.finConnection.sendMessage("get-unique-user-id").thenApply(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error get-unique-user-id, reason: " + ack.getReason());
			}
			else {
				return ((JsonString)ack.getData()).getString();
			}
		});
	}
	
	/**
	 * Retrieves an object that contains data about the monitor setup of the computer that the runtime is running on.
	 * @return new CompletionStage of the monitor info. 
	 */
	public CompletionStage<MonitorInfo> getMonitorInfo() {
		return this.finConnection.sendMessage("get-monitor-info").thenApply(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error get-monitor-info, reason: " + ack.getReason());
			}
			else {
				return FinBeanUtils.fromJsonObject(ack.getData().asJsonObject(), MonitorInfo.class);
			}
		});
	}
	
	/**
	 * Shows the Chromium Developer Tools for the specified window
	 * @param identity The identity of the window.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> showDeveloperTools(Identity identity) {
		return this.finConnection.sendMessage("show-developer-tools", FinBeanUtils.toJsonObject(identity)).thenAccept(ack->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error show-developer-tools, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Runs an executable or batch file. 
	 * @param path The path of the executable.
	 * @param arguments The arguments to be passed to the executable.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> launchExternalProcess(String path, String arguments) {
		JsonObjectBuilder builder = Json.createObjectBuilder().add("path", path);
		if (arguments != null) {
			builder.add("arguments", arguments);
		}
		return this.finConnection.sendMessage("launch-external-process", builder.build()).thenAccept(ack->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error launch-external-process, reason: " + ack.getReason());
			}
		});
		
	}

}
