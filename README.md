﻿### About SKDF

![TestCase.xls](https://github.com/sergueik/skdf/blob/master/images/testcase_selenium.png)

This directory contains a skeleton [Keyword-Driven Framework](http://toolsqa.com/selenium-webdriver/keyword-driven-framework/introduction/) project based on
[ashokkhape/automation_framework_selenium](https://github.com/ashokkhape/automation_framework_selenium) and [selenium-webdriver-software-testing/keyword-driven-framework](https://github.com/selenium-webdriver-software-testing/keyword-driven-framework)

The project builds two runnable jars (`com.github.sergueik.jprotractor` and `com.github.sergueik.ngwebdriver` group id namespaces):
```bash
cp TestCase.xls ~/Desktop
pushd jprotractor
mvn  -Pdevelop -DskipTests -Dmaven.test.skip=true install
REM java -jar target/skdf_jprotractor-develop.jar
REM alternatively
java -cp target\skdf_jprotractor-develop.jar;target\lib\* com.github.sergueik.jprotractor.Launcher
popd
pushd ngwebdriver
mvn clean install
REM java -jar target/skdf_ngwebdriver-develop.jar
java -cp target\skdf_ngwebdriver-develop.jar;target\lib\* com.github.sergueik.ngwebdriver.Launcher
popd
```
The launcher uses reflection to associate _keywords_ with *class methods*
```java
private static Map<String, String> methodTable = new HashMap<>();
static {
  methodTable.put("CLICK", "clickButton");
  methodTable.put("CLICK_BUTTON", "clickButton");
...

```
- a single method may have several keywords pointing to it;
```java
String methodName = methodTable.get(keyword);
try {
  Class<?> _class = Class.forName("com.github.sergueik.ngwebdriver.KeywordLibrary");
  Method _method = _class.getMethod(methodName, Map.class);
  System.out.println(keyword + " call method: " + methodName + " with "
			+ String.join(",", params.values()));
  // for static methods
  _method.invoke(null, params);
  // or when using instances methods
  Object _object = _class.newInstance();
  _method.invoke(_object, params);
```
Similar approach is used to define Selector strategies, most of which are static methods of the appropriate core  Selenium class:
```java
    locatorTable.put("className",
        By.class.getMethod("className", String.class));
    locatorTable.put("css", By.class.getMethod("cssSelector", String.class));
    locatorTable.put("id", By.class.getMethod("id", String.class));
    locatorTable.put("linkText",
        By.class.getMethod("linkText", String.class));
    locatorTable.put("name", By.class.getMethod("name", String.class));
    locatorTable.put("tagName", By.class.getMethod("tagName", String.class));
    locatorTable.put("xpath", By.class.getMethod("xpath", String.class));
```

with few "synthetic" strategies, notably  the __TEXT__:

```java
// put synthetic selectors explicitly
  locatorTable.put("text", methodMissing);
```
implemented e.g. through `xpath`:

```java
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
```

The test step arguments are passed as hash of parameters.  That is done so one does not care about the method signature.
Also the [AngularJS](https://angularjs.org/) introduced `NgBy` locators which fequently require (multiple) additional arguments like e.g.
```java
@FindBy(how = How.REPEATER_COLUMN, using = "row in rows", column = "name")
private List<WebElement> friendNames;
```
The step status is returned via `params["status"]` entry, the step result (if any) is returned via `params["result"]`

### Adding Tests to the Spreadsheet
To add a test case, put its name into the `Index` sheet and mark it with `Yes` to be executed
![index](https://github.com/sergueik/skdf/blob/master/images/testcase_index.png)

Next, add the steps. Making the cell border visible will ensure the blank cells are not getting skipped:
![demoqa](https://github.com/sergueik/skdf/blob/master/images/testcase_demoqa.png)

### Adding jProtractor

One can explore additional selectors with jProtractor.

![TestCase.xls](https://github.com/sergueik/skdf/blob/master/images/testcase_protractor.png)

[jProtractor](https://github.com/sergueik/jProtractor) is not available in maven central, therefore to use it with framework one needs do build it from source and
install it into current user's `.m2` repo:

```cmd
git clone https://github.com/sergueik/jProtractor.git
pushd jProtractor
mvn -Dmaven.test.skip=true clean install
```
which will install the jar:
```cmd
[INFO] Installing C:\developer\sergueik\selenium_java\protractor\target\jprotractor-1.2-SNAPSHOT.jar to C:\Users\Serguei\.m2\repository\com\jprotractor\jprotractor\1.2-SNAPSHOT\jprotractor-1.2-SNAPSHOT.jar
[INFO] Installing C:\developer\sergueik\selenium_java\protractor\pom.xml to C:\Users\Serguei\.m2\repository\com\jprotractor\jprotractor\1.2-SNAPSHOT\jprotractor-1.2-SNAPSHOT.pom
```
This will add about 10 AngularJS-specific `NgBy` locators:

![NgBy methods](https://github.com/sergueik/skdf/blob/master/images/ngby_methods.png)

* `options`
* `input`
* `selectedOption`
* `repeater`
* `model`
* `binding`
* `repeaterElement`
* `repeaterRows`
* `buttonText`
* `repeaterColumn`
* `cssContainingText`
* `selectedRepeaterOption`
* `partialButtonText`

### Adding ngWebDriver

Another implementation of Protractor selectors is [ngWebDriver](https://github.com/paul-hammant/ngWebDriver).

![ByAngular methods](https://github.com/sergueik/skdf/blob/master/images/byangular_methods.png)

Most Protractor-specific locators are the same (the class is `ByAngular`):

* `options`
* `repeater`
* `model`
* `binding`
* `buttonText`
* `cssContainingText`
* `partialButtonText`

Few method signatures of are different:

* `repeaterCell`
* `repeaterRow`
* `repeaterColumn`

The implementation of the correspondent keyword methods is through chaining the
ngWebDriver methods e.g.

```java
case "repeaterColumn":
  ngDriver.waitForAngularRequestsToFinish();
  ByAngularRepeater _elementRepeater = ByAngular.repeater(selectorValue);
  ByAngularRepeaterColumn _elementRepeaterColumn = _elementRepeater.column(selectorColumn);
  _element = driver.findElement(_elementRepeaterColumn);
  return _element;

```
Some methods are unique to jProtracror, they do not exist in ngWebDriver:

* `selectedOption`
* `selectedRepeaterOption`


In addition the "text" keyword is recognized with the usual implementation through `xpath` locator:
```java
String.format("//%s[contains(normalize-space(text()), '%s')]", (selectorTagName != null) ? selectorTagName : "*", selectorValue);
```
alternatively if the `selectorTagName` is provided, the Java 8 stream based impplementation is possible:
```java
_element = driver.findElements(By.tagName(selectorTagName)).stream()
  .filter(o -> o.getText().contains(selectorValue)).findFirst().get();
```

### Introduction

The original Keyword Driven Framework [suggests](http://toolsqa.com/selenium-webdriver/keyword-driven-framework/introduction/)
identifying few columns (not necessarily exactly four, though).

  * __Summary__: *brief description of the step*
  * __Target__: *name of the Web Page object/element, like "Link" or "Input"*
  * __Action__: *name of the action, which will be performed on Target Element such as click, open browser, input text etc.*
  * __Data__: *any value which is needed by the Object to perform any action, like text value for input field.*


It is very likely inspired by
![selenium_ide](https://github.com/sergueik/skdf/blob/master/images/selenium_ide.png)

[Selenium IDE Firefox Add-On](https://addons.mozilla.org/en-US/firefox/addon/selenium-ide/)
Command, Target, Value columns, with significanylty narrowed choice of commands (Keywords).
The advantages from taking such approach are discussed many times:

  * __Less Technical Expertise__:  manual testers or non technical testers can easily write test scripts for automation using the Framework than code straight.
  * __Easy To Understand__: With no coding is exposed, the test flow is easy to read and understand. Keywords & actions are descriptive.
  * __Early Start__: One can start building Keyword Driven test cases immediately deferring more challenging tasks like Page Object model to a later stage. Keyword steps are quick to identify from requirements documentation or manual test.
  * __Re-usability__: With implementing modularization in Keyword Driven, Re-usability can be further increased. Equipped with a stable and powerful Execution Engine in Keyword Driven Framework, it encourage extreme code re-usability.
  * __Automation__: Excel file is (a lot) easier to produce by a recording tool than a full blown program.

### Work in Progress

Adding 

* [ahajamit/chrome-devtools-webdriver-integration](https://github.com/sahajamit/chrome-devtools-webdriver-integration) Selenium 3.x compatible Chrome Devtool Selenium extension project that exposes whole set of [Chrome DevTools Protocol automation API](https://chromedevtools.github.io/devtools-protocol/) to Selenium in similar fashion Selenium 4 is doing (Selenum 4 is currently in alpha) as __cdp\_integration__
* [sukgu/shadow-automation-selenium](https://github.com/sukgu/shadow-automation-selenium) shadow ROOT DOM automation javascript API wrapper project, which offrs custom API as __shadow\_automation__

No keywords defined for any of those yet.

### Note

the urls `http://www.seleniumeasy.com/test/basic-first-form-demo.html` and `https://www.seleniumeasy.com/test/input-form-demo.html` no longer exist on `http://www.seleniumeasy.com` though the former is still referenced in documentation for Python testers.

These pages were apparently mirrored on `https://demo.anhtester.com`
The directory `http://www.seleniumeasy.com/test/` is also gone and there is no mirror
The update of test material is a work in progress. The sheets `KeywordFramework` are curently disabled.
The `Alert` sheet is updated and enabled (it had a typo in the custom method name for processing the alert popup)

### See Also

 * [qaf](https://github.com/qmetry/qaf)
 * [NPOI](https://github.com/dotnetcore/NPOI)
 * [ExcelDataReader (.net)](https://github.com/ExcelDataReader/ExcelDataReader)
 * [LinqToExcel](https://github.com/paulyoder/LinqToExcel)
 * [save-selenium-webdriver-testng-result-excel](http://www.techbeamers.com/save-selenium-webdriver-testng-result-excel/)
 * [DataProvider - Data Driven Testing with Selenium and TestNG](http://functionaltestautomation.blogspot.in/2009/10/dataprovider-data-driven-testing-with.html)
 * [Excel Java poi helpers](https://github.com/Crab2died/Excel4J) 
 * [Fast Excel poi-clean Java implementation](https://github.com/dhatim/fastexcel)

### License
This project is licensed under the terms of the MIT license.

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)
