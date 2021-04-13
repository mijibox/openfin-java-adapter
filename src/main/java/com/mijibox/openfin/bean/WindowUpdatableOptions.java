package com.mijibox.openfin.bean;

import java.util.List;

import javax.json.JsonValue;

public class WindowUpdatableOptions extends FinJsonBean {

	protected Boolean alwaysOnTop;
	protected Double aspectRatio;
	protected Boolean contextMenu;
	protected ContextMenuSettings contextMenuSettings;
	protected CornerRounding cornerRounding;
	protected JsonValue customContext;
	protected JsonValue customData;
	protected Boolean frame;
	protected List<Hotkey> hotkeys;
	protected String icon;
	protected Integer maxHeight;
	protected Boolean maximizable;
	protected Integer maxWidth;
	protected Integer minWidth;
	protected Boolean minimizable;
	protected Integer minHeight;
	protected Double opacity;
	protected Boolean resizable;
	protected ResizeRegion resizeRegion;
	protected Boolean showTaskbarIcon;

	public Boolean getAlwaysOnTop() {
		return alwaysOnTop;
	}

	public void setAlwaysOnTop(Boolean alwaysOnTop) {
		this.alwaysOnTop = alwaysOnTop;
	}

	public Double getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(Double aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	public Boolean getContextMenu() {
		return contextMenu;
	}

	public void setContextMenu(Boolean contextMenu) {
		this.contextMenu = contextMenu;
	}

	public ContextMenuSettings getContextMenuSettings() {
		return contextMenuSettings;
	}

	public void setContextMenuSettings(ContextMenuSettings contextMenuSettings) {
		this.contextMenuSettings = contextMenuSettings;
	}

	public CornerRounding getCornerRounding() {
		return cornerRounding;
	}

	public void setCornerRounding(CornerRounding cornerRounding) {
		this.cornerRounding = cornerRounding;
	}

	public JsonValue getCustomContext() {
		return customContext;
	}

	public void setCustomContext(JsonValue customContext) {
		this.customContext = customContext;
	}

	public JsonValue getCustomData() {
		return customData;
	}

	public void setCustomData(JsonValue customData) {
		this.customData = customData;
	}

	public Boolean getFrame() {
		return frame;
	}

	public void setFrame(Boolean frame) {
		this.frame = frame;
	}

	public List<Hotkey> getHotkeys() {
		return hotkeys;
	}

	public void setHotkeys(List<Hotkey> hotkeys) {
		this.hotkeys = hotkeys;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(Integer maxHeight) {
		this.maxHeight = maxHeight;
	}

	public Boolean getMaximizable() {
		return maximizable;
	}

	public void setMaximizable(Boolean maximizable) {
		this.maximizable = maximizable;
	}

	public Integer getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
	}

	public Integer getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(Integer minWidth) {
		this.minWidth = minWidth;
	}

	public Boolean getMinimizable() {
		return minimizable;
	}

	public void setMinimizable(Boolean minimizable) {
		this.minimizable = minimizable;
	}

	public Integer getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(Integer minHeight) {
		this.minHeight = minHeight;
	}

	public Double getOpacity() {
		return opacity;
	}

	public void setOpacity(Double opacity) {
		this.opacity = opacity;
	}

	public Boolean getResizable() {
		return resizable;
	}

	public void setResizable(Boolean resizable) {
		this.resizable = resizable;
	}

	public ResizeRegion getResizeRegion() {
		return resizeRegion;
	}

	public void setResizeRegion(ResizeRegion resizeRegion) {
		this.resizeRegion = resizeRegion;
	}

	public Boolean getShowTaskbarIcon() {
		return showTaskbarIcon;
	}

	public void setShowTaskbarIcon(Boolean showTaskbarIcon) {
		this.showTaskbarIcon = showTaskbarIcon;
	}

}
