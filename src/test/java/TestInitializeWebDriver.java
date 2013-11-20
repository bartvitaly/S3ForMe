package test.java;

import java.util.concurrent.TimeUnit;

import me.s3for.common.PropertiesUtils;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

public class TestInitializeWebDriver extends TestInitialize {

	@BeforeMethod(groups = { "cors" })
	public void createDriver() {

		System.setProperty("webdriver.chrome.driver",
				PropertiesUtils.getProperty("webdriver.chrome.driver"));

		if (browser.equals("chrome")) {
			driver = new ChromeDriver();
		} else if (browser.equals("firefox")) {
			driver = new FirefoxDriver();
		} else {
			driver = new HtmlUnitDriver();
		}

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

}
