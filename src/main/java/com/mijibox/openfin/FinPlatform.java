package com.mijibox.openfin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.PlatformOptions;

public class FinPlatform extends FinApiObject {

	private static Logger logger = LoggerFactory.getLogger(FinPlatform.class);

	FinPlatform(FinConnectionImpl finConnection) {
		super(finConnection);
	}
	
	/**
	 * Returns a Platform object that represents an existing platform.
	 * @param uuid UUID of the platform.
	 * @return Platform object with given UUID.
	 */
	public CompletionStage<FinPlatformObject> wrap(String uuid) {
		return this.finConnection._application.wrap(new Identity(uuid)).thenApply(appObj->{
			return new FinPlatformObject(appObj);
		});
	}

	/**
	 * Creates and starts a Platform and returns a wrapped and running Platform
	 * instance. The wrapped Platform methods can be used to launch content into the
	 * platform. Promise will reject if the platform is already running.
	 * 
	 * @param platformOptions
	 *            The required options object, also any Application option is also a
	 *            valid platform option.
	 * @return new CompletionStage for the platform that was started.
	 */
	public CompletionStage<FinPlatformObject> start(PlatformOptions platformOptions) {
		CompletableFuture<?> platformApiReadyCallback = new CompletableFuture<>();
		return this.finConnection._application.create(platformOptions).thenCompose(appObj->{
			appObj.addEventListener("platform-api-ready", event -> {
				platformApiReadyCallback.complete(null);
			});
			return this.finConnection._application.run(appObj, null).thenCombine(platformApiReadyCallback, (platform, nothing) -> {
				FinPlatformObject platformObj = new FinPlatformObject(appObj);
				return platformObj;
			});
		});
	}

	/**
	 * Retrieves platforms's manifest and returns a wrapped and running Platform. If there is a snapshot in the manifest, it will be launched into the platform.
	 * @param manifestUrl The URL of platform's manifest.
	 * @return new CompletionStage for the platform that was started.
	 */
	public CompletionStage<FinPlatformObject> startFromManifest(String manifestUrl) {
		CompletableFuture<?> platformApiReadyCallback = new CompletableFuture<>();
		return this.finConnection._application.createFromManifest(manifestUrl).thenApply(appObj -> {
			appObj.addEventListener("platform-api-ready", actionEvent -> {
				platformApiReadyCallback.complete(null);
			});
			this.finConnection._application.run(appObj, manifestUrl);
			return appObj;
		}).thenCombine(platformApiReadyCallback, (appObj, nothing) -> {
			FinPlatformObject platformObj = new FinPlatformObject(appObj);
			return platformObj;
		});
	}

}
