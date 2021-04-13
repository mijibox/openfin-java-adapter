package com.mijibox.openfin;

import com.mijibox.openfin.bean.Identity;

public class FinInstanceObject extends FinApiObject {

	final protected Identity identity;

	FinInstanceObject(FinConnectionImpl finConnection, Identity identity) {
		super(finConnection);
		this.identity = identity;
	}

	public Identity getIdentity() {
		return identity;
	}

}
