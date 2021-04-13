package com.mijibox.openfin.bean;

public class ApplySnapshotOptions {
	private Boolean closeExistingWindows;
	private Boolean skipOutOfBoundsCheck;

	public Boolean getCloseExistingWindows() {
		return closeExistingWindows;
	}

	public void setCloseExistingWindows(Boolean closeExistingWindows) {
		this.closeExistingWindows = closeExistingWindows;
	}

	public Boolean getSkipOutOfBoundsCheck() {
		return skipOutOfBoundsCheck;
	}

	public void setSkipOutOfBoundsCheck(Boolean skipOutOfBoundsCheck) {
		this.skipOutOfBoundsCheck = skipOutOfBoundsCheck;
	}

}
