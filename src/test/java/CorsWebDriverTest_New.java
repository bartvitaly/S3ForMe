package test.java;

import java.io.IOException;

import me.s3for.common.S3Utils;
import me.s3for.common.WebDriverCommon;
import me.s3for.common.XmlUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

public class CorsWebDriverTest_New extends TestInitialize {

	static final String RESPONSE_CODE_SUCCESS = "200";
	static final String RESPONSE_MESSAGE_NETWORK_ERROR = "NS_ERROR_FAILURE";

	@AfterTest(groups = { "corsWDNew" })
	public void tear() {
		WebDriverCommon.takeScreenshot(driver);
		driver.close();
		driver.quit();
	}

	@Test(groups = { "corsWDNew" })
	public void allowedOriginNegative() throws IOException {

		// Adjust cors file according to test
		System.out.println(corsXmlPath);
		XmlUtils xmlUtils = new XmlUtils(corsXmlPath);
		xmlUtils.setNode("//CORSRule/AllowedOrigin", homeAlias);
		String corsXmlEdited = xmlUtils.transform();

		// Put the file to a basket
		S3Utils s3Utils = new S3Utils();
		s3Utils.putTextFile(corsXml, corsXmlEdited, bucketName);

		driver.get(homeAlias + "/index.htm");
		WebElement element = wait(By.xpath("//body/div[@response='status']"), 3);

		System.out.println(element.getText());

		Assert.assertNotEquals(element.getText(), RESPONSE_CODE_SUCCESS);

	}

	@Test(groups = { "corsWDNew" })
	public void allowedOriginPositive() throws IOException {

		// Adjust cors file according to test
		System.out.println(corsXmlPath);
		XmlUtils xmlUtils = new XmlUtils(corsXmlPath);
		xmlUtils.setNode("//CORSRule/AllowedOrigin", homeAlias);
		String corsXmlEdited = xmlUtils.transform();

		// Put the file to a basket
		S3Utils s3Utils = new S3Utils();
		s3Utils.putTextFile(corsXml, corsXmlEdited, bucketName);

		driver.get(home);
		WebElement element = wait(By.xpath("//body/div[@response='status']"), 3);

		System.out.println(element.getText());

		Assert.assertEquals(element.getText(), RESPONSE_CODE_SUCCESS);

	}

}
