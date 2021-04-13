package com.mijibox.openfin.bean;

import java.util.List;

public class Snapshot extends FinJsonBean {
	private List<WindowOptions> windows;
	private SnapshotDetails snapshotDetails;

	public List<WindowOptions> getWindows() {
		return windows;
	}

	public void setWindows(List<WindowOptions> windows) {
		this.windows = windows;
	}

	public SnapshotDetails getSnapshotDetails() {
		return snapshotDetails;
	}

	public void setSnapshotDetails(SnapshotDetails snapshotDetails) {
		this.snapshotDetails = snapshotDetails;
	}

}
