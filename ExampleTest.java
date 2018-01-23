package com.github.sergueik.jprotractor;

/**
 * Copyright 2017 Serguei Kouzmine
 */
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class ExampleTest {

	private static WebDriver driver = null;
	private static WebDriverWait wait;

	@Test
	public void invalidCredentialsTest() {
		System.setProperty("webdriver.chrome.driver",
				(new File("c:/java/selenium/chromedriver.exe")).getAbsolutePath());

		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, 10);
		wait.pollingEvery(500, TimeUnit.MILLISECONDS);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		// open the start page
		driver.get("http://www.store.demoqa.com");
		// go to login page
		driver.findElement(By.xpath(".//*[@id='account']/a")).click();
		ExpectedCondition<Boolean> urlChange = driver -> driver.getCurrentUrl()
				.matches("http://store.demoqa.com/products-page/your-account/");
		wait.until(urlChange);

		// log in
		driver.findElement(By.id("log")).sendKeys("testuser_3");
		driver.findElement(By.id("pwd")).sendKeys("Test@123");
		driver.findElement(By.id("login")).click();

		String errorMessage = wait.until((WebDriver driver) -> {
			WebElement element = null;
			try {
				element = driver
						.findElement(By.cssSelector("#ajax_loginform > p.response"));
			} catch (Exception e) {
				return null;
			}
			return (element.isDisplayed()) ? element.getText() : null;
		});

		assertThat(errorMessage, notNullValue());
		assertThat(errorMessage, containsString(
				"The password you entered for the username testuser_3 is incorrect"));
		driver.quit();
	}
}