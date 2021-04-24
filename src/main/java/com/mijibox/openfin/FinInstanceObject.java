package com.mijibox.openfin;

import com.mijibox.openfin.bean.Identity;

public class FinInstanceObject extends FinApiObject {

	final protected Identity identity;

	FinInstanceObject(FinConnectionImpl finConnection, Identity identity) {
		super(finConnection);
		this.identity = identity;
	}

	/**
	 * Gets the identity of this instance.
	 * @return The identity of this instance.
	 */
	public Identity getIdentity() {
		return identity;
	}

}
