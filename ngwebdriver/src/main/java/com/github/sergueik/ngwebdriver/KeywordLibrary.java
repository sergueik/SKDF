package com.github.sergueik.ngwebdriver;
/**
 * Copyright 2017,2018 Serguei Kouzmine
 */

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
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
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

// NOTE: eclipse appears to be removing these imports
import com.paulhammant.ngwebdriver.NgWebDriver;
import com.paulhammant.ngwebdriver.ByAngular;
import com.paulhammant.ngwebdriver.ByAngularBinding;
import com.paulhammant.ngwebdriver.ByAngularButtonText;
import com.paulhammant.ngwebdriver.ByAngularCssContainingText;
import com.paulhammant.ngwebdriver.ByAngularExactBinding;
import com.paulhammant.ngwebdriver.ByAngularModel;
import com.paulhammant.ngwebdriver.ByAngularOptions;
import com.paulhammant.ngwebdriver.ByAngularPartialButtonText;
import com.paulhammant.ngwebdriver.ByAngularRepeater;
import com.paulhammant.ngwebdriver.ByAngularRepeaterCell;
import com.paulhammant.ngwebdriver.ByAngularRepeaterColumn;
import com.paulhammant.ngwebdriver.ByAngularRepeaterRow;

import org.apache.poi.sl.usermodel.Sheet;
import org.hamcrest.CoreMatchers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Keyword Driven Library for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class KeywordLibrary {

	private static boolean instance_flag = false;
	private Object _object = null;
	private Class<?> _class = null;
	public WebDriver driver;
	public WebDriverWait wait;
	public Actions actions;
	private String browser = "chrome";
	private static String baseURL = "about:blank";
	private String osName = getOsName();
	private String chromeDriverPath = null;
	private String geckoDriverPath = null;
	private String firefoxBrowserPath = null;
	private String ieDriverPath = null;
	private String edgeDriverPath = null;
	private NgWebDriver ngDriver;
	private WebElement element;
	private Pattern pattern;
	private Matcher matcher;
	private By locator;
	private long timeout;
	private static boolean debug = true;

	private String status;
	private String result;
	private String selectorTagName = null;
	private String selectorType = null;
	private String selectorValue = null;
	private String selectorRow = null;
	private String selectorColumn = null;
	private String selectorContainedText = null;
	private String expectedValue = null;
	private String textData = null;
	private String visibleText = null;
	private String expectedText = null;
	private String expectedTag = null;
	private String attributeName = null;
	private String param1;
	private String param2;
	private String param3;
	public int scriptTimeout = 30;
	public int stepWait = 150;
	public int flexibleWait = 120;
	public int implicitWait = 10;
	public long pollingInterval = 500;

	private Map<String, String> methodTable = new HashMap<>();
	{
		methodTable.put("CLEAR_TEXT", "clearText");
		methodTable.put("CLICK", "clickButton");
		methodTable.put("CLICK_BUTTON", "clickButton");
		methodTable.put("CLICK_CHECKBOX", "clickCheckBox");
		methodTable.put("CLICK_LINK", "clickLink");
		methodTable.put("CLICK_RADIO", "clickRadioButton");
		methodTable.put("CLOSE_BROWSER", "closeBrowser");
		methodTable.put("CONFIRM_ALERT", "confirmAlert");
		methodTable.put("CREATE_BROWSER", "openBrowser");
		methodTable.put("DISMISS_ALERT", "dismissAlert");
		methodTable.put("ELEMENT_PRESENT", "elementPresent");
		methodTable.put("FILL_ALERT_PROMPT", "fillAlertPrompt");
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
		methodTable.put("WAIT_ELEMENT", "waitVisible");
		methodTable.put("WAIT_ELEMENT_CLICAKBLE", "waitClickable");
		methodTable.put("WAIT_URL_CHANGE", "waitURLChange");
	}

	public Set<String> getKeywords() {
		return this.methodTable.keySet();
	}

	private Map<String, Method> selectorTypes = new HashMap<>();

	public Set<String> getLocators() {
		return this.selectorTypes.keySet();
	}

	public void closeBrowser(Map<String, String> params) {
		driver.quit();
	}

	public void navigateTo(Map<String, String> params) {
		String url = params.get("param1");
		if (debug) {
			System.err.println("Navigate to: " + url);
		}
		driver.navigate().to(url);
	}

	private KeywordLibrary() {
		_object = this;
		initMethods();
	}

	public String getStatus() {
		return status;
	}

	public String getResult() {
		return result;
	}

	public static KeywordLibrary Instance() {
		if (!instance_flag) {
			instance_flag = true;
			return new KeywordLibrary();
		} else
			return null;
	}

	public void finalize() {
		instance_flag = false;
	}

	public void initMethods() {
		try {
			_class = Class.forName("com.github.sergueik.ngwebdriver.KeywordLibrary");
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
					System.err.println("Adding keyword for method: " + methodName);
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
			Class<?> _locatorHelper = Class
					.forName("com.paulhammant.ngwebdriver.ByAngular");
			Method[] _locatorMethods = _locatorHelper.getMethods();
			for (Method _locatorMethod : _locatorMethods) {
				System.err
						.println("Adding locator of com.paulhammant.ngwebdriver.ByAngular:"
								+ _locatorMethod.toString());
			}
		} catch (ClassNotFoundException | SecurityException e) {
			System.out.println("Exception (ignored): " + e.toString());
		}
		*/
		try {
			selectorTypes.put("className",
					By.class.getMethod("className", String.class));
			selectorTypes.put("css", By.class.getMethod("cssSelector", String.class));
			selectorTypes.put("id", By.class.getMethod("id", String.class));
			selectorTypes.put("linkText",
					By.class.getMethod("linkText", String.class));
			selectorTypes.put("name", By.class.getMethod("name", String.class));
			selectorTypes.put("tagName", By.class.getMethod("tagName", String.class));
			selectorTypes.put("xpath", By.class.getMethod("xpath", String.class));
		} catch (NoSuchMethodException e) {
			System.out.println("Exception (ignored): " + e.toString());
		}
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
		// put synthetic selectors explicitly
		selectorTypes.put("text", methodMissing);
		try {
			// put synthetic selectors explicitly
			selectorTypes.put("binding",
					ByAngular.class.getMethod("binding", String.class));
			selectorTypes.put("buttontext",
					ByAngular.class.getMethod("buttonText", String.class));
			selectorTypes.put("cssContainingText", ByAngular.class
					.getMethod("cssContainingText", String.class, String.class));
			selectorTypes.put("exactBinding",
					ByAngular.class.getMethod("exactBinding", String.class));
			selectorTypes.put("exactRepeater",
					ByAngular.class.getMethod("exactRepeater", String.class));
			selectorTypes.put("model",
					ByAngular.class.getMethod("model", String.class));
			selectorTypes.put("options",
					ByAngular.class.getMethod("options", String.class));
			selectorTypes.put("partialButtonText",
					ByAngular.class.getMethod("partialButtonText", String.class));
			selectorTypes.put("repeater",
					ByAngular.class.getMethod("repeater", String.class));
			selectorTypes.put("repeaterCell", methodMissing);
			selectorTypes.put("repeaterColumn", methodMissing);
			selectorTypes.put("repeaterElement", methodMissing);
			selectorTypes.put("repeaterRow", methodMissing);
			// NOTE: plural in the method name
			selectorTypes.put("repeaterRows", methodMissing);

		} catch (NoSuchMethodException e) {
			System.out.println("Exception (ignored): " + e.toString());
		}
	}

	public void callMethod(String keyword, Map<String, String> params) {
		if (_object == null) {
			try {
				_object = _class.newInstance();
			} catch (IllegalAccessException | InstantiationException e) {
				throw new RuntimeException(e);
			}
		}
		if (methodTable.containsKey(keyword)) {
			String methodName = methodTable.get(keyword);
			try {
				System.err.println(keyword + " call method: " + methodName + " with "
						+ String.join(",", params.values()));
				_class.getMethod(methodName, Map.class).invoke(_object, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			throw new RuntimeException("No method found for keyword: " + keyword);
		}
	}

	public void openBrowser(Map<String, String> params) throws IOException {
		try {
			switch (this.browser) {
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
				System.err.println("Open: " + this.getBrowser());
			}
			wait = new WebDriverWait(driver, flexibleWait);
			wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);
			driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
			ngDriver = new NgWebDriver((JavascriptExecutor) driver);
			status = "Passed";
			// TODO: pass from launcher
			// driver.get(config.getProperty("url"));
			try {
				// TODO: pass from launcher
				driver.get(baseURL);
				driver.manage().window().maximize();
			} catch (WebDriverException e1) {
				// disconnected: unable to connect to renderer
				System.err
						.println("Exception in openBrowser (ignored): " + e1.toString());
			}
		} catch (Exception e) {
			status = "Failed";
		}
	}

	public void enterText(Map<String, String> params) {
		element = _findElement(params);
		textData = params.get("param5");
		if (element != null) {
			try {
				highlight(element);
				System.err.println("Entering text: " + textData);
				element.sendKeys(textData);
				sleep(100);
				element = _findElement(params);
				System.err.println("Entered text: " + element.getAttribute("value"));
				status = "Passed";
			} catch (NoSuchElementException e) {
				status = "Failed";
			}
		} else {
			status = "Failed";
		}
	}

	public void sleep(Integer seconds) {
		long secondsLong = (long) seconds;
		try {
			Thread.sleep(secondsLong);
		} catch (InterruptedException e) {
			e.printStackTrace();
			status = "Failed";
		}
		status = "Passed";
	}

	public void clickButton(Map<String, String> params) {
		element = _findElement(params);
		if (element != null) {
			try {
				highlight(element);
				element.click();
				status = "Passed";
			} catch (NoSuchElementException e) {
				status = "Failed";
			}
		} else {
			status = "Failed";
		}
	}

	public void clickLink(Map<String, String> params) {
		element = _findElement(params);
		if (element != null) {
			try {
				highlight(element);
				System.err.println("click");
				element.click();
				status = "Passed";
			} catch (NoSuchElementException e) {
				status = "Failed";
			}
		} else {
			status = "Failed";
		}
		try {
			Thread.sleep(stepWait);
		} catch (InterruptedException e) {
			// System.err.println("Ignored: " + e.toString());
		}

	}

	public void selectDropDown(Map<String, String> params) {
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
			System.err.println("element not found");
			status = "Failed";
		}
	}

	public void verifyAttribute(Map<String, String> params) {
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
			System.err.println("element not found");
		status = "Failed";
	}

	public void verifyTag(Map<String, String> params) {
		boolean flag = false;
		expectedTag = params.get("param5");
		element = _findElement(params);
		if (element != null) {
			highlight(element);
			flag = element.getTagName().equals(expectedTag);
		}
		if (flag)
			status = "Passed";
		else
			status = "Failed";
	}

	public void verifyText(Map<String, String> params) {
		boolean flag = false;
		expectedText = params.get("param5");
		element = _findElement(params);
		if (element != null) {
			highlight(element);
			if (debug) {
				System.err.println("Verifying " + element.getText());
			}
			flag = element.getText().equals(expectedText);
		}
		if (flag)
			status = "Passed";
		else
			status = "Failed";
	}

	public void getElementText(Map<String, String> params) {
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

	public void getElementAttribute(Map<String, String> params) {
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
			status = "Failed";
		}
	}

	public void elementPresent(Map<String, String> params) {
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

	public void clickCheckBox(Map<String, String> params) {
		if (!params.containsKey("param5")) {
			element = _findElement(params);
			if (element != null) {
				try {
					highlight(element);
					element.click();
					status = "Passed";
				} catch (NoSuchElementException e) {
					status = "Failed";
				}
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
			try {
				highlight(element);
				element.click();
				status = "Passed";
			} catch (NoSuchElementException e) {
				status = "Failed";
			}
		} else {
			status = "Failed";
		}
	}

	public void clickRadioButton(Map<String, String> params) {
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
			try {
				highlight(element);
				element.click();
				status = "Passed";
			} catch (NoSuchElementException e) {
				status = "Failed";
			}
		} else {
			status = "Failed";
		}
	}

	// TODO:
	public void switchFrame(Map<String, String> params) {
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

	private WebElement _findElement(Map<String, String> params) {
		selectorType = params.get("param1");
		if (!selectorTypes.containsKey(selectorType)) {
			throw new RuntimeException("Unknown Selector Type: " + selectorType);
		}
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

		WebElement _element = null;
		try {
			switch (selectorType) {

			case "binding":
				ngDriver.waitForAngularRequestsToFinish();
				_element = driver.findElement(ByAngular.binding(selectorValue));
				break;

			case "buttontext":
				ngDriver.waitForAngularRequestsToFinish();
				_element = driver.findElement(ByAngular.buttonText(selectorValue));
				break;

			case "className":
				_element = driver.findElement(By.className(selectorValue));
				break;

			case "css":
				_element = driver.findElement(By.cssSelector(selectorValue));
				break;

			case "cssContainingText":
				ngDriver.waitForAngularRequestsToFinish();
				_element = driver.findElement(
						ByAngular.cssContainingText(selectorValue, selectorContainedText));
				break;

			case "cssSelector":
				_element = driver.findElement(By.cssSelector(selectorValue));
				break;

			case "exactBinding":
				ngDriver.waitForAngularRequestsToFinish();
				_element = driver.findElement(ByAngular.exactBinding(selectorValue));
				break;

			case "exactRepeater":
				ngDriver.waitForAngularRequestsToFinish();
				_element = driver.findElement(ByAngular.exactRepeater(selectorValue));
				break;

			case "id":
				_element = driver.findElement(By.id(selectorValue));
				break;

			case "model":
				ngDriver.waitForAngularRequestsToFinish();
				_element = driver.findElement(ByAngular.model(selectorValue));
				break;

			case "linkText":
				_element = driver.findElement(By.linkText(selectorValue));
				break;

			case "name":
				_element = driver.findElement(By.name(selectorValue));
				break;

			case "options":
				ngDriver.waitForAngularRequestsToFinish();
				_element = driver.findElement(ByAngular.options(selectorValue));
				break;

			case "partialLinkText":
				_element = driver.findElement(By.partialLinkText(selectorValue));
				break;

			case "repeater":
				ngDriver.waitForAngularRequestsToFinish();
				_element = driver.findElement(ByAngular.repeater(selectorValue));
				break;

			case "repeaterColumn":
				ngDriver.waitForAngularRequestsToFinish();
				ByAngularRepeater _elementRepeater = ByAngular.repeater(selectorValue);
				ByAngularRepeaterColumn _elementRepeaterColumn = _elementRepeater
						.column(selectorColumn);
				_element = driver.findElement(_elementRepeaterColumn);
				break;

			case "repeaterRow":

			case "repeaterRows":
				ngDriver.waitForAngularRequestsToFinish();
				_element = driver.findElement(ByAngular.repeater(selectorValue)
						.row(Integer.parseInt(selectorRow.replaceAll(".\\d+$", ""))));
				break;
			//
			case "repeaterElement":

			case "repeaterCell":
				if (debug) {
					System.err.println(
							String.format("repeaterElement(\"%s\",%d,\"%s\")", selectorValue,
									Integer.parseInt(selectorRow.replaceAll(".\\d+$", "")),
									selectorColumn));

				}
				ngDriver.waitForAngularRequestsToFinish();
				_element = driver.findElement(ByAngular.repeater(selectorValue)
						.row(Integer.parseInt(selectorRow.replaceAll(".\\d+$", "")))
						.column(selectorColumn));
				if (debug) {
					assertThat(element, notNullValue());
					System.err.println(element.getAttribute("outerHTML"));
				}
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
		} catch (Exception e) {
			System.err.println("Exception in _findElement: " + e.toString());
		}
		return _element;
	}

	public List<WebElement> _findElements(Map<String, String> params) {
		selectorType = params.get("param1");
		if (!selectorTypes.containsKey(selectorType)) {
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
				ngDriver.waitForAngularRequestsToFinish();
				_elements = driver.findElements(ByAngular.binding(selectorValue));
				break;

			case "buttontext":
				ngDriver.waitForAngularRequestsToFinish();
				_elements = driver.findElements(ByAngular.buttonText(selectorValue));
				break;

			case "className":
				_elements = driver.findElements(By.className(selectorValue));
				break;

			case "css":
				_elements = driver.findElements(By.cssSelector(selectorValue));
				break;

			case "cssContainingText":
				ngDriver.waitForAngularRequestsToFinish();
				_elements = driver.findElements(
						ByAngular.cssContainingText(selectorValue, selectorContainedText));
				break;

			case "cssSelector":
				_elements = driver.findElements(By.cssSelector(selectorValue));
				break;

			case "exactBinding":
				ngDriver.waitForAngularRequestsToFinish();
				_elements = driver.findElements(ByAngular.exactBinding(selectorValue));
				break;

			case "exactRepeater":
				ngDriver.waitForAngularRequestsToFinish();
				_elements = driver.findElements(ByAngular.exactRepeater(selectorValue));
				break;

			case "id":
				_elements = driver.findElements(By.id(selectorValue));
				break;

			case "model":
				ngDriver.waitForAngularRequestsToFinish();
				_elements = driver.findElements(ByAngular.model(selectorValue));
				break;

			case "linkText":
				_elements = driver.findElements(By.linkText(selectorValue));
				break;

			case "name":
				_elements = driver.findElements(By.name(selectorValue));
				break;

			case "options":
				ngDriver.waitForAngularRequestsToFinish();
				_elements = driver.findElements(ByAngular.options(selectorValue));
				break;

			case "partialLinkText":
				_elements = driver.findElements(By.partialLinkText(selectorValue));
				break;

			case "repeater":
				ngDriver.waitForAngularRequestsToFinish();
				_elements = driver.findElements(ByAngular.repeater(selectorValue));
				break;

			/*
			repeaterColumn
			repeatereCell
			RepeaterRow
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

	public void fillAlertPrompt(Map<String, String> params) {
		textData = params.get("param5");
		try {
			// fill the data in the alert
			Alert alert = driver.switchTo().alert();
			System.err.println("In alert " + alert.toString());
			alert.sendKeys(textData);
			alert.accept();
			status = "Passed";
		} catch (NoAlertPresentException ex) {
			// Alert not present - ignore
			status = "Failed";
		} catch (WebDriverException ex) {
			System.err
					.println("Alert was not handled : " + ex.getStackTrace().toString());
			status = "Failed";
			return;
		}
	}

	public void confirmAlert(Map<String, String> params) {
		try {
			// confirm alert
			driver.switchTo().alert().accept();
			status = "Passed";
		} catch (NoAlertPresentException ex) {
			// Alert not present - ignore
			status = "Passed";
		} catch (WebDriverException ex) {
			status = "Failed";
			System.err
					.println("Alert was not handled : " + ex.getStackTrace().toString());
			return;
		}
	}

	public void dismissAlert(Map<String, String> params) {
		try {
			// dismiss alert
			driver.switchTo().alert().dismiss();
			status = "Passed";
		} catch (NoAlertPresentException ex) {
			// Alert not present - ignore
			status = "Passed";
		} catch (WebDriverException ex) {
			status = "Failed";
			System.err
					.println("Alert was not handled : " + ex.getStackTrace().toString());
			return;
		}

	}

	// wait for the page url to change to contain expected url
	public void waitURLChange(Map<String, String> params) {
		WebDriverWait _wait;
		try {
			timeout = (long) (Float.parseFloat(params.get("param7")));
			_wait = new WebDriverWait(driver, timeout);
		} catch (java.lang.NumberFormatException e) {
			_wait = wait;
			status = "Failed";
		}
		final String expectedURL = params.get("param1"); // was: param8
		// NOTE: cannot change: the code below would lead
		// to a compiler error:
		// local variables referenced from a lambda expression
		// must be final or effectively final
		/* 
		expectedURL = params.get("param8");
		if (expectedURL.isEmpty()) {
			expectedURL = params.get("param1");
		}
		*/
		ExpectedCondition<Boolean> urlChange = driver -> {
			String url = driver.getCurrentUrl();
			if (debug) {
				System.err.println("Inspecting the URL: " + url);
				System.err.println("Waiting for the URL: " + expectedURL); // https://accounts.google.com/signin

			}
			return (boolean) url.matches(String.format("^%s.*", expectedURL));
		};
		try {
			_wait.until(urlChange);
			status = "Passed";
		} catch (TimeoutException e) {
			status = "Failed";
		}
	}

	// wait for the element to become visible
	public void waitVisible(Map<String, String> params) {
		selectorType = params.get("param1");
		if (!selectorTypes.containsKey(selectorType)) {
			throw new RuntimeException("Unknown Selector Type: " + selectorType);
		}
		if (params.containsKey("param5")) {
			selectorContainedText = params.get("param5");
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
			try {
				_wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
				status = "Passed";
			} catch (TimeoutException e) {
				status = "Failed";
			}
		}
		pattern = Pattern.compile(
				"(?:binding|buttonText|exactBinding|cssContainingText|model|options|partialButtonText)",
				Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(selectorType);
		if (matcher.find()) {
			switch (selectorType) {
			case "binding":
				locator = ByAngular.binding(selectorValue);
				break;
			case "buttontext":
				locator = ByAngular.buttonText(selectorValue);
				break;
			case "cssContainingText":
				locator = ByAngular.cssContainingText(selectorValue,
						selectorContainedText);
				break;
			case "exactBinding":
				locator = ByAngular.exactBinding(selectorValue);
				break;
			case "model":
				locator = ByAngular.model(selectorValue);
				break;
			case "options":
				locator = ByAngular.options(selectorValue);
				break;
			case "partialButtontext":
				locator = ByAngular.partialButtonText(selectorValue);
				break;
			default:
				throw new RuntimeException("wait code for Selector type " + selectorType
						+ " is not implemented yet");
			}
			try {
				_wait.until(new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver d) {
						WebElement e = d.findElement(locator);
						Boolean result = e.isDisplayed();
						System.err
								.println("In apply: Element = " + e.getAttribute("outerHTML")
										+ "\nresult = " + result.toString());
						return result;
					}
				});
				status = "Passed";
			} catch (Exception e) {
				System.err.println("Exception: " + e.toString());
				status = "Failed";
				// throw new RuntimeException(e);
			}

			/*
			try {
				_wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
				status = "Passed";
			} catch (TimeoutException e) {
				status = "Failed";
			}
			*/

		}
		if (selectorType == "text") {
			try {
				_wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
						String.format("//*[normalize-space(.) = '%s']", selectorValue))));
				status = "Passed";
			} catch (TimeoutException e) {
				status = "Failed";
			}
		}
	}

	// wait for the element to become clickable
	public void waitClickable(Map<String, String> params) {

		selectorType = params.get("param1");
		if (!selectorTypes.containsKey(selectorType)) {
			throw new RuntimeException("Unknown Selector Type: " + selectorType);
		}
		if (params.containsKey("param5")) {
			selectorContainedText = params.get("param5");
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
			try {
				_wait.until(ExpectedConditions
						.elementToBeClickable(driver.findElement(locator)));
				status = "Passed";
			} catch (NoSuchElementException e) {
				status = "Failed";
			}
		}
		pattern = Pattern.compile(
				"(?:binding|buttonText|exactBinding|cssContainingText|model|options|partialButtonText)",
				Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(selectorType);
		if (matcher.find()) {
			switch (selectorType) {
			case "binding":
				locator = ByAngular.binding(selectorValue);
				break;
			case "buttontext":
				locator = ByAngular.buttonText(selectorValue);
				break;
			case "cssContainingText":
				locator = ByAngular.cssContainingText(selectorValue,
						selectorContainedText);
				break;
			case "exactBinding":
				locator = ByAngular.exactBinding(selectorValue);
				break;
			case "model":
				locator = ByAngular.model(selectorValue);
				break;
			case "options":
				locator = ByAngular.options(selectorValue);
				break;
			case "partialButtontext":
				locator = ByAngular.partialButtonText(selectorValue);
				break;
			default:
				throw new RuntimeException("wait code for Selector type " + selectorType
						+ " is not implemented yet");
			}

			try {
				_wait.until(new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver d) {
						WebElement e = d.findElement(locator);
						Boolean result = e.isDisplayed();
						System.err
								.println("In apply: Element = " + e.getAttribute("outerHTML")
										+ "\nresult = " + result.toString());
						return result;
					}
				});
				status = "Passed";
			} catch (Exception e) {
				System.err.println("Exception: " + e.toString());
				status = "Failed";
				// throw new RuntimeException(e);
			}

			/*
						try {
							_wait.until(ExpectedConditions
									.elementToBeClickable(driver.findElement(locator)));
							status = "Passed";
						} catch (NoSuchElementException e) {
							status = "Failed";
						}
						*/
		}
		if (selectorType == "text") {
			try {
				_wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
						String.format("//*[normalize-space(.) = '%s']", selectorValue))));
				status = "Passed";
			} catch (NoSuchElementException e) {
				status = "Failed";
			}
		}
	}

	public void wait(Map<String, String> params) {

		timeout = (long) (Float.parseFloat(params.get("param7")));
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
		}
	}

	public void highlight(WebElement element) {
		highlight(element, 100);
	}

	public void highlight(WebElement element, long highlight_interval) {
		if (wait == null) {
			wait = new WebDriverWait(driver, flexibleWait);
		}
		wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);
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

	public String getChromeDriverPath() {
		return chromeDriverPath;
	}

	public void setChromeDriverPath(String chromeDriverPath) {
		this.chromeDriverPath = chromeDriverPath;
	}

	public String getGeckoDriverPath() {
		return geckoDriverPath;
	}

	public void setGeckoDriverPath(String geckoDriverPath) {
		this.geckoDriverPath = geckoDriverPath;
	}

	public String getFirefoxBrowserPath() {
		return firefoxBrowserPath;
	}

	public void setFirefoxBrowserPath(String firefoxBrowserPath) {
		this.firefoxBrowserPath = firefoxBrowserPath;
	}

	public String getIeDriverPath() {
		return ieDriverPath;
	}

	public void setIeDriverPath(String ieDriverPath) {
		this.ieDriverPath = ieDriverPath;
	}

	public String getEdgeDriverPath() {
		return edgeDriverPath;
	}

	public void setEdgeDriverPath(String edgeDriverPath) {
		this.edgeDriverPath = edgeDriverPath;
	}

	public String getOsName() {
		if (this.osName == null) {
			this.osName = System.getProperty("os.name");
		}
		return osName.toLowerCase();
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		if (debug) {
			System.err.println("KeywordLibrary use browser: " + browser);
		}
		this.browser = browser;
	}

}
