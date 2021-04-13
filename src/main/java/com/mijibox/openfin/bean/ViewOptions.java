package com.mijibox.openfin.bean;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonValue;

public class ViewOptions extends FinJsonBean {

	private String name;
	private String url;
	private String uuid;
	private Identity target;
	private String processAffinity;
	private Boolean detachOnClose;
	private Boolean isClosable;
	private Boolean preventDragOut;
	private List<PreloadScript> preloadScripts;
	private String backgroundColor;
	private JsonValue customData;
	private JsonValue customContext;
	private AutoResizeOptions autoResize;
	private Bounds bounds;
	private String manifestUrl;
	private ContextMenuSettings contextMenuSettings;
	private ContentNavigation contentNavigation;
	private List<Hotkey> hotkeys;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Identity getTarget() {
		return target;
	}

	public void setTarget(Identity target) {
		this.target = target;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getProcessAffinity() {
		return processAffinity;
	}

	public void setProcessAffinity(String processAffinity) {
		this.processAffinity = processAffinity;
	}

	public Boolean getDetachOnClose() {
		return detachOnClose;
	}

	public void setDetachOnClose(Boolean detachOnClose) {
		this.detachOnClose = detachOnClose;
	}

	public Boolean getIsClosable() {
		return isClosable;
	}

	public void setIsClosable(Boolean isClosable) {
		this.isClosable = isClosable;
	}

	public Boolean getPreventDragOut() {
		return preventDragOut;
	}

	public void setPreventDragOut(Boolean preventDragOut) {
		this.preventDragOut = preventDragOut;
	}

	public List<PreloadScript> getPreloadScripts() {
		return preloadScripts;
	}

	public void setPreloadScripts(List<PreloadScript> preloadScripts) {
		this.preloadScripts = preloadScripts;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public JsonValue getCustomData() {
		return customData;
	}

	public void setCustomData(JsonValue customData) {
		this.customData = customData;
	}

	public JsonValue getCustomContext() {
		return customContext;
	}

	public void setCustomContext(JsonValue customContext) {
		this.customContext = customContext;
	}

	public AutoResizeOptions getAutoResize() {
		return autoResize;
	}

	public void setAutoResize(AutoResizeOptions autoResize) {
		this.autoResize = autoResize;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	public String getManifestUrl() {
		return manifestUrl;
	}

	public void setManifestUrl(String manifestUrl) {
		this.manifestUrl = manifestUrl;
	}

	public ContextMenuSettings getContextMenuSettings() {
		return contextMenuSettings;
	}

	public void setContextMenuSettings(ContextMenuSettings contextMenuSettings) {
		this.contextMenuSettings = contextMenuSettings;
	}

	public ContentNavigation getContentNavigation() {
		return contentNavigation;
	}

	public void setContentNavigation(ContentNavigation contentNavigation) {
		this.contentNavigation = contentNavigation;
	}

	public List<Hotkey> getHotkeys() {
		return hotkeys;
	}

	public void setHotkeys(List<Hotkey> hotkeys) {
		this.hotkeys = hotkeys;
	}
	
	public void addHotkey(Hotkey hotkey) {
		if (this.hotkeys == null) {
			this.hotkeys = new ArrayList<>();
		}
		this.hotkeys.add(hotkey);
	}
	
	public void removeHotkey(Hotkey hotkey) {
		if (this.hotkeys != null) {
			this.hotkeys.remove(hotkey);
		}
	}
	
}
