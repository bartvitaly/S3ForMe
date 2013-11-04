package test.java;

import java.io.IOException;

import me.s3for.common.S3Utils;
import me.s3for.common.WebDriverCommon;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.amazonaws.services.s3.model.CORSRule.AllowedMethods;

public class Cors_Negative_Test extends TestInitialize {

	static final String RESPONSE_MESSAGE_403 = "403";

	String crossOriginUrl = homeAlias;
	String corsJsUri = home + "/cors.js";
	String nodeXpath = "//body//div";

	S3Utils s3Utils;

	@BeforeTest(groups = { "corsAPI_Negative" })
	public void init() {
		s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		s3Utils.setBacket(bucketName);
	}

	@AfterTest(groups = { "corsAPI_Negative" })
	public void tear() {
		WebDriverCommon.takeScreenshot(driver);
		driver.close();
		driver.quit();
	}

	@Test(groups = { "corsAPI_Negative" })
	public void negativeGet_Test() throws IOException {
		Assert.assertEquals(testAllowedOrigin(home, "GET", nodeXpath),
				RESPONSE_MESSAGE_403);
	}

	@Test(groups = { "corsAPI_Negative" })
	public void negativePost_Test() throws IOException {
		Assert.assertEquals(testAllowedOrigin(home, "POST", nodeXpath),
				RESPONSE_MESSAGE_403);
	}

	@Test(groups = { "corsAPI_Negative" })
	public void negativePut_Test() throws IOException {
		Assert.assertEquals(testAllowedOrigin(home, "PUT", nodeXpath),
				RESPONSE_MESSAGE_403);
	}

	@Test(groups = { "corsAPI_Negative" })
	public void negativeDelete_Test() throws IOException {
		Assert.assertEquals(testAllowedOrigin(home, "DELETE", nodeXpath),
				RESPONSE_MESSAGE_403);
	}

	@Test(groups = { "corsAPI_Negative" })
	public void negativeHead_Test() throws IOException {
		Assert.assertEquals(testAllowedOrigin(home, "HEAD", nodeXpath),
				RESPONSE_MESSAGE_403);
	}

	public String testAllowedOrigin(String allowedOrigin, String requestType,
			String nodeXpath) throws IOException {

		String ruleId = "rule";
		int maxAgeSeconds = 1;
		AllowedMethods[] allowedMethods = new AllowedMethods[] { S3Utils
				.getAllowedMethod(requestType) };
		String[] allowedOrigins = new String[] { allowedOrigin };
		String[] allowedHeaders = new String[] { "x-custom-header",
				"x-authorization" };
		String[] exposedHeaders = new String[] { null };

		s3Utils.setCrosConfiguration(ruleId, maxAgeSeconds, allowedMethods,
				allowedOrigins, allowedHeaders, exposedHeaders);
		String testHtm = S3Utils.creteCorsHtml(path, corsJsUri, requestType,
				crossOriginUrl, nodeXpath);

		s3Utils.putTextFile(INDEX_FILE, testHtm, bucketName);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		driver.get(home);
		WebElement element = wait(By.xpath(nodeXpath), 3);

		return element.getText();

	}

}