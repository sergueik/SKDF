package com.github.sergueik.jprotractor;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import com.github.sergueik.junitparams.Utils;
import com.github.sergueik.jprotractor.KeywordLibrary;

/**
 * Standalone Launcher for Selenium WebDriver Keyword Driven Library
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class Launcher {

	private static boolean debug = true;
	// set loadEmptyColumns to true when observed
	// java.lang.NullPointerException or
	// java.lang.reflect.InvocationTargetException
	// due to incorrectly loading sparse spreadsheets
	// NOTE: the loadEmptyColumns attribute is only supported starting with
	// 0.0.8-SNAPSHOT of com.github.sergueik.junitparams
	private static boolean loadEmptyColumns = false;
	private static String defaultTestCase = "TestCase.xls";
	private static String testCase;
	private static String suiteName;

	public static void setTestCase(String testCase) {
		Launcher.testCase = testCase;
	}

	private static int statusColumn;

	public static void setStatusColumn(int statusColumn) {
		Launcher.statusColumn = statusColumn;
	}

	private static int defaultStatusColumn = 10;

	// parameter definition duplication from the fact the Launcher can be used
	// stand alone or through junit

	// application configuration file
	private static String propertiesFileName = "application.properties";

	private static Map<String, String> propertiesMap = new HashMap<>();

	public static void setPropertiesMap(Map<String, String> propertiesMap) {
		Launcher.propertiesMap = propertiesMap;
	}

	private static String osName = getOsName();

	public static void setOsName(String osName) {
		Launcher.osName = osName;
	}

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

	private static Utils utils = Utils.getInstance();

	public static void main(String[] args) throws IOException {

		// Load property file from project directory (not from the jar)
		// TODO: refactor
		propertiesMap = PropertiesParser
				.getProperties(String.format("%s/src/main/resources/%s",
						System.getProperty("user.dir"), propertiesFileName));
		if (debug) {
			System.err.println("Loading properties map from "
					+ String.format("%s/src/main/resources/%s",
							System.getProperty("user.dir"), propertiesFileName));
			for (String propertyKey : propertiesMap.keySet()) {
				System.err.println(
						"Property: " + propertyKey + " " + propertiesMap.get(propertyKey));
			}
		}
		String browser = (propertiesMap.get("browser") != null)
				? propertiesMap.get("browser") : defaultBrowsers.get(osName);

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
				readsuiteTestStepsWIP(suiteName);
				// TODO: remove legacy code
				// readsuiteTestSteps(suiteName);
			}
		}
		/*
				FileInputStream file = new FileInputStream(testCase);
				HSSFWorkbook workbook = new HSSFWorkbook(file);
				HSSFSheet indexSheet = workbook.getSheet("Index");
				for (int row = 1; row <= indexSheet.getLastRowNum(); row++) {
					Row indexRow = indexSheet.getRow(row);
					suiteName = safeCellToString(indexRow.getCell(0));
					suiteStatus = safeCellToString(indexRow.getCell(1));
					if (suiteStatus.equalsIgnoreCase("yes") && !suiteName.isEmpty()) {
						if (debug) {
							System.err.println("Reading suite: " + suiteName);
						}
						readsuiteTestSteps(suiteName);
					}
				}
				workbook.close();
		*/
		if (debug) {
			System.err.println("Done");
		}

	}

	// WIP
	private static void readsuiteTestStepsWIP(String suiteName)
			throws IOException {
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
				if (row[col] != null && StringUtils.isNotBlank(row[col].toString().trim())) {
					String cellValue = row[col].toString();
					data.put(String.format("param%d", col), cellValue);
					if (debug) {
						System.err.println("Column[param" + col + "] = " + cellValue);
					}
				}
			}
			KeywordLibrary.callMethod(keyword, data);
			writeStatus(suiteName, step + 1);
		}
	}

	private static void readsuiteTestSteps(String suiteName) throws IOException {
		Map<Integer, Map<String, String>> steps = readSteps(suiteName);

		for (int step = 0; step < steps.size(); step++) {
			Map<String, String> data = steps.get(step);
			for (String param : new ArrayList<String>(data.keySet())) {
				if (data.get(param) == null) {
					data.remove(param);
				}
			}
			String keyword = data.get("keyword");
			KeywordLibrary.callMethod(keyword, data);
			writeStatus(suiteName, step + 1);
		}
	}

	private static void verifyKeywordLibrary() {
		String[] expected = new String[] { "CLEAR_TEXT", "SWITCH_FRAME", "SET_TEXT",
				"WAIT_URL_CHANGE", "CLICK_CHECKBOX", "SEND_KEYS", "VERIFY_TEXT",
				"CLICK_RADIO", "GET_ATTR", "CLICK_LINK", "GET_TEXT", "COUNT_ELEMENTS",
				"ELEMENT_PRESENT", "GOTO_URL", "CLICK_BUTTON", "CLOSE_BROWSER",
				"CREATE_BROWSER", "VERIFY_ATTR", "WAIT_ELEMENT_CLICAKBLE", "CLICK",
				"WAIT", "SELECT_OPTION", "VERIFY_TAG" };
		Set<String> result = new HashSet<>();
		result = KeywordLibrary.getKeywords();
		if (debug) {
			for (String keyword : result) {
				System.err.println("verify: " + keyword);
			}
		}
		Set<String> dataSet = new HashSet<String>(Arrays.asList(expected));
		assertTrue(result.containsAll(dataSet));
	}

	public static void setBrowser(String browser) {
		System.err.println("Setting browser: " + browser);
		String browserDriver = browserDrivers.get(browser.toLowerCase());
		KeywordLibrary.setBrowser(browser);
		if (debug) {
			System.err.println(String.format("Setting %s driver (%s): %s", browser,
					browserDriver, propertiesMap.get(browserDriver)));
		}
		// the method name is browser name specific
		if (browser.matches("chrome")) {
			KeywordLibrary.setChromeDriverPath(propertiesMap.get(browserDriver));
		}

		if (browser.matches("firefox")) {
			KeywordLibrary.setGeckoDriverPath(propertiesMap.get(browserDriver));
		}
		if (browser.matches("edge")) {
			KeywordLibrary.setEdgeDriverPath(propertiesMap.get(browserDriver));
		}
		if (browser.matches("ie")) {
			KeywordLibrary.setIeDriverPath(propertiesMap.get(browserDriver));
		}
	}

	public static void writeStatus(String sheetName, int rowNumber)
			throws IOException {
		File file = new File(testCase);

		FileInputStream istream = new FileInputStream(file);
		HSSFWorkbook workbook = new HSSFWorkbook(istream);
		HSSFSheet sheet = workbook.getSheet(sheetName);
		Row row = sheet.getRow(rowNumber);
		Cell cell = row.createCell(statusColumn);
		cell.setCellValue(KeywordLibrary.getStatus());

		FileOutputStream ostream = new FileOutputStream(file);
		workbook.write(ostream);
		ostream.close();
		workbook.close();
	}

	// reads the spreadsheet into a hash of step keywords and parameters indexed
	// by column number and step number
	public static Map<Integer, Map<String, String>> readSteps(String sheetName)
			throws IOException {
		Map<String, String> data = new HashMap<>();
		Map<Integer, Map<String, String>> stepDataMap = new HashMap<>();
		System.err.println("Reading test case: " + testCase);
		FileInputStream file = new FileInputStream(testCase);

		HSSFWorkbook workbook = new HSSFWorkbook(file);
		HSSFSheet testcaseSheet = workbook.getSheet(sheetName);
		Row stepRow;
		Cell stepCell;
		for (int row = 1; row <= testcaseSheet.getLastRowNum(); row++) {
			if (debug) {
				System.err.println("Row: " + row);
			}
			data = new HashMap<>();
			stepRow = testcaseSheet.getRow(row);
			stepCell = stepRow.getCell(0);
			if (stepCell != null && stepCell.getCellTypeEnum() != CellType._NONE
					&& stepCell.getCellTypeEnum() != CellType.BLANK
					&& !stepRow.getCell(0).getStringCellValue().trim().isEmpty()) {
				data.put("keyword", stepCell.getStringCellValue());
				if (debug) {
					System.err.println("Keyword:" + stepCell.getStringCellValue());
				}
				for (int col = 1; col < statusColumn; col++) {
					stepCell = stepRow.getCell(col);
					if (col == statusColumn) {
						continue;
					}
					String cellValue = null;
					try {
						cellValue = safeCellToString(stepCell);
					} catch (NullPointerException | IllegalStateException e) {
						System.err.println("Exception (ignored): " + e.toString());
						cellValue = "";
					}
					if (cellValue != null) {
						if (debug) {
							System.err.println("Column[param" + col + "] = " + cellValue);
						}
						data.put(String.format("param%d", col), cellValue);
					}
				}
				stepDataMap.put(row - 1, data);
			}
		}
		workbook.close();
		return stepDataMap;
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
