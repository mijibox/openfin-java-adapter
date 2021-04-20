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
		TestUtils.runSync(fin.System.exit());
	}
	
	@Test
	public void multiThread() throws Exception {
		FinRuntime fin = TestUtils.getOpenFinRuntime();
		
		try {
			int threadCount = 50;
			CompletableFuture<?>[] futures = new CompletableFuture<?>[threadCount] ;
			for (int i=0; i<threadCount; i++) {
				futures[i] = fin.System.getRuntimeInfo().thenAccept(info->{
					logger.debug("runtime info: {}", info.getFromJson());
				}).toCompletableFuture();
			}
			CompletableFuture.allOf(futures);
		}
		finally {
			TestUtils.runSync(fin.System.exit());
		}
	}

	@Test
	public void multiThreadWait() throws Exception {
		FinRuntime fin = TestUtils.getOpenFinRuntime();
		
		try {
			int threadCount = 50;
			for (int i=0; i<threadCount; i++) {
				TestUtils.runSync(fin.System.getRuntimeInfo().thenAccept(info->{
					logger.debug("runtime info: {}", info.getFromJson());
				}));
			}
		}
		finally {
			TestUtils.runSync(fin.System.exit());
		}
	}

	@Test
	public void multiThreadChain() throws Exception {
		FinRuntime fin = TestUtils.getOpenFinRuntime();
		
		try {
			int threadCount = 50;
			CompletionStage<?> future = null;
			for (int i=0; i<threadCount; i++) {
				if (future == null) {
					future = fin.System.getRuntimeInfo().thenAccept(info->{
						logger.debug("runtime info: {}", info.getFromJson());
					});
				}
				else {
					future = future.thenCompose(v->{
						return fin.System.getRuntimeInfo().thenAccept(info->{
							logger.debug("runtime info: {}", info.getFromJson());
						});
					});
				}
			}
			TestUtils.runSync(future);
		}
		finally {
			TestUtils.runSync(fin.System.exit());
		}
	}
	
	@Test
	public void multipleConnections() throws Exception {
		int cnt = 20;
		String[] versions = {"stable", "stable-v18", "stable-v17", "stable-v16", "stable-v15"};
		CountDownLatch latch = new CountDownLatch(cnt);
		for (int i=0; i<cnt; i++) {
			int index = i;
			FinLauncher.newLauncherBuilder()
				.runtimeVersion(versions[index%versions.length])
				.build().launch().thenComposeAsync(runtime->{
				return runtime.System.getVersion().thenAccept(v->{
					logger.debug("runtime: {}, version: {}", runtime.getConnectionUuid(), v);
					latch.countDown();
				});
			});
		}
		
		latch.await(30, TimeUnit.SECONDS);
		assertEquals(0, latch.getCount());
	}
}
