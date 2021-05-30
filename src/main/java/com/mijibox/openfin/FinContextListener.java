package com.mijibox.openfin;

import com.mijibox.openfin.bean.Context;

@FunctionalInterface
public interface FinContextListener {
	public void onContext(Context context);
}
