package com.github.sergueik.ngwebdriver;
/**
 * Copyright 2017,2018 Serguei Kouzmine
 */
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;

import com.github.sergueik.ngwebdriver.KeywordLibrary;
import com.github.sergueik.junitparams.Utils;

/**
 * Standalone Launcher for Selenium WebDriver Keyword Driven Library
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class Launcher {

	private static String propertiesFileName = "application.properties";

	private static String defaultTestCase = "TestCase.xls";
	private static String testCase;
	private static String suiteName;

	public static void setTestCase(String testCase) {
		Launcher.testCase = testCase;
	}

	private static int statusColumn;
	private static int defaultStatusColumn = 9;

	public static void setStatusColumn(int statusColumn) {
		Launcher.statusColumn = statusColumn;
	}

	private static KeywordLibrary keywordLibrary;

	public static void setKeywordLibrary(KeywordLibrary keywordLibrary) {
		Launcher.keywordLibrary = keywordLibrary;
	}

	private static Utils utils = Utils.getInstance();

	private static boolean debug = false;
	// set loadEmptyColumns to true when observed
	// java.lang.NullPointerException or
	// java.lang.reflect.InvocationTargetException
	// due to incorrectly loading sparse spreadsheets
	// NOTE: the loadEmptyColumns attribute is only supported starting with
	// 0.0.8-SNAPSHOT of com.github.sergueik.junitparams
	private static boolean loadEmptyColumns = false;

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
		browserDrivers.put("safari", null);
	}

	private static Map<String, String> propertiesMap = new HashMap<>();

	public static void setPropertiesMap(Map<String, String> propertiesMap) {
		Launcher.propertiesMap = propertiesMap;
	}

	private static String osName = getOsName();

	public static void setOsName(String osName) {
		Launcher.osName = osName;
	}

	public static void main(String[] args) throws IOException {

		propertiesMap = PropertiesParser
				.getProperties(String.format("%s/src/main/resources/%s",
						System.getProperty("user.dir"), propertiesFileName));
		String browser = (propertiesMap.get("browser") != null)
				? propertiesMap.get("browser") : defaultBrowsers.get(osName);

		keywordLibrary = KeywordLibrary.Instance();
		setBrowser(browser);
		statusColumn = (propertiesMap.get("statusColumn") != null)
				? Integer.parseInt(propertiesMap.get("statusColumn"))
				: defaultStatusColumn;
		testCase = (propertiesMap.get("testCase") != null)
				? propertiesMap.get("testCase")
				: getPropertyEnv("testCase", String.format("%s\\Desktop\\%s",
						System.getenv("USERPROFILE"), defaultTestCase));

		run(testCase, statusColumn);

	}

	public static void run(String testCase, int statusColumn) throws IOException {
		if (debug) {
			verifyKeywordLibrary();
		}
		utils.setDebug(debug);
		utils.setLoadEmptyColumns(loadEmptyColumns);

		System.err.println("Loading test case from: " + testCase);
		utils.setSheetName("Index");
		List<Object[]> result = utils.createDataFromExcel2003(testCase);
		String suiteStatus = null;
		for (Object[] indexRow : result) {
			suiteName = (String) indexRow[0];
			suiteStatus = (String) indexRow[1];

			if (suiteStatus.equalsIgnoreCase("yes") && !suiteName.isEmpty()) {
				if (debug) {
					System.err.println("Loading test suite : " + suiteName);
				}
				readsuiteTestSteps(suiteName);
			}
		}
		if (debug) {
			System.err.println("Done");
		}
	}

	private static void readsuiteTestSteps(String suiteName) throws IOException {
		utils.setSheetName(suiteName);
		List<Object[]> steps = utils.createDataFromExcel2003(testCase);
		for (int step = 0; step < steps.size(); step++) {
			Object[] row = steps.get(step);
			Map<String, String> data = new HashMap<String, String>();
			String keyword = (String) row[0];
			if (debug) {
				System.err.println("Keyword:" + keyword);
			}
			for (int col = 1; col < row.length; col++) {
				if (col == statusColumn) {
					continue;
				}
				if (row[col] != null
						&& StringUtils.isNotBlank(row[col].toString().trim())) {
					String cellValue = row[col].toString();
					data.put(String.format("param%d", col), cellValue);
					if (debug) {
						System.err.println("Column[param" + col + "] = " + cellValue);
					}
				}
			}
			keywordLibrary.callMethod(keyword, data);
			writeStatus(suiteName, step + 1);
		}
	}

	private static void verifyKeywordLibrary() {
		String[] expected = new String[] { "VERIFY_TAG" };
		Set<String> result = new HashSet<>();
		result = keywordLibrary.getKeywords();
		if (debug) {
			for (String keyword : result) {
				System.err.println("verify: " + keyword);
			}
		}
		Set<String> dataSet = new HashSet<String>(Arrays.asList(expected));
		assertTrue(result.containsAll(dataSet));
	}

	public static void setBrowser(String browser) {
		// TODO: debug
		String browserDriver = browserDrivers.get(browser.toLowerCase());
		String browserDriverPath = propertiesMap.get(browserDriver);
		Launcher.keywordLibrary.setBrowser(browser);
		if (debug) {
			System.err.println(String.format("Setting browser driver path: %s \"%s\"",
					browserDriver, browserDriverPath));
		}
		if (browser.matches("chrome")) {
			keywordLibrary.setChromeDriverPath(browserDriverPath);
		}
		if (browser.matches("firefox")) {
			keywordLibrary.setGeckoDriverPath(browserDriverPath);
		}
		if (browser.matches("edge")) {
			keywordLibrary.setEdgeDriverPath(browserDriverPath);
		}
		if (browser.matches("ie")) {
			keywordLibrary.setIeDriverPath(browserDriverPath);
		}
	}

	@SuppressWarnings("deprecation")
	public static void writeStatus(String sheetName, int rowNumber)
			throws IOException {
		File file = new File(testCase);

		FileInputStream istream = new FileInputStream(file);
		HSSFWorkbook workbook = new HSSFWorkbook(istream);
		HSSFSheet sheet = workbook.getSheet(sheetName);
		Row row = sheet.getRow(rowNumber);
		Cell cell = row.createCell(statusColumn);
		String status = keywordLibrary.getStatus();
		cell.setCellValue(status);
		// https://stackoverflow.com/questions/10912578/apache-poi-xssfcolor-from-hex-code
		// https://github.com/rahulrathore44/ExcelReportGenerator
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFillForegroundColor(
				(status.matches("(?i:Passed)")) ? HSSFColor.BRIGHT_GREEN.index
						: (status.matches("(?i:Failed)")) ? HSSFColor.RED.index
								: HSSFColor.WHITE.index);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cell.setCellStyle(cellStyle);

		try {

			FileOutputStream ostream = new FileOutputStream(file);
			workbook.write(ostream);
			ostream.close();
			workbook.close();
		} catch (FileNotFoundException e) {
			System.err.println("Exception (ignored) " + e.toString());
		}
	}

	// Safe conversion of type Excel cell object to String value
	public static String safeCellToString(org.apache.poi.ss.usermodel.Cell cell) {

		if (cell == null) {
			return null;
		}
		CellType type = cell.getCellTypeEnum();
		Object result;
		switch (type) {
		case _NONE:
			result = null;
			break;
		case NUMERIC:
			result = cell.getNumericCellValue();
			break;
		case STRING:
			result = cell.getStringCellValue();
			break;
		case FORMULA:
			throw new IllegalStateException("The formula cell is not supported");
		case BLANK:
			result = null;
			break;
		case BOOLEAN:
			result = cell.getBooleanCellValue();
			break;
		case ERROR:
			throw new RuntimeException("Cell has an error");
		default:
			throw new IllegalStateException(
					"Cell type: " + type + " is not supported");
		}
		return (result == null) ? null : result.toString();
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

	public static String getOsName() {
		if (osName == null) {
			osName = System.getProperty("os.name");
		}
		return osName.toLowerCase();
	}
}
