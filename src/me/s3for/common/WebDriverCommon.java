package me.s3for.common;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

public class WebDriverCommon {

	public WebDriver driver;

	public static void takeScreenshot(WebDriver driver) {

		String fileName = "\\test-output\\screenshot.png";
		File scrFile = ((TakesScreenshot) driver)
				.getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile,
					new File((new File(".")).getCanonicalPath() + fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void waitForPageLoaded(WebDriver driver) {

		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript(
						"return document.readyState").equals("complete");
			}
		};

		org.openqa.selenium.support.ui.Wait<WebDriver> wait = new WebDriverWait(
				driver, 30);
		try {
			wait.until(expectation);
		} catch (Throwable error) {
			Assert.assertFalse(
					"Timeout waiting for Page Load Request to complete.", true);
		}
	}

	private void setDefaultTimeout() {
		driver.manage()
				.timeouts()
				.implicitlyWait(
						Integer.valueOf(PropertiesUtils.getProperty("timeout")),
						TimeUnit.SECONDS);
	}

	private void setZeroTimeout() {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
	}

	public WebElement waitFluently(final By locator, int timeout) {

		setZeroTimeout();

		WebElement element = null;
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeout);
			element = wait.until(ExpectedConditions
					.visibilityOfElementLocated(locator));
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setDefaultTimeout();
		return element;
	}

	public WebElement wait(final By locator, int timeout) {

		setZeroTimeout();

		// use a "custom" ExpectedCondition
		WebElement element = null;
		try {
			FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver)
					.withTimeout(timeout, TimeUnit.SECONDS)
					.pollingEvery(timeout / 5, TimeUnit.SECONDS)
					.ignoring(NoSuchElementException.class,
							StaleElementReferenceException.class);

			element = wait.until(new Function<WebDriver, WebElement>() {
				public WebElement apply(WebDriver driver) {
					return driver.findElement(locator);
				}
			});
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		setDefaultTimeout();

		return element;
	};

}
