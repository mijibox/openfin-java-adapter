package com.mijibox.openfin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.mijibox.openfin.bean.Identity;

public class FinView extends FinApiObject {
	
	FinView(FinConnectionImpl finConnection) {
		super(finConnection);
	}

	/**
	 * Returns a View object that represents an existing view.
	 * @param identity The identity of the view.
	 * @return new CompletionStage of the view object.
	 */
	public CompletionStage<FinViewObject> wrap(Identity identity) {
		return CompletableFuture.supplyAsync(()->{
			return new FinViewObject(this.finConnection, identity);
		}, this.finConnection.executor);
	}
	
}
