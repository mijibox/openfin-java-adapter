package com.mijibox.openfin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.CompletionStage;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.FinRuntime;

public class FinClipboardTest {
	private final static Logger logger = LoggerFactory.getLogger(FinClipboardTest.class);
	
	private static FinRuntime fin;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		fin = TestUtils.getOpenFinRuntime();
	}

	@AfterClass
	public static void teardownAfterClass() throws Exception {
		TestUtils.dispose(fin);
	}

	@Test
	public void getAvailableFormats() throws Exception {
		String[] formats = TestUtils.runSync(fin.Clipboard.getAvailableFormats());
		assertNotNull(formats);
		formats = TestUtils.runSync(fin.Clipboard.getAvailableFormats(fin.Clipboard.TYPE_CLIPBOARD));
		assertNotNull(formats);
	}
	
	@Test
	public void htmlWriteThenRead() throws Exception {
		String html = "<html><body><h4>htmlWriteThenRead</h4></body></html>";
		TestUtils.runSync(fin.Clipboard.writeHtml(html));
		String gotHtml = TestUtils.runSync(fin.Clipboard.readHtml());
		assertNotNull(gotHtml);
	}
	
	@Test
	public void textWriteThenRead() throws Exception {
		String text = "textWriteThenRead";
		TestUtils.runSync(fin.Clipboard.writeText(text));
		String gotText = TestUtils.runSync(fin.Clipboard.readText());
		assertEquals(text, gotText);
	}
	
	@Test
	public void rtfWriteThenRead() throws Exception {
		String rtf = "\\b1rtfWriteThenRead\\b0";
		TestUtils.runSync(fin.Clipboard.writeRtf(rtf));
		String gotRtf = TestUtils.runSync(fin.Clipboard.readRtf());
		assertNotNull(gotRtf);
	}
	
	@Test
	public void writeThenReadAll() throws Exception {
		String html = "<html><body><h4>htmlWriteThenRead</h4></body></html>";
		String text = "textWriteThenRead";
		String rtf = "\\b1rtfWriteThenRead\\b0";
		TestUtils.runSync(fin.Clipboard.write(fin.Clipboard.TYPE_CLIPBOARD, text, html, rtf));
		String gotText = TestUtils.runSync(fin.Clipboard.readText());
		String gotHtml = TestUtils.runSync(fin.Clipboard.readHtml());
		String gotRtf = TestUtils.runSync(fin.Clipboard.readRtf());
		assertEquals(text, gotText);
		assertNotNull(gotHtml);
		assertNotNull(gotRtf);
	}

}
