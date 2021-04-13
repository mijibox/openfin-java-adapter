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
import com.mijibox.openfin.bean.ApplicationInfo;
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
	 * @return
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
	 * @return
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
	 * Adds a listener to the end of the listeners array for the specified event.
	 * 
	 * @param eventType
	 * @param listener
	 * @return
	 */
	public CompletionStage<Boolean> addEventListener(String eventType, FinEventListener listener) {
		return this.finConnection._subscriptionManager.addListener("system", eventType, listener);
	}
	
	public CompletionStage<Boolean> removeEventListener(String eventType, FinEventListener listener) {
		return this.finConnection._subscriptionManager.removeListener("system", eventType, listener);
	}
	
	/**
	 * Clears cached data containing application resource files (images, HTML, JavaScript files), cookies, and items stored in the Local Storage.
	 * 
	 * @param opts
	 * @return
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
	 * @return
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
	 * @return
	 */
	public CompletionStage<Void> exit() {
		return this.finConnection.sendMessage("exit-desktop").thenAccept(ack ->{
			//will it definitely get the ack?
			if (!ack.isSuccess()) {
				throw new RuntimeException("error exit-desktop, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Writes any unwritten cookies data to disk.
	 * 
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
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
	 * @param url
	 * @return
	 */
	public CompletionStage<Void> openUrlWithBrowser(String url) {
		return this.finConnection.sendMessage("open-url-with-browser", Json.createObjectBuilder().add("url", url).build()).thenAccept(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error openUrlWithBrowser, reason: " + ack.getReason());
			}
		});
	}
	
	public CompletionStage<Void> setCookie(CookieDetails cookie) {
		return this.finConnection.sendMessage("set-cookie", FinBeanUtils.toJsonObject(cookie)).thenAccept(ack ->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error setCookie, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Get additional info of cookies.
	 * @param url
	 * @param name
	 * @return
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
	 * @return
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
	 * @return
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
	 * @param identity
	 * @return
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
	 * @param path
	 * @param arguments
	 * @return
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
