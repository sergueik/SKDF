### Info

This directory contains experimental port of the SKDF to [CDP 4 Java client library](https://github.com/webfolderio/cdp4j) backend.
The [Chrome Devtools Protocol](https://github.com/ChromeDevTools/devtools-protocol) is an entirely different API stack than [Selenium / JSONWireProtocol protocol](https://w3c.github.io/webdriver/webdriver-spec.html`). In partiular it no longer uses strongly typed locators, and number of methods belong to `session` rather then `element`.
For example the basic test
```java
import org.openqa.selenium.WebDriver;

WebDriver driver = new ChromeDriver();
WebDriverWait wait = new WebDriverWait(driver, 10);
// open the start page
driver.get("http://www.store.demoqa.com");

// go to login page
driver.findElement(By.xpath("//*[@id='account']/a")).click();
wait.until( driver ->
driver.getCurrentUrl().matches("http://store.demoqa.com/products-page/your-account/"));

// log in
driver.findElement(By.id("log")).sendKeys("testuser_3");
// with invalid password
driver.findElement(By.id("pwd")).sendKeys("Test@123");

driver.findElement(By.id("login")).click();

// confirm the error message is displayed
assertThat(wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(
  "#ajax_loginform > p.response")))).getText(),
  containsString("The password you entered for the username testuser_3 is incorrect));
driver.quit();
```

would become
```java
Launcher launcher = new Launcher();
SessionFactory factory = launcher.launch();
Session session = factory.create();
session.clearCookies();
session.clearCache();

// navigate to start screen
session.navigate("http://www.store.demoqa.com");

// go to login page
session.waitUntil(s -> s.getObjectIds("#account > a").size() > 0, 5000, 500 );
executeScript(session, "function() { this.click(); }", "#account > a");
session.waitUntil( s -> s.getLocation().matches(
  "http://store.demoqa.com/products-page/your-account/"), 5000, 500 );

// log in
session.waitUntil(s -> s.getObjectIds("#log").size() > 0, 5000, 500 );

session.focus("#log");
// with invalid password

session.sendKeys("testuser_3");
session.focus("#pwd");
session.sendKeys("Test@123");
executeScript(session, "function() { this.click(); }", "#login");

// confirm the error message is displayed
session.waitUntil(s -> s.getObjectIds(
  "//form[@id='ajax_loginform']/p[@class='response']/text()").size() > 0, 5000, 500 );

assertThat(session.getText(
  "//form[@id='ajax_loginform']/p[@class='response']"),
  containsString("The password you entered for the username testuser_3 is incorrect));

```

This makes CDP4J a good candidate for use behind the scenes with SKDF facade.

### Note:

The [Chrome DevTools Protocol for Java](https://github.com/webfolderio/cdp4j) is distributed under [GNU Affero General Public License v3.0](https://en.wikipedia.org/wiki/GNU_Affero_General_Public_License)  and it not free to commercial use: buying a license is __mandatory__ to use __CDP4J__
