package com.mijibox.openfin;

import java.util.concurrent.ConcurrentHashMap;

import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.Identity;

public class FinChannelBase extends FinApiObject {
	private final static Logger logger = LoggerFactory.getLogger(FinChannelBase.class);
	
	protected ConcurrentHashMap<String, FinChannelAction> actionMap;
	protected FinChannelMiddleware defaultAction;
	protected FinChannelMiddleware onError;
	protected FinChannelMiddleware beforeAction;
	protected FinChannelMiddleware afterAction;

	FinChannelBase(FinConnectionImpl finConnection) {
		super(finConnection);
		this.actionMap = new ConcurrentHashMap<>();
	}

	public void register(String action, FinChannelAction listener) {
		this.actionMap.put(action, listener);
	}
	
	public void remove(String action) {
		this.actionMap.remove(action);
	}
	
	public JsonValue processAction(String action, JsonValue payload, Identity senderIdentity) {
		try {
			payload = this.beforeAction == null ? payload : this.beforeAction.invoke(action, payload, senderIdentity);
			if (this.actionMap.containsKey(action)) {
				payload = this.actionMap.get(action).invoke(payload, senderIdentity);
			}
			else {
				payload = this.defaultAction.invoke(action, payload, senderIdentity);
			}
			payload = this.afterAction == null ? payload : this.afterAction.invoke(action, payload, senderIdentity);
			return payload;
		}
		catch (Exception e) {
			logger.error("error processAction", e);
			//error processing action
			if (this.onError != null) {
				this.onError.invoke(action, payload, senderIdentity);
			}
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public boolean hasActionListener(String action) {
		return this.actionMap.containsKey(action) || this.defaultAction != null;
	}

	public FinChannelMiddleware getDefaultAction() {
		return defaultAction;
	}

	public void setDefaultAction(FinChannelMiddleware defaultAction) {
		this.defaultAction = defaultAction;
	}

	public FinChannelMiddleware getOnError() {
		return onError;
	}

	public void setOnError(FinChannelMiddleware onError) {
		this.onError = onError;
	}

	public FinChannelMiddleware getBeforeAction() {
		return beforeAction;
	}

	public void setBeforeAction(FinChannelMiddleware beforeAction) {
		this.beforeAction = beforeAction;
	}

	public FinChannelMiddleware getAfterAction() {
		return afterAction;
	}

	public void setAfterAction(FinChannelMiddleware afterAction) {
		this.afterAction = afterAction;
	}
	
}
