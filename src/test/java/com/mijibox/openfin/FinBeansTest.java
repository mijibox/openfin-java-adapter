package com.mijibox.openfin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.FinJsonBean;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.Runtime;
import com.mijibox.openfin.bean.RuntimeConfig;

public class FinBeansTest {
	final static Logger logger = LoggerFactory.getLogger(FinBeansTest.class);
	
	@Test
	public void runtimeConfigTest() throws Exception {
		String licenseKey = "AABB";
		String version = "stable";
		String customPropName = "MyProp";
		String customPropValue = "MyValue";
		String runtimeCustomPropName = "runtimeCustomPropName";
		String runtimeCustomPropValue = "runtimeCustomPropValue";
		Integer devtoolsPort = 1235;
		
		RuntimeConfig config = new RuntimeConfig();
		config.setLicenseKey(licenseKey);
		config.setDevtoolsPort(devtoolsPort);
		
		Runtime runtime = new Runtime() {
			public String[] customPropName = {"customPropValue1", "customPropValue2","customPropValue3" };
		};
		runtime.setVersion(version);
		
		config.setRuntime(runtime);
		
		JsonbConfig jsonbConfig = new JsonbConfig();
		
		Jsonb jsonb = JsonbBuilder.create(jsonbConfig);
		
		String jsonString = jsonb.toJson(config);
		
		System.out.println(jsonString);
		
		RuntimeConfig dupConfig =jsonb.fromJson(jsonString, RuntimeConfig.class);
		
		assertEquals(licenseKey, config.getLicenseKey());
		assertEquals(config.getLicenseKey(), dupConfig.getLicenseKey());
		assertEquals(devtoolsPort, config.getDevtoolsPort());
		assertEquals(config.getDevtoolsPort(), dupConfig.getDevtoolsPort());
		assertEquals(version, config.getRuntime().getVersion());
		assertEquals(config.getRuntime().getVersion(), dupConfig.getRuntime().getVersion());
		assertEquals(config.getRuntime().getVersion(), dupConfig.getRuntime().getVersion());
	}
	
	@Test
	public void toJsonObject() throws Exception {
		RuntimeConfig config = new RuntimeConfig();
		JsonObject jsonObj = FinBeanUtils.toJsonObject(config);
		assertNotNull(jsonObj);
		assertEquals("stable", config.getRuntime().getVersion());
		assertEquals("stable", jsonObj.getJsonObject("runtime").getString("version"));
	}
	
	@Test
	public void sameIdentity() throws Exception {
		Identity i1 = new Identity("AAA", "BBB");
		Identity i2 = new Identity("AAA", "BBB");
		
		assertEquals(i1, i2);
		
		HashSet<Identity> identitySet = new HashSet<>();
		identitySet.add(i1);
		
		assertTrue(identitySet.contains(i2));
	}

	@Test
	public void sameEmptyIdentity() throws Exception {
		Identity i1 = new Identity();
		Identity i2 = new Identity();
		
		assertEquals(i1, i2);
		
		HashSet<Identity> identitySet = new HashSet<>();
		identitySet.add(i1);
		
		assertTrue(identitySet.contains(i2));
	}

	@Test
	public void differentIdentity() throws Exception {
		Identity i1 = new Identity();
		Identity i2 = new Identity("AAA", "BBB");
		
		assertNotEquals(i1, i2);
		
		HashSet<Identity> identitySet = new HashSet<>();
		identitySet.add(i1);
		
		assertFalse(identitySet.contains(i2));
	}
	
	@Test
	public void getFromJsonObject() throws Exception {
		String version = "stable";
		RuntimeConfig config = new RuntimeConfig();
		config.setLicenseKey("MyLicenseKey");
		config.getRuntime().setVersion(version);
		
		JsonObject configJson = FinBeanUtils.toJsonObject(config);
		RuntimeConfig dupConfig = FinBeanUtils.fromJsonString(configJson.toString(), RuntimeConfig.class);
		assertNotNull(dupConfig);
		assertNotNull(dupConfig.getFromJson());
		logger.debug("dupConfig.fromJson: {}", dupConfig.getFromJson());
		assertNotNull(dupConfig.getRuntime().getFromJson());
		logger.debug("dupConfig.getRuntime().getFromJson: {}", dupConfig.getRuntime().getFromJson());
	}
	
	public static class TestClass extends FinJsonBean {
		public Object id;
		public String type = "MyType";
	}
	
	@Test
	public void jsonObjectToObject() throws Exception {
		TestClass obj = new TestClass();
		obj.id = Json.createObjectBuilder().add("ticker", "IBM").add("amount", 12345).build();
		
		JsonObject jsonObj = FinBeanUtils.toJsonObject(obj);
		logger.debug("jsonObject: {}", jsonObj);
		
		TestClass dupObj = FinBeanUtils.fromJsonObject(jsonObj, TestClass.class);
		logger.debug("dupObj.id.class: {}", dupObj.id.getClass());
		if (dupObj.id instanceof HashMap) {
			HashMap idMap = (HashMap) dupObj.id;
			idMap.keySet().forEach(key->{
				logger.debug("key.class: {}, toString: {}", key.getClass(), key.toString());
				logger.debug("value.class: {}, toString: {}", idMap.get(key).getClass(),  idMap.get(key).toString());
			});
		}
	}
}
