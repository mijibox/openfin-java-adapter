package com.mijibox.openfin.fdc3;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.json.Json;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.FinRuntime;
import com.mijibox.openfin.TestUtils;
import com.mijibox.openfin.fdc3.AppIntent;
import com.mijibox.openfin.fdc3.FinDesktopAgent;
import com.mijibox.openfin.fdc3.IntentResolution;
import com.mijibox.openfin.fdc3.channel.ContextChannel;
import com.mijibox.openfin.fdc3.context.Context;
import com.mijibox.openfin.fdc3.context.InstrumentContext;

public class FinDesktopAgentTest {
	private final static Logger logger = LoggerFactory.getLogger(FinDesktopAgentTest.class);
	private static FinRuntime fin;
	private static FinDesktopAgent fdc3;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		fin = TestUtils.getOpenFinRuntime("stable", true);
		fdc3 = new FinDesktopAgent(fin);
	}

	@AfterClass
	public static void teardownAfterClass() throws Exception {
		TestUtils.runSync(fin.System.exit());
	}
	
	@Test
	public void open() throws Exception {
		TestUtils.runSync(fdc3.open("fdc3-charts-blue", null));
	}
	
	@Test
	public void raiseIntent() throws Exception {
		Context context = new Context("fdc3.instrument");
		context.setName("IBM");
		context.setId(Json.createObjectBuilder().add("ticker", "ibm").build());
		IntentResolution intentResolution = TestUtils.runSync(fdc3.raiseIntent("ViewChart", context, "fdc3-charts-red"));
		assertNotNull(intentResolution);
	}
	
	@Test
	public void findContextIntents() throws Exception {
		InstrumentContext context = new InstrumentContext();
		List<AppIntent> intents = TestUtils.runSync(fdc3.findIntentsByContext(context));
		assertNotNull(intents);
		for (AppIntent intent: intents) {
			logger.debug("intent: {}", intent);
		}
	}
	
	@Test
	public void findIntent() throws Exception {
		AppIntent intent = TestUtils.runSync(fdc3.findIntent("ViewChart"));
		assertNotNull(intent);
		logger.debug("intent: {}", intent);
		InstrumentContext context = new InstrumentContext();
		AppIntent intent2 = TestUtils.runSync(fdc3.findIntent("ViewChart", context));
		assertNotNull(intent2);
		logger.debug("intent2: {}", intent2);
	}
	
	@Test
	public void broadcast() throws Exception {
		InstrumentContext context = new InstrumentContext();
		TestUtils.runSync(fdc3.broadcast(context));
	}
	
	@Test
	public void getCurrentChannel() throws Exception {
		ContextChannel channel = TestUtils.runSync(fdc3.getCurrentChannel());
		assertNotNull(channel);
		logger.debug("current channel: {}", channel.getClass().getName());
	}
	
}
