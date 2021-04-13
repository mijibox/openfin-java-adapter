package com.mijibox.openfin.bean;

import java.util.ArrayList;
import java.util.List;

public class ContentNavigation extends FinJsonBean {
	private List<String> whitelist;
	private List<String> blacklist;

	public List<String> getWhitelist() {
		return whitelist;
	}

	public void setWhitelist(List<String> whitelist) {
		this.whitelist = whitelist;
	}

	public List<String> getBlacklist() {
		return blacklist;
	}

	public void setBlacklist(List<String> blacklist) {
		this.blacklist = blacklist;
	}

	public void addWhitelist(String item) {
		if (this.whitelist == null) {
			this.whitelist = new ArrayList<>();
		}
		this.whitelist.add(item);
	}

	public void removeWhiteList(String item) {
		if (this.whitelist != null) {
			this.whitelist.remove(item);
		}
	}

	public void addBlacklist(String item) {
		if (this.blacklist == null) {
			this.blacklist = new ArrayList<>();
		}
		this.blacklist.add(item);
	}

	public void removeBlackList(String item) {
		if (this.blacklist != null) {
			this.blacklist.remove(item);
		}
	}

}
