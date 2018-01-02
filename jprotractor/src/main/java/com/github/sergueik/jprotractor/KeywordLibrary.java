package com.github.sergueik.jprotractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.github.sergueik.jprotractor.NgBy;
import com.github.sergueik.jprotractor.NgWebDriver;

/**
 * Keyword Driven Library for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public final class KeywordLibrary {

	private static boolean debug = false;
	private static Class<?> _class = null;
	public static WebDriver driver;
	public static WebDriverWait wait;
	public static Actions actions;
	private static NgWebDriver ngDriver;
	private static WebElement element;

	private static Pattern pattern;
	private static Matcher matcher;
	private static By locator;
	private static long timeout;

	private static String status;
	private static String result;
	private static String selectorTagName = null;
	private static String selectorType = null;
	private static String selectorValue = null;
	private static String selectorRow = null;
	private static String selectorColumn = null;
	private static String selectorContainedText = null;
	private static String expectedValue = null;
	private static String textData = null;
	private static String visibleText = null;
	private static String expectedText = null;
	private static String expectedTag = null;
	private static String attributeName = null;
	private static String param1;
	private static String param2;
	private static String param3;

	public static int scriptTimeout = 5;
	public static int stepWait = 150;
	public static int flexibleWait = 120;
	public static int implicitWait = 1;
	public static long pollingInterval = 500;

	private static String browser = "chrome";
	private static String osName = getOsName();
	private static String chromeDriverPath = null;
	private static String geckoDriverPath = null;
	private static String firefoxBrowserPath = null;
	private static String ieDriverPath = null;
	private static String edgeDriverPath = null;

	private static Map<String, String> methodTable = new HashMap<>();
	static {
		methodTable.put("CLEAR_TEXT", "clearText");
		methodTable.put("CLICK", "clickButton");
		methodTable.put("CLICK_BUTTON", "clickButton");
		methodTable.put("CLICK_CHECKBOX", "clickCheckBox");
		methodTable.put("CLICK_LINK", "clickLink");
		methodTable.put("CLICK_RADIO", "clickRadioButton");
		methodTable.put("CLOSE_BROWSER", "closeBrowser");
		methodTable.put("COUNT_ELEMENTS", "countElements");
		methodTable.put("CREATE_BROWSER", "openBrowser");
		methodTable.put("ELEMENT_PRESENT", "elementPresent");
		methodTable.put("GET_ATTR", "getElementAttribute");
		methodTable.put("GET_TEXT", "getElementText");
		methodTable.put("GOTO_URL", "navigateTo");
		methodTable.put("SELECT_OPTION", "selectDropDown");
		methodTable.put("SEND_KEYS", "enterText");
		methodTable.put("SET_TEXT", "enterText");
		methodTable.put("SWITCH_FRAME", "switchFrame");
		methodTable.put("VERIFY_ATTR", "verifyAttribute");
		methodTable.put("VERIFY_TAG", "verifyTag");
		methodTable.put("VERIFY_TEXT", "verifyText");
		methodTable.put("WAIT", "wait");
		methodTable.put("WAIT_ELEMENT_CLICAKBLE", "waitClickable");
		methodTable.put("WAIT_URL_CHANGE", "waitURLChange");
	}

	public static Set<String> getKeywords() {
		return KeywordLibrary.methodTable.keySet();
	}

	private static Map<String, Method> locatorTable = new HashMap<>();

	public static Set<String> getLocators() {
		if (_class == null) {
			initMethods();
		}
		return KeywordLibrary.locatorTable.keySet();
	}

	public static void closeBrowser(Map<String, String> params) {
		driver.quit();
	}

	public static void navigateTo(Map<String, String> params) {
		String url = params.get("param1");
		System.err.println("Navigate to: " + url);
		driver.navigate().to(url);
	}

	public static String getStatus() {
		return status;
	}

	public String getResult() {
		return result;
	}

	// https://stackoverflow.com/questions/7486012/static-classes-in-java
	// https://github.com/sergueik/selenium_java/commit/57724dafc4fa33339

	// A top-level Java class mimicking static class behavior
	// All methods are static
	private KeywordLibrary() { // private constructor
		_class = null;
	}

	public static void initMethods() {
		try {
			_class = Class.forName("com.github.sergueik.jprotractor.KeywordLibrary");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		// NOTE: java.util.ConcurrentModificationException
		for (String keyword : methodTable.keySet()
				.toArray(new String[methodTable.keySet().size()])) {
			if (methodTable.get(keyword).isEmpty()) {
				if (debug) {
					System.err.println("Removing keyword:" + keyword);
				}
				methodTable.remove(keyword);
			} else {
				try {
					_class.getMethod(methodTable.get(keyword), Map.class);
				} catch (NoSuchMethodException e) {
					if (debug) {
						System.err.println(
								"Removing  keyword:" + keyword + " exception: " + e.toString());
					}
					methodTable.remove(keyword);
				} catch (SecurityException e) {
					/* ignore*/
				}
			}
		}
		// NOTE: java.util.ConcurrentModificationException
		for (String methodName : methodTable.values()
				.toArray(new String[methodTable.values().size()])) {
			if (!methodTable.containsKey(methodName)) {
				if (debug) {
					System.err.println("Adding keyword for method itself: " + methodName);
				}
				methodTable.put(methodName, methodName);
			}
		}
		/*
		// optional: list all methods
		try {
			Class<?> _locatorHelper = Class.forName("org.openqa.selenium.By");
			Method[] _locatorMethods = _locatorHelper.getMethods();
			for (Method _locatorMethod : _locatorMethods) {
				System.err.println("Adding locator of org.openqa.selenium.By: "
						+ _locatorMethod.toString());
			}
		} catch (ClassNotFoundException | SecurityException e) {
		}
		*/
		/*
		// optional: list all methods
		try {
			Class<?> _locatorHelper = Class.forName("com.jprotractor.NgBy");
			Method[] _locatorMethods = _locatorHelper.getMethods();
			for (Method _locatorMethod : _locatorMethods) {
				System.err.println("Adding locator of com.jprotractor.NgBy:"
						+ _locatorMethod.toString());
			}
		} catch (ClassNotFoundException | SecurityException e) {
			System.out.println("Exception (ignored): " + e.toString());
		}
		*/
		// phony method
		Method methodMissing = null;
		try {
			// do we ever want to send correct arguments ?
			@SuppressWarnings("rawtypes")
			Class[] arguments = new Class[] { String.class, String.class };
			Constructor<Method> methodConstructor = Method.class
					.getDeclaredConstructor(arguments);
			methodConstructor.setAccessible(true);
			methodMissing = (Method) methodConstructor.newInstance();
		} catch (NoSuchMethodException | IllegalAccessException
				| InstantiationException | IllegalArgumentException
				| InvocationTargetException e) {
			System.out.println("Exception (ignored): " + e.toString());
		}
		try {
			locatorTable.put("className",
					By.class.getMethod("className", String.class));
			locatorTable.put("css", By.class.getMethod("cssSelector", String.class));
			locatorTable.put("id", By.class.getMethod("id", String.class));
			locatorTable.put("linkText",
					By.class.getMethod("linkText", String.class));
			locatorTable.put("name", By.class.getMethod("name", String.class));
			locatorTable.put("tagName", By.class.getMethod("tagName", String.class));
			locatorTable.put("xpath", By.class.getMethod("xpath", String.class));
		} catch (NoSuchMethodException e) {
		}
		try {
			locatorTable.put("binding",
					NgBy.class.getMethod("binding", String.class));
			locatorTable.put("buttontext",
					NgBy.class.getMethod("buttonText", String.class));
			locatorTable.put("cssContainingText", NgBy.class
					.getMethod("cssContainingText", String.class, String.class));
			locatorTable.put("model", NgBy.class.getMethod("model", String.class));
			locatorTable.put("options",
					NgBy.class.getMethod("options", String.class));
			locatorTable.put("repeater",
					NgBy.class.getMethod("repeater", String.class));
			locatorTable.put("repeaterCell", methodMissing);
			locatorTable.put("repeaterColumn",
					NgBy.class.getMethod("repeaterColumn", String.class, String.class));
			locatorTable.put("repeaterElement", NgBy.class.getMethod(
					"repeaterElement", String.class, Integer.class, String.class));
			locatorTable.put("repeaterRow", methodMissing);
			// NOTE: plural in the method name
			locatorTable.put("repeaterRows",
					NgBy.class.getMethod("repeaterRows", String.class, Integer.class));
			locatorTable.put("selectedOption",
					NgBy.class.getMethod("selectedOption", String.class));
			locatorTable.put("selectedRepeaterOption",
					NgBy.class.getMethod("selectedRepeaterOption", String.class));
		} catch (NoSuchMethodException e) {
			System.out.println("Execption (ignored): " + e.toString());
		}
		// put synthetic selectors explicitly
		locatorTable.put("text", methodMissing);
	}

	public static void callMethod(String keyword, Map<String, String> params) {
		if (_class == null) {
			initMethods();
		}
		if (methodTable.containsKey(keyword)) {
			String methodName = methodTable.get(keyword);
			try {
				System.out.println(keyword + " call method: " + methodName + " with "
						+ String.join(",", params.values()));
				_class.getMethod(methodName, Map.class).invoke(null, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No method found for keyword: " + keyword);
		}
	}

	public static void openBrowser(Map<String, String> params)
			throws IOException {
		try {
			switch (KeywordLibrary.browser) {
			case "chrome":
				System.setProperty("webdriver.chrome.driver",
						new File((chromeDriverPath == null)
								? osName.toLowerCase().startsWith("windows")
										? "C:\\java\\selenium\\chromedriver.exe"
										: "/var/run/chromedriver"
								: chromeDriverPath).getAbsolutePath());
				driver = new ChromeDriver();
				break;
			case "firefox":
				System.setProperty("webdriver.gecko.driver",
						new File((geckoDriverPath == null)
								? osName.toLowerCase().startsWith("windows")
										? "c:/java/selenium/geckodriver.exe"
										: "/var/run/geckodriver"
								: geckoDriverPath).getAbsolutePath());
				DesiredCapabilities capabilities = DesiredCapabilities.firefox();

				// TODO: switch to Selenium 3.X+
				capabilities.setCapability("marionette", false);
				driver = new FirefoxDriver(capabilities);
				break;
			case "ie":
				System.setProperty("webdriver.ie.driver",
						new File((ieDriverPath == null)
								? "c:/java/selenium/IEDriverServer.exe" : ieDriverPath)
										.getAbsolutePath());
				driver = new InternetExplorerDriver();
				break;
			case "edge":
				System.setProperty("webdriver.edge.driver",
						new File((edgeDriverPath == null)
								? "C:\\Program Files (x86)\\Microsoft Web Driver\\MicrosoftWebDriver.exe"
								: edgeDriverPath).getAbsolutePath());
				driver = new EdgeDriver();
				break;
			default:
				break;
			}
			if (debug) {
				System.err.println("Open: " + KeywordLibrary.getBrowser());
			}

			wait = new WebDriverWait(driver, flexibleWait);
			wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);
			ngDriver = new NgWebDriver(driver);
			// driver.get(config.getProperty("url"));
			driver.manage().window().maximize();
			status = "Passed";
		} catch (

		Exception e) {
			System.err.println("Exception: " + e.toString());
			status = "Failed";
		}
	}

	public static void countElements(Map<String, String> params) {
		List<WebElement> _elements = _findElements(params);
		textData = params.get("param5");
		if (_elements != null) {
			highlight(element);
			status = "Passed";
			result = String.format("%d", _elements.size());
		} else {
			status = "Failed";
			result = "-1";
		}
		System.err
				.println(String.format("%s returned \"%s\"", "countElements", result));
	}

	public static void enterText(Map<String, String> params) {
		element = _findElement(params);
		textData = params.get("param5");
		if (element != null) {
			highlight(element);
			element.sendKeys(textData);
			status = "Passed";
		} else {
			status = "Failed";
		}
	}

	public static void clickButton(Map<String, String> params) {
		element = _findElement(params);
		if (element != null) {
			highlight(element);
			element.click();
			status = "Passed";
		} else {
			status = "Failed";
		}
	}

	public static void clickLink(Map<String, String> params) {
		element = _findElement(params);
		// experimental
		// element = _waitFindElement(params);
		if (element != null) {
			highlight(element);
			System.err.println("click");
			element.click();
			status = "Passed";
		} else {
			System.err.println("Can't click");
			status = "Failed";
		}
		try {
			Thread.sleep(stepWait);
		} catch (InterruptedException e) {
			// System.err.println("Ignored: " + e.toString());
		}

	}

	public static void selectDropDown(Map<String, String> params) {
		Select select;
		visibleText = params.get("param5");
		element = _findElement(params);
		if (element != null) {
			highlight(element);
			select = new Select(element);
			try {
				select.selectByVisibleText(visibleText);
				status = "Passed";
			} catch (Exception e) {
				status = "Failed";
			}
		} else {
			status = "Failed";
		}
	}

	public static void verifyAttribute(Map<String, String> params) {
		boolean flag = false;
		attributeName = params.get("param5");
		expectedValue = params.get("param6");
		element = _findElement(params);
		if (element != null) {
			flag = element.getAttribute(attributeName).equals(expectedValue);
		}
		if (flag)
			status = "Passed";
		else
			status = "Failed";
	}

	public static void verifyText(Map<String, String> params) {
		boolean flag = false;
		expectedText = params.get("param5");
		element = _findElement(params);
		if (element != null) {
			highlight(element);
			flag = element.getText().equals(expectedText);
		}
		if (flag)
			status = "Passed";
		else
			status = "Failed";
	}

	public static void verifyTag(Map<String, String> params) {
		boolean flag = false;
		expectedTag = params.get("param5");
		element = _findElement(params);
		if (element != null) {
			highlight(element);
			flag = element.getText().equals(expectedText);
		}
		if (flag)
			status = "Passed";
		else
			status = "Failed";
	}

	public static void getElementText(Map<String, String> params) {
		String text = null;
		element = _findElement(params);
		if (element != null) {
			highlight(element);
			text = element.getText();
			status = "Passed";
			result = text;
			System.err.println(
					String.format("%s returned \"%s\"", "getElementText", result));
		} else {
			status = "Failed";
		}
	}

	public static void getElementAttribute(Map<String, String> params) {
		attributeName = params.get("param5");
		String value = null;
		element = _findElement(params);

		if (element != null) {
			highlight(element);
			value = element.getAttribute(attributeName);
			status = "Passed";
			result = value;
			System.err.println(
					String.format("%s returned \"%s\"", "getElementAttribute", result));
		} else {
			System.err.println("element not found");

			status = "Failed";
		}
	}

	public static void elementPresent(Map<String, String> params) {
		Boolean flag = false;
		element = _findElement(params);
		if (element != null) {
			flag = element.isDisplayed();
		}
		if (flag) {
			highlight(element);
			status = "Passed";
			result = "true";
		} else {
			status = "Failed";
			result = "false";
		}
	}

	public static void clickCheckBox(Map<String, String> params) {
		if (!params.containsKey("param5")) {
			element = _findElement(params);
			if (element != null) {
				highlight(element);
				element.click();
			}
		} else {
			expectedValue = params.get("param5");
			element = null;
			for (WebElement e : _findElements(params)) {
				if (e.getAttribute("value").equals(expectedValue)) {
					element = e;
				}
			}
		}
		if (element != null) {
			highlight(element);
			element.click();
			status = "Passed";
		} else {
			status = "Failed";
		}
	}

	public static void clickRadioButton(Map<String, String> params) {
		if (!params.containsKey("param5")) {
			element = _findElement(params);
		} else {
			expectedValue = params.get("param5");
			element = null;
			for (WebElement e : _findElements(params)) {
				if (e.getAttribute("value").equals(expectedValue)) {
					element = e;
				}
			}
		}
		if (element != null) {
			highlight(element);
			element.click();
			status = "Passed";
		} else {
			status = "Failed";
		}
	}

	// TODO:
	public static void switchFrame(Map<String, String> params) {
		param1 = params.get("param1");
		param2 = params.get("param2");
		param3 = params.get("param5");
		try {
			switch (param1) {
			case "name":
				driver.switchTo().frame(param2);
				break;
			case "id":
				driver.findElement(By.id(param2)).click();
				break;
			case "css":
				driver.findElement(By.cssSelector(param2)).click();
				System.out
						.println(driver.findElement(By.cssSelector(param2)).getText());
				break;
			case "xpath":
				driver.findElement(By.xpath(param2)).click();
				System.out.println(driver.findElement(By.xpath(param2)).getText());
				break;
			}
			status = "Passed";
		} catch (Exception e) {
			status = "Failed";
		}
	}

	// wrapped in expectedCondition of the appropriate @apply signature
	public static WebElement _waitFindElement(Map<String, String> params) {
		selectorType = params.get("param1");
		if (!locatorTable.containsKey(selectorType)) {
			throw new RuntimeException("Unknown Selector Type: " + selectorType);
		}
		/* TODO: objectRepo.getProperty(selectorValue) || selectorValue */
		selectorValue = params.get("param2");
		if (params.containsKey("param3")) {
			selectorRow = params.get("param3");
		}
		if (params.containsKey("param4")) {
			selectorColumn = params.get("param4");
		}

		if (params.containsKey("param5")) {
			selectorContainedText = params.get("param5");
		}
		if (params.containsKey("param6")) {
			selectorTagName = params.get("param6");
		}
		WebDriverWait _wait;
		if (params.containsKey("param7")) {
			timeout = (long) (Float.parseFloat(params.get("param7")));
			_wait = new WebDriverWait(driver, timeout);
		} else {
			_wait = wait;
		}
		WebElement _element = null;

		// NOTE: all keys, including synthetic ones e.g. 'css'
		pattern = Pattern.compile(
				"(?:css|cssSelector|id|linkText|name|partialLinkText|tagName|xpath)",
				Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(selectorType);
		if (matcher.find()) {
			switch (selectorType) {
			case "css":
				locator = By.cssSelector(selectorValue);
				break;
			case "cssSelector":
				locator = By.cssSelector(selectorValue);
				break;
			case "id":
				locator = By.id(selectorValue);
				break;
			case "linkText":
				locator = By.linkText(selectorValue);
				break;
			case "name":
				locator = By.name(selectorValue);
				break;
			case "partialLinkText":
				locator = By.partialLinkText(selectorValue);
				break;
			case "tagName":
				locator = By.tagName(selectorValue);
				break;
			case "xpath":
				locator = By.xpath(selectorValue);
				break;
			}
			// TODO: add Angular locators
			_element = _wait.until(new ExpectedCondition<WebElement>() {

				@Override
				public WebElement apply(WebDriver d) {
					Optional<WebElement> e = d.findElements(locator).stream().findFirst();
					/*
					if (e.isPresent()) {
						System.err.println("apply => " + selectorType + " => "
								+ e.get().getAttribute("outerHTML"));
					}
					*/
					return (e.isPresent()) ? e.get() : (WebElement) null;
				}
			});
			/* 
			  System.err
					.println("returned from _wait : " + _element.getAttribute("outerHTML"));
			*/
		} else if (selectorType == "text") {
			if (selectorTagName != null) {
				_element = _wait.until(new ExpectedCondition<WebElement>() {

					@Override
					public WebElement apply(WebDriver d) {
						return d.findElements(By.tagName(selectorTagName)).stream()
								.filter(o -> {
									return (Boolean) (o.getText().contains(selectorValue));
								}).findFirst().get();
					}
				});
			} else {
				String amendedSelectorValue = String.format(
						"//%s[contains(normalize-space(text()),'%s')]",
						(selectorTagName != null) ? selectorTagName : "*", selectorValue);
				_element = _wait.until(new ExpectedCondition<WebElement>() {
					@Override
					public WebElement apply(WebDriver d) {
						return d.findElements(By.xpath(amendedSelectorValue)).stream()
								.filter(o -> {
									return (Boolean) (o.getText().contains(selectorValue));
								}).findFirst().get();
					}
				});

			}

		}
		return _element;
	}

	// straight to WebDriver
	public static WebElement _findElement(Map<String, String> params) {
		selectorType = params.get("param1");
		if (!locatorTable.containsKey(selectorType)) {
			throw new RuntimeException("Unknown Selector Type: " + selectorType);
		}

		/* TODO: objectRepo.getProperty(selectorValue) || selectorValue */
		selectorValue = params.get("param2");
		if (params.containsKey("param3")) {
			selectorRow = params.get("param3");
		}
		if (params.containsKey("param4")) {
			selectorColumn = params.get("param4");
		}

		if (params.containsKey("param5")) {
			selectorContainedText = params.get("param5");
		}
		if (params.containsKey("param6")) {
			selectorTagName = params.get("param6");
		}
		if (debug) {
			System.err
					.println(String.format("selectorType: \"%s\", selectorValue: \"%s\"",
							selectorType, selectorValue));
		}
		WebElement _element = null;
		try {
			switch (selectorType) {

			case "binding":
				// case "exactBinding":
				_element = ngDriver.findElement(NgBy.binding(selectorValue));
				break;

			case "buttontext":
				_element = ngDriver.findElement(NgBy.buttonText(selectorValue));
				break;

			case "className":
				_element = driver.findElement(By.className(selectorValue));
				break;

			case "css":
				_element = driver.findElement(By.cssSelector(selectorValue));
				break;

			case "cssContainingText":
				_element = ngDriver.findElement(
						NgBy.cssContainingText(selectorValue, selectorContainedText));
				break;

			case "cssSelector":
				_element = driver.findElement(By.cssSelector(selectorValue));
				break;

			case "id":
				_element = driver.findElement(By.id(selectorValue));
				break;

			case "model":
				_element = ngDriver.findElement(NgBy.model(selectorValue));
				break;

			case "linkText":
				_element = driver.findElement(By.linkText(selectorValue));
				break;

			case "name":
				_element = driver.findElement(By.name(selectorValue));
				break;

			case "options":
				_element = ngDriver.findElement(NgBy.options(selectorValue));
				break;

			case "partialLinkText":
				_element = driver.findElement(By.partialLinkText(selectorValue));
				break;

			// case "exactRepeater":
			case "repeater":
				_element = ngDriver.findElement(NgBy.repeater(selectorValue));
				break;

			case "repeaterColumn":
				_element = ngDriver
						.findElement(NgBy.repeaterColumn(selectorValue, selectorColumn));
				break;

			// added for ngWebDriver compatibility
			case "repeaterCell":

			case "repeaterElement":
				if (debug) {
					System.err.println(
							String.format("repeaterElement(\"%s\",%d,\"%s\")", selectorValue,
									Integer.parseInt(selectorRow.replaceAll(".\\d+$", "")),
									selectorColumn));

				}
				_element = ngDriver.findElement(NgBy.repeaterElement(selectorValue,
						Integer.parseInt(selectorRow.replaceAll(".\\d+$", "")),
						selectorColumn));
				break;

			case "repeaterRow":

			case "repeaterRows":
				_element = ngDriver.findElement(NgBy.repeaterRows(selectorValue,
						Integer.parseInt(selectorRow.replaceAll(".\\d+$", ""))));
				break;

			// unique to jProtracror and old Protractor JS
			case "selectedOption":
				_element = ngDriver.findElement(NgBy.selectedOption(selectorValue));
				break;

			case "selectedRepeaterOption":
				_element = ngDriver
						.findElement(NgBy.selectedRepeaterOption(selectorValue));
				break;

			case "text":
				// Option 1: construct XPath selector
				Map<String, String> amendedParams = new HashMap<>();
				String amendedSelectorValue = String.format(
						"//%s[contains(normalize-space(text()),'%s')]",
						(selectorTagName != null) ? selectorTagName : "*", selectorValue);
				System.err.println("Build xpath: " + amendedSelectorValue);
				amendedParams.put("param1", "xpath");
				amendedParams.put("param2", amendedSelectorValue);
				_element = _findElement(amendedParams);
				// Option 2: use Java streams for filtering
				if (selectorTagName != null && _element == null) {
					_element = driver.findElements(By.tagName(selectorTagName)).stream()
							.filter(o -> {
								return (Boolean) (o.getText().contains(selectorValue));
							}).findFirst().get();
				}
				break;

			case "xpath":
				_element = driver.findElement(By.xpath(selectorValue));
				break;
			}
		} catch (Exception e) {/* TODO: logging*/
			System.err.println("Exception: " + e.toString());
		}
		return _element;
	}

	public static List<WebElement> _findElements(Map<String, String> params) {
		selectorType = params.get("param1");
		if (!locatorTable.containsKey(selectorType)) {
			throw new RuntimeException("Unknown Selector Type: " + selectorType);
		}
		// TODO: introduce repository of selector aliases:
		// objectRepo.getProperty(selectorValue) || selectorValue
		selectorValue = params.get("param2");
		// TODO: change keys
		if (params.containsKey("param5")) {
			selectorContainedText = params.get("param5");
		}
		if (params.containsKey("param6")) {
			selectorTagName = params.get("param6");
		}
		List<WebElement> _elements = new ArrayList<>();
		try {
			switch (selectorType) {

			case "binding":
				_elements = ngDriver.findElements(NgBy.binding(selectorValue));
				break;

			case "buttontext":
				_elements = ngDriver.findElements(NgBy.buttonText(selectorValue));
				break;

			case "className":
				_elements = driver.findElements(By.className(selectorValue));
				break;

			case "css":
				_elements = driver.findElements(By.cssSelector(selectorValue));
				break;

			case "cssContainingText":
				_elements = ngDriver.findElements(
						NgBy.cssContainingText(selectorValue, selectorContainedText));
				break;

			case "cssSelector":
				_elements = driver.findElements(By.cssSelector(selectorValue));
				break;

			case "id":
				_elements = driver.findElements(By.id(selectorValue));
				break;

			case "model":
				_elements = ngDriver.findElements(NgBy.model(selectorValue));
				break;

			case "linkText":
				_elements = driver.findElements(By.linkText(selectorValue));
				break;

			case "name":
				_elements = driver.findElements(By.name(selectorValue));
				break;

			case "options":
				_elements = ngDriver.findElements(NgBy.options(selectorValue));
				break;

			case "partialLinkText":
				_elements = driver.findElements(By.partialLinkText(selectorValue));
				break;

			case "repeater":
				_elements = ngDriver.findElements(NgBy.repeater(selectorValue));
				break;
			/*
			repeaterColumn
			repeatereElement
			selectedOption
			selectedRepeaterOption
			are unlikely to be useful here
			*/

			case "text":
				// Option 1: construct xpath selector
				Map<String, String> amendedParams = new HashMap<>();
				String amendedSelectorValue = String.format(
						"//%s[contains(normalize-space(text()), '%s')]",
						(selectorTagName != null) ? selectorTagName : "*", selectorValue);
				System.err.println("Build xpath: " + amendedSelectorValue);
				amendedParams.put("param1", "xpath");
				amendedParams.put("param2", amendedSelectorValue);
				_elements = _findElements(amendedParams);
				// Option 2: use Java streams for filtering
				if (selectorTagName != null
						&& (_elements == null || _elements.size() == 0)) {

					_elements = driver.findElements(By.tagName(selectorTagName)).stream()
							.filter(o -> {
								return (Boolean) (o.getText().contains(selectorValue));
							}).collect(Collectors.toList());
				}
				break;

			case "xpath":
				_elements = driver.findElements(By.xpath(selectorValue));
				break;
			}

		} catch (Exception e) {
			// TODO: logging
		}
		return _elements;
	}

	// wait for the page url to change to contain expectedURL
	public static void waitURLChange(Map<String, String> params) {
		WebDriverWait _wait;
		try {
			timeout = (long) (Float.parseFloat(params.get("param7")));
			_wait = new WebDriverWait(driver, timeout);
		} catch (java.lang.NumberFormatException e) {
			_wait = wait;
		}
		String expectedURL = params.get("param1");
		ExpectedCondition<Boolean> urlChange = driver -> driver.getCurrentUrl()
				.matches(String.format("^%s.*", expectedURL));
		_wait.until(urlChange);
	}

	// wait for the element to become clickable
	public static void waitClickable(Map<String, String> params) {

		selectorType = params.get("param1");
		if (!locatorTable.containsKey(selectorType)) {
			throw new RuntimeException("Unknown Selector Type: " + selectorType);
		}
		selectorValue = params.get("param2");
		WebDriverWait _wait;
		if (params.containsKey("param7")) {
			timeout = (long) (Float.parseFloat(params.get("param7")));
			_wait = new WebDriverWait(driver, timeout);
		} else {
			_wait = wait;
		}
		pattern = Pattern.compile(
				"(?:css|cssSelector|id|linkText|name|partialLinkText|tagName|xpath)",
				Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(selectorType);
		if (matcher.find()) {
			switch (selectorType) {
			case "css":
				locator = By.cssSelector(selectorValue);
				break;
			case "cssSelector":
				locator = By.cssSelector(selectorValue);
				break;
			case "id":
				locator = By.id(selectorValue);
				break;
			case "linkText":
				locator = By.linkText(selectorValue);
				break;
			case "name":
				locator = By.name(selectorValue);
				break;
			case "partialLinkText":
				locator = By.partialLinkText(selectorValue);
				break;
			case "tagName":
				locator = By.tagName(selectorValue);
				break;
			case "xpath":
				locator = By.xpath(selectorValue);
				break;
			}
			_wait.until(
					ExpectedConditions.elementToBeClickable(driver.findElement(locator)));
		}
		pattern = Pattern.compile(
				"(?:binding|buttonText|partialButtonText|model|options)",
				Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(selectorType);
		if (matcher.find()) {
			switch (selectorType) {
			case "binding":
				locator = NgBy.binding(selectorValue);
				break;
			case "buttontext":
				locator = NgBy.buttonText(selectorValue);
				break;
			case "partialButtontext":
				locator = NgBy.partialButtonText(selectorValue);
				break;
			case "options":
				locator = NgBy.options(selectorValue);
				break;
			case "model":
				locator = NgBy.model(selectorValue);
				break;
			}
			_wait.until(
					ExpectedConditions.elementToBeClickable(driver.findElement(locator)));
		}
		if (selectorType == "text") {
			try {
				_wait.until(new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver d) {
						String t = d.findElement(By.className("intro-message")).getText();
						Boolean result = t.contains("Link Successfully clicked");
						System.err.println(
								"in apply: Text = " + t + "\nresult = " + result.toString());
						return result;
					}
				});
			} catch (Exception e) {
				System.err.println("Exception: " + e.toString());
				throw new RuntimeException(e);
			}

		}
	}

	public static void wait(Map<String, String> params) {
		timeout = (long) (Float.parseFloat(params.get("param7")));
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
		}
	}

	public static void highlight(WebElement element) {
		highlight(element, 100);
	}

	public static void highlight(WebElement element, long highlight_interval) {
		try {
			wait.until(ExpectedConditions.visibilityOf(element));
			if (driver instanceof JavascriptExecutor) {
				((JavascriptExecutor) driver).executeScript(
						"arguments[0].style.border='3px solid yellow'", element);
			}
			Thread.sleep(highlight_interval);
			if (driver instanceof JavascriptExecutor) {
				((JavascriptExecutor) driver)
						.executeScript("arguments[0].style.border=''", element);
			}
		} catch (InterruptedException e) {
			System.err.println("Ignored: " + e.toString());
		}
	}

	public static String getChromeDriverPath() {
		return chromeDriverPath;
	}

	public static void setChromeDriverPath(String chromeDriverPath) {
		KeywordLibrary.chromeDriverPath = chromeDriverPath;
	}

	public static String getGeckoDriverPath() {
		return geckoDriverPath;
	}

	public static void setGeckoDriverPath(String geckoDriverPath) {
		KeywordLibrary.geckoDriverPath = geckoDriverPath;
	}

	public static String getFirefoxBrowserPath() {
		return firefoxBrowserPath;
	}

	public static void setFirefoxBrowserPath(String firefoxBrowserPath) {
		KeywordLibrary.firefoxBrowserPath = firefoxBrowserPath;
	}

	public static String getIeDriverPath() {
		return ieDriverPath;
	}

	public static void setIeDriverPath(String ieDriverPath) {
		KeywordLibrary.ieDriverPath = ieDriverPath;
	}

	public static String getEdgeDriverPath() {
		return edgeDriverPath;
	}

	public static void setEdgeDriverPath(String edgeDriverPath) {
		KeywordLibrary.edgeDriverPath = edgeDriverPath;
	}

	public static String getOsName() {
		if (KeywordLibrary.osName == null) {
			KeywordLibrary.osName = System.getProperty("os.name");
		}
		return KeywordLibrary.osName.toLowerCase();
	}

	public static String getBrowser() {
		return browser;
	}

	public static void setBrowser(String browser) {
		KeywordLibrary.browser = browser;
		if (debug){
			System.err.println("KeywordLibrary use browser: " + browser);			
		}
	}

}
