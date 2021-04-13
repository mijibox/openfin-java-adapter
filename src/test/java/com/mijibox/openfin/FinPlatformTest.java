package com.mijibox.openfin;

import static com.mijibox.openfin.TestUtils.runSync;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonString;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.FinEvent;
import com.mijibox.openfin.FinLayoutObject;
import com.mijibox.openfin.FinPlatformObject;
import com.mijibox.openfin.FinRuntime;
import com.mijibox.openfin.FinViewObject;
import com.mijibox.openfin.bean.ApplySnapshotOptions;
import com.mijibox.openfin.bean.AutoResizeOptions;
import com.mijibox.openfin.bean.Bounds;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.LayoutConfig;
import com.mijibox.openfin.bean.LayoutItem;
import com.mijibox.openfin.bean.PlatformOptions;
import com.mijibox.openfin.bean.RuntimeConfig;
import com.mijibox.openfin.bean.Snapshot;
import com.mijibox.openfin.bean.ViewOptions;
import com.mijibox.openfin.bean.WindowOptions;

public class FinPlatformTest {
	private final static Logger logger = LoggerFactory.getLogger(FinPlatformTest.class);
	private static String version = "stable-v17";
	private static FinRuntime fin;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		fin = TestUtils.getOpenFinRuntime(version);
	}

	@AfterClass
	public static void teardownAfterClass() throws Exception {
		TestUtils.runSync(fin.System.exit());
	}

	@Test
	public void startThenQuit() throws Exception {
		PlatformOptions platformOptions = new PlatformOptions(UUID.randomUUID().toString());
		FinPlatformObject platformObj = TestUtils.runSync(fin.Platform.start(platformOptions));
		assertNotNull(platformObj);
		try {
			TestUtils.runSync(platformObj.quit());
		}
		catch (Exception ex) {
			//openfin bug
		}
	}

	@Test
	public void startFromManifestThenQuit() throws Exception {
		FinPlatformObject platformObj = TestUtils.runSync(fin.Platform.startFromManifest("https://openfin.github.io/platform-api-project-seed/public.json"));
		assertNotNull(platformObj);
		try {
			TestUtils.runSync(platformObj.quit());
		}
		catch (Exception ex) {
			//openfin bug
		}
	}

	@Test
	public void createView() throws Exception {
		PlatformOptions platformOptions = new PlatformOptions(UUID.randomUUID().toString());
		FinPlatformObject platformObj = TestUtils.runSync(fin.Platform.start(platformOptions));
		assertNotNull(platformObj);
		ViewOptions viewOpts = new ViewOptions();
		viewOpts.setName("myViewName");
		viewOpts.setUrl("https://www.bing.com");
		
		FinViewObject viewObj1 = TestUtils.runSync(platformObj.createView(viewOpts, null));
		
		CompletableFuture<FinViewObject> ofViewObj2Future = new CompletableFuture<>();
		
		viewObj1.addEventListener(FinViewObject.EVENT_TARGET_CHANGED, e->{
			viewObj1.getCurrentWindow().thenCompose(view1Window->{
				ViewOptions viewOpts2 = new ViewOptions();
				logger.debug("platform window identity: {}", view1Window.getIdentity());
				viewOpts2.setName("myViewName2");
				viewOpts2.setUrl("https://www.google.com");
				return platformObj.createView(viewOpts2, view1Window.getIdentity()).thenAccept(v->{
					ofViewObj2Future.complete(v);
				});
			});
		});
		
		FinViewObject viewObj2 = TestUtils.runSync(ofViewObj2Future);
		assertNotNull(viewObj2);

		try {
			TestUtils.runSync(platformObj.quit());
		}
		catch (Exception ex) {
			//openfin bug
		}
	}
	
	@Test
	public void createWindow() throws Exception {
		PlatformOptions platformOptions = new PlatformOptions(UUID.randomUUID().toString());
		FinPlatformObject platformObj = TestUtils.runSync(fin.Platform.start(platformOptions));
		assertNotNull(platformObj);
		WindowOptions winOpts = new WindowOptions(UUID.randomUUID().toString());
		winOpts.setUrl("https://www.google.com");
		winOpts.setAutoShow(true);
		TestUtils.runSync(platformObj.createWindow(winOpts));
		try {
			TestUtils.runSync(platformObj.quit());
		}
		catch (Exception ex) {
			//openfin bug
		}
	}
	
	@Test
	public void wrap() throws Exception {
		String platformUuid = UUID.randomUUID().toString();
		PlatformOptions platformOptions = new PlatformOptions(platformUuid);
		FinPlatformObject platformObj = TestUtils.runSync(fin.Platform.start(platformOptions));
		assertNotNull(platformObj);
		ViewOptions viewOpts = new ViewOptions();
		viewOpts.setName("myViewName");
		viewOpts.setUrl("https://www.bing.com");
		
		FinViewObject viewObj1 = TestUtils.runSync(platformObj.createView(viewOpts, null));
		
		CompletableFuture<FinViewObject> ofViewObj2Future = new CompletableFuture<>();
		
		viewObj1.addEventListener(FinViewObject.EVENT_TARGET_CHANGED, e->{
			viewObj1.getCurrentWindow().thenCompose(view1Window->{
				ViewOptions viewOpts2 = new ViewOptions();
				logger.debug("platform window identity: {}", view1Window.getIdentity());
				viewOpts2.setName("myViewName2");
				viewOpts2.setUrl("https://www.google.com");
				return fin.Platform.wrap(platformUuid).thenAccept(newPlatformObj->{
					newPlatformObj.createView(viewOpts2, view1Window.getIdentity()).thenAccept(v->{
						ofViewObj2Future.complete(v);
					});
				});
			});
		});
		
		FinViewObject viewObj2 = TestUtils.runSync(ofViewObj2Future);
		assertNotNull(viewObj2);

		try {
			TestUtils.runSync(platformObj.quit());
		}
		catch (Exception ex) {
			//openfin bug
		}
	}
	
	@Test
	public void listenToViewEvents() throws Exception {
		String uuid = UUID.randomUUID().toString();
		String viewName = "myViewName";
		PlatformOptions platformOptions = new PlatformOptions(uuid);
		FinPlatformObject platformObj = TestUtils.runSync(fin.Platform.start(platformOptions));
		assertNotNull(platformObj);
		ViewOptions viewOpts = new ViewOptions();
		viewOpts.setName(viewName);
		viewOpts.setUrl("https://www.google.com");
		
		CompletableFuture<FinEvent> viewEventFuture = new CompletableFuture<>();

		fin.View.wrap(new Identity(uuid, viewName)).thenAccept(viewObj->{
			viewObj.addEventListener(FinViewObject.EVENT_CREATED, e->{
				viewEventFuture.complete(e);
			});
		}).thenCompose(v->{
			return platformObj.createView(viewOpts, null);
		});
		
		FinEvent eventObj = TestUtils.runSync(viewEventFuture);
		assertNotNull(eventObj);
		
		logger.debug("view created event: {}", eventObj);

		try {
			TestUtils.runSync(platformObj.quit());
		}
		catch (Exception ex) {
			//openfin bug
		}
	}
	
	@Test
	public void getLayoutConfig() throws Exception {
		String uuid = UUID.randomUUID().toString();
		String viewName = "myViewName";
		PlatformOptions platformOptions = new PlatformOptions(uuid);
		FinPlatformObject platformObj = TestUtils.runSync(fin.Platform.start(platformOptions));
		assertNotNull(platformObj);
		ViewOptions viewOpts = new ViewOptions();
		viewOpts.setName(viewName);
		viewOpts.setUrl("https://www.google.com");
		
		CompletableFuture<LayoutConfig> layoutConfigFuture = new CompletableFuture<>();

		fin.View.wrap(new Identity(uuid, viewName)).thenAccept(viewObj->{
			viewObj.addEventListener(FinViewObject.EVENT_TARGET_CHANGED, e->{
				
				viewObj.getCurrentWindow().thenCompose(winObj->{
					return winObj.getLayout();
				}).thenCompose(layoutObj->{
					return layoutObj.getConfig();
				}).thenAccept(config->{
					layoutConfigFuture.complete(config);
				});
			
			});
		}).thenCompose(v->{
			return platformObj.createView(viewOpts, null);
		});
		
		LayoutConfig layoutConfig = TestUtils.runSync(layoutConfigFuture);
		assertNotNull(layoutConfig);
		logger.debug("layoutConfig: {}", layoutConfig.getFromJson());

		try {
			TestUtils.runSync(platformObj.quit());
		}
		catch (Exception ex) {
			//openfin bug
		}
	}
	
	@Test
	public void applyLayout() throws Exception {
		String uuid = UUID.randomUUID().toString();
		PlatformOptions platformOptions = new PlatformOptions(uuid);
		FinPlatformObject platformObj = TestUtils.runSync(fin.Platform.start(platformOptions));
		assertNotNull(platformObj);
		
		CompletableFuture<?> doneTestFuture = new CompletableFuture<>();

		ViewOptions viewOpts = new ViewOptions();
		viewOpts.setUrl("https://www.google.com");

		platformObj.createView(viewOpts, null).thenAccept(viewObj->{
			viewObj.addEventListener(FinViewObject.EVENT_TARGET_CHANGED, e->{
				viewObj.getCurrentWindow().thenAccept(winObj->{
					Identity winIdentity = winObj.getIdentity();
					CompletionStage<?> testsFuture = null;
					for (int i=0; i<4; i++) {
						if (testsFuture == null) {
							testsFuture = platformObj.createView(viewOpts, winIdentity);
						}
						else {
							testsFuture = testsFuture.thenCompose(v->{
								try {
									Thread.sleep(1000);
								}
								catch (InterruptedException e1) {
									e1.printStackTrace();
								}
								return platformObj.createView(viewOpts, winIdentity);
							});
						}
					}
					FinLayoutObject.PresetLayout[] presets = { FinLayoutObject.PresetLayout.COLUMNS, FinLayoutObject.PresetLayout.GRID,
							FinLayoutObject.PresetLayout.ROWS, FinLayoutObject.PresetLayout.TABS };
					
					for (int i=0; i<presets.length; i++) {
						int idx = i;
						testsFuture = testsFuture.thenCompose(v->{
							try {
								Thread.sleep(1000);
							}
							catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							return winObj.getLayout().thenAccept(layoutObj->{
								layoutObj.applyPreset(presets[idx]);
							});
							
						});
					}
					
					testsFuture.thenAccept(v->{
						try {
							Thread.sleep(1000);
						}
						catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						doneTestFuture.complete(null);
					});
					
				});
			});
		});
		
		TestUtils.runSync(doneTestFuture, 30);
	}
	@Test
	public void replaceLayout() throws Exception {
		//create number of tabs, change the layout to grid then replace it to single view then finally replace it back to previous layout
		String uuid = UUID.randomUUID().toString();
		PlatformOptions platformOptions = new PlatformOptions(uuid);
		FinPlatformObject platformObj = TestUtils.runSync(fin.Platform.start(platformOptions));
		assertNotNull(platformObj);
		
		CompletableFuture<?> doneTestFuture = new CompletableFuture<>();

		ViewOptions viewOpts = new ViewOptions();
		viewOpts.setUrl("https://www.google.com");
		viewOpts.setPreventDragOut(true);

		platformObj.createView(viewOpts, null).thenAccept(viewObj->{
			viewObj.addEventListener(FinViewObject.EVENT_TARGET_CHANGED, e->{
				viewObj.getCurrentWindow().thenAccept(winObj->{
					Identity winIdentity = winObj.getIdentity();
					CompletionStage<?> testsFuture = null;
					for (int i=0; i<4; i++) {
						if (testsFuture == null) {
							testsFuture = platformObj.createView(viewOpts, winIdentity);
						}
						else {
							testsFuture = testsFuture.thenCompose(v->{
								return platformObj.createView(viewOpts, winIdentity);
							});
						}
					}
					
					testsFuture = testsFuture.thenCompose(v->{
						return winObj.getLayout().thenAccept(layoutObj->{
							layoutObj.applyPreset(FinLayoutObject.PresetLayout.GRID);
						});
					});
					
					
					testsFuture = testsFuture.thenCompose(v->{
						try {
							Thread.sleep(1000);
						}
						catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						return winObj.getLayout().thenCompose(layoutObj->{
							return layoutObj.getConfig().thenCompose(existingLayoutConfig->{
								//config->stack->component
								//1. component
								LayoutItem itemComponent = new LayoutItem(LayoutItem.TYPE_COMPONENT);
								itemComponent.setComponentState(viewOpts);
								//2. stack
								LayoutItem itemStack = new LayoutItem(LayoutItem.TYPE_STACK);
								itemStack.add(itemComponent);
								//3. config
								LayoutConfig newConfig = new LayoutConfig();
								newConfig.add(itemStack);
								
								return layoutObj.replace(newConfig).thenCompose(v1->{
									try {
										Thread.sleep(1000);
									}
									catch (InterruptedException e1) {
										e1.printStackTrace();
									}
									return layoutObj.replace(existingLayoutConfig).thenAccept(v2->{
										try {
											Thread.sleep(1000);
										}
										catch (InterruptedException e1) {
											e1.printStackTrace();
										}
										doneTestFuture.complete(null);
									});
								}).exceptionally(ex->{
									ex.printStackTrace();
									return null;
								});
							});
							
						});
					});
				});
			});
		});
		
		TestUtils.runSync(doneTestFuture, 30);
	}
	
	@Test
	public void viewGetOptions() throws Exception {
		String uuid = UUID.randomUUID().toString();
		PlatformOptions platformOptions = new PlatformOptions(uuid);
		FinPlatformObject platformObj = TestUtils.runSync(fin.Platform.start(platformOptions));
		assertNotNull(platformObj);

		ViewOptions viewOpts = new ViewOptions();
		viewOpts.setUrl("https://www.google.com");
		
		FinViewObject viewObj = TestUtils.runSync(platformObj.createView(viewOpts, null));
		
		ViewOptions opts = TestUtils.runSync(viewObj.getOptions());
		
		assertNotNull(opts);
		assertEquals(viewOpts.getUrl(), opts.getUrl());
		
		logger.debug("opts: {}", opts);
		
	}

	@Test
	public void viewUpdateOptions() throws Exception {
		String uuid = UUID.randomUUID().toString();
		PlatformOptions platformOptions = new PlatformOptions(uuid);
		FinPlatformObject platformObj = TestUtils.runSync(fin.Platform.start(platformOptions));
		assertNotNull(platformObj);

		String oldCustomData = "oldCustomData";
		String newCustomData = "newCustomData";
		
		ViewOptions viewOpts = new ViewOptions();
		viewOpts.setUrl("https://www.google.com");
		viewOpts.setIsClosable(false);
		viewOpts.setCustomData(Json.createValue(oldCustomData));
		
		FinViewObject viewObj = TestUtils.runSync(platformObj.createView(viewOpts, null));

		Thread.sleep(1000);
		
		TestUtils.runSync(viewObj.navigate("https://www.apple.com").thenCompose(v->{
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			ViewOptions newOptions = new ViewOptions();
			newOptions.setCustomData(Json.createValue(newCustomData));
			return viewObj.updateOptions(newOptions).thenCompose(v2->{
				viewOpts.setIsClosable(true);
				return viewObj.getCurrentWindow().thenCompose(winObj->{
					return platformObj.createView(viewOpts, winObj.getIdentity());
				});
			});
		}));
		
		ViewOptions opts = TestUtils.runSync(viewObj.getOptions());
		assertNotNull(opts);
		assertNotNull(opts.getCustomData());
		assertEquals(newCustomData, ((JsonString)opts.getCustomData()).getString());
	}

	@Test
	public void closeView() throws Exception {
		String uuid = UUID.randomUUID().toString();
		PlatformOptions platformOptions = new PlatformOptions(uuid);
		FinPlatformObject platformObj = TestUtils.runSync(fin.Platform.start(platformOptions));
		assertNotNull(platformObj);

		ViewOptions viewOpts = new ViewOptions();
		viewOpts.setUrl("https://www.google.com");
		
		FinViewObject viewObj = TestUtils.runSync(platformObj.createView(viewOpts, null).thenCompose(view->{
			CompletableFuture<FinViewObject> viewShownFuture = new CompletableFuture<>();
			view.addEventListener(FinViewObject.EVENT_TARGET_CHANGED, e->{
				viewShownFuture.complete(view);
			});
			return viewShownFuture;
		}));
		
		TestUtils.runSync(platformObj.closeView(viewObj));
	}

	@Test
	public void getSnapshot() throws Exception {
		String uuid = UUID.randomUUID().toString();
		PlatformOptions platformOptions = new PlatformOptions(uuid);
		FinPlatformObject platformObj = TestUtils.runSync(fin.Platform.start(platformOptions));
		assertNotNull(platformObj);

		ViewOptions viewOpts = new ViewOptions();
		viewOpts.setUrl("https://www.google.com");
		
		FinViewObject viewObj = TestUtils.runSync(platformObj.createView(viewOpts, null).thenCompose(view->{
			CompletableFuture<FinViewObject> viewShownFuture = new CompletableFuture<>();
			view.addEventListener(FinViewObject.EVENT_TARGET_CHANGED, e->{
				viewShownFuture.complete(view);
			});
			return viewShownFuture;
		}));
		
		Snapshot snapshot = TestUtils.runSync(platformObj.getSnapshot());
		assertNotNull(snapshot);
		assertNotNull(snapshot.getSnapshotDetails().getRuntimeVersion());
		logger.debug("snapshot.fromJson: {}", snapshot.getFromJson());
		logger.debug("snapshot.runtimeVersioin: {}", snapshot.getSnapshotDetails().getRuntimeVersion());
	}

	@Test
	public void applySnapshot() throws Exception {
		String uuid = UUID.randomUUID().toString();
		PlatformOptions platformOptions = new PlatformOptions(uuid);
		FinPlatformObject platformObj = TestUtils.runSync(fin.Platform.start(platformOptions));
		assertNotNull(platformObj);

		ViewOptions viewOpts = new ViewOptions();
		viewOpts.setUrl("https://www.google.com");
		
		FinViewObject viewObj = TestUtils.runSync(platformObj.createView(viewOpts, null).thenCompose(view->{
			CompletableFuture<FinViewObject> viewShownFuture = new CompletableFuture<>();
			view.addEventListener(FinViewObject.EVENT_TARGET_CHANGED, e->{
				viewShownFuture.complete(view);
			});
			return viewShownFuture;
		}));
		
		Snapshot snapshot = TestUtils.runSync(platformObj.getSnapshot());
		assertNotNull(snapshot);
		Thread.sleep(1000);
		//took the snapshot, create two other views and close existing view;
		TestUtils.runSync(platformObj.createView(viewOpts, null));
		TestUtils.runSync(platformObj.createView(viewOpts, null));
		TestUtils.runSync(platformObj.closeView(viewObj));
		Thread.sleep(1000);
		//apply the old snapshot back
		TestUtils.runSync(platformObj.applySnapshot(snapshot, null));
		Thread.sleep(1000);
		ApplySnapshotOptions opts = new ApplySnapshotOptions();
		opts.setCloseExistingWindows(true);
		TestUtils.runSync(platformObj.applySnapshot(snapshot, opts));
		Thread.sleep(1000);
	}
}
