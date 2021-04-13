package com.mijibox.openfin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.WindowOptions;

public class FinWindow extends FinApiObject {

	FinWindow(FinConnectionImpl finConnection) {
		super(finConnection);
	}

	/**
	 * Asynchronously returns a Window object that represents an existing window.
	 * @param identity
	 * @return
	 */
	public CompletionStage<FinWindowObject> wrap(Identity identity) {
		return CompletableFuture.completedStage(new FinWindowObject(this.finConnection, identity));
	}
}
