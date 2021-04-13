package com.mijibox.openfin.fdc3;

import com.mijibox.openfin.bean.FinJsonBean;

public class AppIntent extends FinJsonBean {

	private IntentMetadata intent;
	private AppMetadata[] apps;

	public IntentMetadata getIntent() {
		return intent;
	}

	public void setIntent(IntentMetadata intent) {
		this.intent = intent;
	}

	public AppMetadata[] getApps() {
		return apps;
	}

	public void setApps(AppMetadata[] apps) {
		this.apps = apps;
	}

}
