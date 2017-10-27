package org.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

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

public class KeywordLibrary {

	private static boolean instance_flag = false;
	private Object _object = null;
	private Class<?> _class = null;
	public WebDriver driver;
	public WebDriverWait wait;
	public Actions actions;
	private NgWebDriver ngDriver;
	private WebElement element;
	private Pattern pattern;
	private Matcher matcher;
	private By locator;
	private long timeout;

	Properties objectRepo;
	String status;
	String result;
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
	private String attributeName = null;
	private String param1;
	private String param2;
	private String param3;
	public int scriptTimeout = 5;
	public int stepWait = 150;
	public int flexibleWait = 120;
	public int implicitWait = 1;
	public long pollingInterval = 500;
	private Map<String, String> methodTable = new HashMap<>();
	{
		methodTable.put("CLICK", "clickButton");
		methodTable.put("CLICK_BUTTON", "clickButton");
		methodTable.put("CLICK_CHECKBOX", "clickCheckBox");
		methodTable.put("CLICK_LINK", "clickLink");
		methodTable.put("CLICK_RADIO", "clickRadioButton");
		methodTable.put("CLOSE_BROWSER", "closeBrowser");
		methodTable.put("CREATE_BROWSER", "openBrowser");
		methodTable.put("ELEMENT_PRESENT", "elementPresent");
		methodTable.put("GET_ATTR", "getElementAttribute");
		methodTable.put("GET_TEXT", "getElementText");
		methodTable.put("GOTO_URL", "navigateTo");
		methodTable.put("SELECT_OPTION", "selectDropDown");
		methodTable.put("SET_TEXT", "enterText");
		methodTable.put("SEND_KEYS", "enterText");
		methodTable.put("SWITCH_FRAME", "switchFrame");
		methodTable.put("VERIFY_ATTR", "verifyAttribute");
		methodTable.put("VERIFY_TEXT", "verifyText");
		methodTable.put("CLEAR_TEXT", "clearText");
		methodTable.put("WAIT", "wait");
		methodTable.put("waitURLChange", "waitURLChange");
		methodTable.put("WAIT_ELEMENT_CLICAKBLE", "waitClickable");
	}
	private Map<String, Method> locatorTable = new HashMap<>();

	public void closeBrowser(Map<String, String> params) {
		driver.quit();
	}

	public void navigateTo(Map<String, String> params) {
		String url = params.get("param1");
		System.err.println("Navigate to: " + url);
		driver.navigate().to(url);
	}

	private KeywordLibrary() {
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
			_class = Class.forName("org.utils.KeywordLibrary");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		for (String keyword : methodTable.keySet()
				.toArray(new String[methodTable.keySet().size()])) {
			if (methodTable.get(keyword).isEmpty()) {
				System.err.println("Removing keyword:" + keyword);
				methodTable.remove(keyword);
			} else {
				try {
					_class.getMethod(methodTable.get(keyword), Map.class);
				} catch (NoSuchMethodException e) {
					System.err.println(
							"Removing  keyword:" + keyword + " exception: " + e.toString());
					methodTable.remove(keyword);
				} catch (SecurityException e) {
					/* ignore*/
				}
			}
		}
		for (String methodName : methodTable.values()
				.toArray(new String[methodTable.values().size()])) {
			System.err.println("Adding keyword for method: " + methodName);
			if (!methodTable.containsKey(methodName)) {
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
			System.out.println("Exception (ignored): " + e.toString());
		}
		Method methodMissing = null;
		try {
			// do we ever want to send correct arguments ?
			@SuppressWarnings("rawtypes")
			Class[] arguments = new Class[] { String.class, String.class };
			Constructor methodConstructor = Method.class
					.getDeclaredConstructor(arguments);
			methodConstructor.setAccessible(true);
			methodMissing = (Method) methodConstructor.newInstance();
		} catch (NoSuchMethodException | IllegalAccessException
				| InstantiationException | IllegalArgumentException
				| InvocationTargetException e) {
			System.out.println("Exception (ignored): " + e.toString());

		}
		try {
			locatorTable.put("binding",
					ByAngular.class.getMethod("binding", String.class));
			locatorTable.put("buttontext",
					ByAngular.class.getMethod("buttonText", String.class));
			locatorTable.put("cssContainingText", ByAngular.class
					.getMethod("cssContainingText", String.class, String.class));
			locatorTable.put("exactBinding",
					ByAngular.class.getMethod("exactBinding", String.class));
			locatorTable.put("exactRepeater",
					ByAngular.class.getMethod("exactRepeater", String.class));
			locatorTable.put("model",
					ByAngular.class.getMethod("model", String.class));
			locatorTable.put("options",
					ByAngular.class.getMethod("options", String.class));
			locatorTable.put("partialButtonText",
					ByAngular.class.getMethod("partialButtonText", String.class));
			locatorTable.put("repeater",
					ByAngular.class.getMethod("repeater", String.class));
			locatorTable.put("repeaterColumn", methodMissing);
			locatorTable.put("repeaterCell", methodMissing);
			locatorTable.put("repeaterRow", methodMissing);

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

	public void loadProperties() throws IOException {
		File file = new File("ObjectRepo.Properties");
		objectRepo = new Properties();
		objectRepo.load(new FileInputStream(file));
	}

	public void openBrowser(Map<String, String> params) throws IOException {
		try {
			File file = new File("Config.properties");
			Properties config = new Properties();
			config.load(new FileInputStream(file));
			if (config.getProperty("browser").equalsIgnoreCase("Chrome")) {
				System.setProperty("webdriver.chrome.driver",
						"C:\\java\\selenium\\chromedriver.exe");
				driver = new ChromeDriver();
				ngDriver = new NgWebDriver((JavascriptExecutor) driver);
				driver.get(config.getProperty("url"));
			}
			driver.manage().window().maximize();
			status = "Passed";
		} catch (Exception e) {
			status = "Failed";
		}
	}

	public void enterText(Map<String, String> params) {
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

	public void clickButton(Map<String, String> params) {
		element = _findElement(params);
		if (element != null) {
			highlight(element);
			element.click();
			status = "Passed";
		} else {
			status = "Failed";
		}
	}

	public void clickLink(Map<String, String> params) {
		element = _findElement(params);
		if (element != null) {
			highlight(element);
			element.click();
			status = "Passed";
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
			status = "Failed";
	}

	public void verifyText(Map<String, String> params) {
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
		if (!params.containsKey("param5") ) {
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
			highlight(element);
			element.click();
			status = "Passed";
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

	public WebElement _findElement(Map<String, String> params) {
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
				ngDriver.waitForAngularRequestsToFinish();
				_element = driver.findElement(ByAngular.repeater(selectorValue)
						.row(Integer.parseInt(selectorRow)));
				break;

			case "repeatereCell":
				ngDriver.waitForAngularRequestsToFinish();
				_element = driver.findElement(ByAngular.repeater(selectorValue)
						.row(Integer.parseInt(selectorRow)).column(selectorColumn));
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
		}
		return _element;
	}

	public List<WebElement> _findElements(Map<String, String> params) {
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

	// wait for the page url to change to contain expectedURL
	public void waitURLChange(Map<String, String> params) {
		WebDriverWait _wait;
		try {
			timeout = (long) (Float.parseFloat(params.get("param7")));
			_wait = new WebDriverWait(driver, timeout);
		} catch (java.lang.NumberFormatException e) {
			_wait = wait;
		}		
		String expectedURL = params.get("param8");
		ExpectedCondition<Boolean> urlChange = driver -> driver.getCurrentUrl()
				.matches(String.format("^%s.*", expectedURL));
		_wait.until(urlChange);
	}

	// wait for the element to become clickable
	public void waitClickable(Map<String, String> params) {

		selectorType = params.get("param1");
		if (!locatorTable.containsKey(selectorType)) {
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
			_wait.until(
					ExpectedConditions.elementToBeClickable(driver.findElement(locator)));
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
}
