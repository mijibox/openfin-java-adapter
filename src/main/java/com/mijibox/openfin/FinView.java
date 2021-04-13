package com.mijibox.openfin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.mijibox.openfin.bean.Identity;

public class FinView extends FinApiObject {
	
	FinView(FinConnectionImpl finConnection) {
		super(finConnection);
	}

	public CompletionStage<FinViewObject> wrap(Identity identity) {
		return CompletableFuture.completedStage(new FinViewObject(this.finConnection, identity));
	}
	
}
