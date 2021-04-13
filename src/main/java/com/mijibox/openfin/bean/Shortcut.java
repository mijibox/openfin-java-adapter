package com.mijibox.openfin.bean;

import javax.json.bind.annotation.JsonbProperty;

public class Shortcut extends FinJsonBean {

	private String company;
	private String description;
	@JsonbProperty("diagnostics-shortcut")
	private Boolean diagnosticsShortcut;
	private Boolean force;
	private String icon;
	private String name;
	private String startMenuRootFolder;
	private String target;
	@JsonbProperty("uninstall-shortcut")
	private Boolean uninstallShortcut;

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getDiagnosticsShortcut() {
		return diagnosticsShortcut;
	}

	public void setDiagnosticsShortcut(Boolean diagnosticsShortcut) {
		this.diagnosticsShortcut = diagnosticsShortcut;
	}

	public Boolean getForce() {
		return force;
	}

	public void setForce(Boolean force) {
		this.force = force;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartMenuRootFolder() {
		return startMenuRootFolder;
	}

	public void setStartMenuRootFolder(String startMenuRootFolder) {
		this.startMenuRootFolder = startMenuRootFolder;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Boolean getUninstallShortcut() {
		return uninstallShortcut;
	}

	public void setUninstallShortcut(Boolean uninstallShortcut) {
		this.uninstallShortcut = uninstallShortcut;
	}

}
