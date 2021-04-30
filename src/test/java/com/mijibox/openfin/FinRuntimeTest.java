package com.mijibox.openfin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.FinRuntime;

public class FinRuntimeTest {
	
	private final static Logger logger = LoggerFactory.getLogger(FinRuntimeTest.class);
	
	@Test
	public void init() throws Exception {
		FinRuntime fin = TestUtils.getOpenFinRuntime();
		assertNotNull(fin.Application);
		assertNotNull(fin.Channel);
		assertNotNull(fin.Clipboard);
		assertNotNull(fin.GlobalHotkey);
		assertNotNull(fin.InterApplicationBus);
		assertNotNull(fin.Layout);
		assertNotNull(fin.Platform);
		assertNotNull(fin.System);
		assertNotNull(fin.View);
		assertNotNull(fin.Window);
		TestUtils.dispose(fin);
	}
	
	@Test
	public void multipleConnections() throws Exception {
		Thread.sleep(5000);
		int cnt = 20;
		String[] versions = { "stable", "stable-v18", "stable-v17", "stable-v16", "stable-v15" };
		ExecutorService threadPool = Executors.newCachedThreadPool();
		CountDownLatch latch = new CountDownLatch(cnt);
		for (int i = 0; i < cnt; i++) {
			Thread.sleep(100);
			int index = i;
			CompletableFuture.runAsync(() -> {
				FinLauncher.newLauncherBuilder()
						.runtimeVersion(versions[index % versions.length])
						.build().launch().thenComposeAsync(runtime -> {
							return runtime.System.getVersion().thenAccept(v -> {
								logger.debug("runtime: {}, version: {}", runtime.getConnectionUuid(), v);
								latch.countDown();
								runtime.disconnect();
							});
						});
			}, threadPool);
		}

		latch.await(30, TimeUnit.SECONDS);
		assertEquals(0, latch.getCount());
	}
}
