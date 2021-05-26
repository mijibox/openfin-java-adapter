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

	/**
	 * Register an action to be called.
	 * @param action Name of the action.
	 * @param listener The action listener. 
	 */
	public void register(String action, FinChannelAction listener) {
		this.actionMap.put(action, listener);
	}
	
	/**
	 * Remove an action by action name.
	 * @param action The action name.
	 */
	public void remove(String action) {
		this.actionMap.remove(action);
	}
	
	JsonValue processAction(String action, JsonValue payload, Identity senderIdentity) {
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
	
	/**
	 * Checks the availability of specified action.
	 * @param action The action name
	 * @return true if it has such action registered.
	 */
	public boolean hasAction(String action) {
		return this.actionMap.containsKey(action) || this.defaultAction != null;
	}

	/**
	 * Gets the default action.
	 * @return The default action, null if not set.
	 */
	public FinChannelMiddleware getDefaultAction() {
		return defaultAction;
	}

	/**
	 * Sets a default action. This is used any time an action that has not been registered is invoked. Default behavior if not set is to throw an error.
	 * @param defaultAction Action to be executed when specified action name that has not been registered.
	 */
	public void setDefaultAction(FinChannelMiddleware defaultAction) {
		this.defaultAction = defaultAction;
	}

	/**
	 * Gets the error handler.
	 * @return the error handler
	 */
	public FinChannelMiddleware getOnError() {
		return onError;
	}

	/**
	 * Register an error handler. This is called before responding on any error.
	 * @param onError Action to be executed in case of an error.
	 */
	public void setOnError(FinChannelMiddleware onError) {
		this.onError = onError;
	}

	/**
	 * Gets the "beforeAction" handler.
	 * @return the "beforeAction" handler
	 */
	public FinChannelMiddleware getBeforeAction() {
		return beforeAction;
	}

	/**
	 * Registers the middleware that fires before the action.
	 * @param beforeAction Action to be executed before invoking the action.
	 */
	public void setBeforeAction(FinChannelMiddleware beforeAction) {
		this.beforeAction = beforeAction;
	}

	/**
	 * Gets the "afterAction" handler.
	 * @return the "afterAction" handler
	 */
	public FinChannelMiddleware getAfterAction() {
		return afterAction;
	}

	/**
	 * Registers the middleware that fires after the action.
	 * @param afterAction Action to be executed after invoking the action.
	 */
	public void setAfterAction(FinChannelMiddleware afterAction) {
		this.afterAction = afterAction;
	}
	
}
