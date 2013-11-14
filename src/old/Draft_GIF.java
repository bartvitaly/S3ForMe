package old;

import java.io.IOException;

import me.s3for.common.PropertiesUtils;
import me.s3for.common.S3Utils;
import me.s3for.common.WebDriverCommon;
import me.s3for.common.XmlUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import test.java.TestInitialize;

public class Draft_GIF extends TestInitialize {

	final static String home = PropertiesUtils.getProperty("home");
	final static String homeAlias = PropertiesUtils.getProperty("homeAlias");
	final static String browser = PropertiesUtils.getProperty("browser");
	final static String corsXml = PropertiesUtils.getProperty("corsXML");
	final static String corsXmlPath = "/static/" + corsXml;

	final static String bucket = PropertiesUtils.getProperty("bucket");

	@AfterTest(groups = { "corsWD", "corsWDNew" })
	public void tear() {
		WebDriverCommon.takeScreenshot(driver);
		driver.close();
		driver.quit();
	}

	@Test(groups = { "corsWDNew" })
	public void allowedOrigin() throws IOException {

		// Adjust cors file according to test
		XmlUtils xmlUtils = new XmlUtils(corsXmlPath);
		xmlUtils.setNode("//CORSRule/AllowedOrigin", "home");
		String corsXmlEdited = xmlUtils.transform();

		// Put the file to a basket
		S3Utils s3Utils = new S3Utils();
		s3Utils.putTextFile(corsXml, corsXmlEdited, bucket);

		driver.get(PropertiesUtils.getProperty("homeAlias") + "/index.htm");
		WebElement element = wait(By.xpath("//div[contains(text(),'GIF')]"), 3);

		Assert.assertTrue(element.isDisplayed());

	}

	@Test(groups = { "corsWD" })
	public void positiveCors() throws IOException {

		driver.get(home);

		WebElement element = wait(By.xpath("//div[contains(text(),'GIF')]"), 3);

		Assert.assertTrue(element != null && element.isDisplayed());

	}

	@Test(groups = { "corsWD" })
	public void negativeCors() throws IOException {

		driver.get(homeAlias + "/index.htm");

		WebElement element = wait(By.xpath("//div[contains(text(),'GIF')]"), 3);

		Assert.assertTrue(element == null || !element.isDisplayed());

	}

}
