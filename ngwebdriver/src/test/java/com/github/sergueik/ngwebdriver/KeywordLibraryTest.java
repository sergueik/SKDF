package com.github.sergueik.ngwebdriver;
/**
 * Copyright 2017 Serguei Kouzmine
 */

// 	import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.sergueik.ngwebdriver.KeywordLibrary;

/**
 * Test for Launcher for Selenium WebDriver Keyword Driven Library
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class KeywordLibraryTest {

	// NOTE: Launcher methods are static
	private static KeywordLibrary keywordLibrary;
	private static String testCase;
	private static String defaultTestCase = "TestCase.xls";
	private static int statusColumn;
	private static int defaultStatusColumn = 9;

	// application configuration file
	private static String propertiesFileName = "application.properties";

	private static Map<String, String> propertiesMap = new HashMap<>();

	private static String osName = getOsName();
	static Map<String, String> defaultBrowsers = new HashMap<>();
	static {
		defaultBrowsers.put("windows", "chrome");
		defaultBrowsers.put("linux", "firefox");
		defaultBrowsers.put("mac", "safari");
	}
	static Map<String, String> browserDrivers = new HashMap<>();
	static {
		browserDrivers.put("chrome", "chromeDriverPath");
		browserDrivers.put("firefox", "geckoDriverPath");
		browserDrivers.put("edge", "edgeDriverPath");
		browserDrivers.put("ie", "ieDriverPath");
		browserDrivers.put("safari", null);
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		// Load property file from project directory (not from the jar)
		propertiesMap = PropertiesParser
				.getProperties(String.format("%s/src/main/resources/%s",
						System.getProperty("user.dir"), propertiesFileName));
		String browser = (propertiesMap.get("browser") != null)
				? propertiesMap.get("browser") : defaultBrowsers.get(osName);

		statusColumn = (propertiesMap.get("statusColumn") != null)
				? Integer.parseInt(propertiesMap.get("statusColumn"))
				: defaultStatusColumn;
		testCase = (propertiesMap.get("testCase") != null)
				? propertiesMap.get("testCase")
				: getPropertyEnv("testCase", String.format("%s\\Desktop\\%s",
						System.getenv("USERPROFILE"), defaultTestCase));
		keywordLibrary = KeywordLibrary.Instance();
		Launcher.setKeywordLibrary(keywordLibrary);
		Launcher.setBrowser(browser);
		Launcher.setOsName(osName);
		Launcher.setTestCase(testCase);
		Launcher.setStatusColumn(statusColumn);
		Launcher.setPropertiesMap(propertiesMap);
	}

	public static String getPropertyEnv(String name, String defaultValue) {
		String value = System.getProperty(name);
		if (value == null) {
			value = System.getenv(name);
			if (value == null) {
				value = defaultValue;
			}
		}
		return value;
	}

	@AfterClass
	public static void afterClass() {
	}

	@Test
	public void test() throws IOException {
		Launcher.run(testCase, statusColumn);
	}

	public static String getOsName() {
		if (osName == null) {
			osName = System.getProperty("os.name");
		}
		return osName.toLowerCase();
	}
}