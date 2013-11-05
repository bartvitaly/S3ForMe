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

public class Cors_01_Negative_Test extends TestInitializeWebDriver {

	static final String RESPONSE_MESSAGE_403 = "403";
	static final String RESPONSE_MESSAGE_501 = "501";

	String crossOriginUrl = serverS3 + "/" + homeNoHttp + "/" + TEST_FILE;
	String corsJsUri = home + "/cors.js";
	String nodeXpath = "//body//div";

	S3Utils s3Utils;

	@BeforeTest(groups = { "cors" })
	public void init() {
		s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		s3Utils.setBacket(bucketName);
		s3Utils.getClient().deleteBucketCrossOriginConfiguration(bucketName);
	}

	@AfterTest(groups = { "cors" })
	public void tear() {
		WebDriverCommon.takeScreenshot(driver);
		driver.close();
		driver.quit();
	}

	@Test(groups = { "cors" })
	public void negativeGet_Test() throws IOException {
		Assert.assertEquals(testAllowedOrigin(home, "GET", nodeXpath),
				RESPONSE_MESSAGE_403);
	}

	@Test(groups = { "cors" })
	public void negativePost_Test() throws IOException {
		Assert.assertEquals(testAllowedOrigin(home, "POST", nodeXpath),
				RESPONSE_MESSAGE_501);
	}

	@Test(groups = { "cors" })
	public void negativePut_Test() throws IOException {
		Assert.assertEquals(testAllowedOrigin(home, "PUT", nodeXpath),
				RESPONSE_MESSAGE_403);
	}

	@Test(groups = { "cors" })
	public void negativeDelete_Test() throws IOException {
		Assert.assertEquals(testAllowedOrigin(home, "DELETE", nodeXpath),
				RESPONSE_MESSAGE_403);
	}

	@Test(groups = { "cors" })
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
		String[] allowedOrigins = new String[] { "*" };
		String[] allowedHeaders = new String[] { null };
		String[] exposedHeaders = new String[] { null };

		s3Utils.setCorsConfiguration(ruleId, maxAgeSeconds, allowedMethods,
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
