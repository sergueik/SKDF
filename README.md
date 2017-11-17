### About SKDF

![TestCase.xls](https://github.com/sergueik/skdf/blob/master/images/testcase_selenium.png)

This directory contains a skeleton [Keyword-Driven Framework](http://toolsqa.com/selenium-webdriver/keyword-driven-framework/introduction/) project based on
[ashokkhape/automation_framework_selenium](https://github.com/ashokkhape/automation_framework_selenium) and [ashokkhape/automation_framework_selenium](https://github.com/ashokkhape/automation_framework_selenium) and [selenium-webdriver-software-testing/keyword-driven-framework](https://github.com/selenium-webdriver-software-testing/keyword-driven-framework)

The project builds a runnable jar:
```bash
cp TestCase.xls ~/Desktop
pushd jprotractor
mvn clean install
java -jar target/skdf_jprotractor-0.9-SNAPSHOT.jar
popd
pushd ngwebdriver
mvn clean install
java -jar target/skdf_ngwebdriver-0.4-SNAPSHOT.jar
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
  Class<?> _class = Class.forName("org.utils.KeywordLibrary");
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

### See Also

* [qaf](https://github.com/qmetry/qaf)

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)
