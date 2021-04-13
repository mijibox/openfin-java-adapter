package com.mijibox.openfin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.mijibox.openfin.bean.Identity;

public class FinLayout extends FinApiObject {

	FinLayout(FinConnectionImpl finConnection) {
		super(finConnection);
	}

	/**
	 * Returns a Layout object that represents a Window's layout.
	 * @param identity window identity
	 * @return OfLayout object that represents a Window's layout.
	 */
	public CompletionStage<FinLayoutObject> wrap(Identity identity) {
		return CompletableFuture.completedStage(new FinLayoutObject(this.finConnection, identity));
	}
}
