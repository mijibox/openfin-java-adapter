package com.mijibox.openfin.bean;

public class WindowInfo extends FinJsonBean {
	private Boolean canNavigateBack;
	private Boolean canNavigateForward;
	private PreloadScript[] preloadScripts;
	private String title;
	private String url;

	public Boolean getCanNavigateBack() {
		return canNavigateBack;
	}

	public void setCanNavigateBack(Boolean canNavigateBack) {
		this.canNavigateBack = canNavigateBack;
	}

	public Boolean getCanNavigateForward() {
		return canNavigateForward;
	}

	public void setCanNavigateForward(Boolean canNavigateForward) {
		this.canNavigateForward = canNavigateForward;
	}

	public PreloadScript[] getPreloadScripts() {
		return preloadScripts;
	}

	public void setPreloadScripts(PreloadScript[] preloadScripts) {
		this.preloadScripts = preloadScripts;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
