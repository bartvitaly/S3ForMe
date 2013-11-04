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

public class Cors_Positive_Test extends TestInitialize {

	static final String RESPONSE_CODE_SUCCESS = "200";
	static final String RESPONSE_MESSAGE_NETWORK_ERROR = "NS_ERROR_FAILURE";

	String crossOriginUrl = home;
	String corsJsUri = home + "/cors.js";
	String nodeXpath = "//body//div";

	S3Utils s3Utils;

	@BeforeTest(groups = { "corsAPI" })
	public void init() {
		s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		s3Utils.setBacket(bucketName);
	}

	@AfterTest(groups = { "corsAPI" })
	public void tear() {
		WebDriverCommon.takeScreenshot(driver);
		driver.close();
		driver.quit();
	}

	@Test(groups = { "corsAPI" })
	public void positiveGet_Test() throws IOException {
		Assert.assertTrue(testAllowedOrigin(home, "GET", nodeXpath,
				RESPONSE_CODE_SUCCESS));
	}

	@Test(groups = { "corsAPI" })
	public void positivePost_Test() throws IOException {
		Assert.assertTrue(testAllowedOrigin(home, "POST", nodeXpath,
				RESPONSE_CODE_SUCCESS));
	}

	@Test(groups = { "corsAPI" })
	public void positivePut_Test() throws IOException {
		Assert.assertTrue(testAllowedOrigin(home, "PUT", nodeXpath,
				RESPONSE_CODE_SUCCESS));
	}

	@Test(groups = { "corsAPI" })
	public void positiveDelete_Test() throws IOException {
		Assert.assertTrue(testAllowedOrigin(home, "DELETE", nodeXpath,
				RESPONSE_CODE_SUCCESS));
	}

	@Test(groups = { "corsAPI" })
	public void positiveHead_Test() throws IOException {
		Assert.assertTrue(testAllowedOrigin(home, "HEAD", nodeXpath,
				RESPONSE_CODE_SUCCESS));
	}

	public boolean testAllowedOrigin(String allowedOrigin, String requestType,
			String nodeXpath, String expectedResponse) throws IOException {

		String ruleId = "rule";
		int maxAgeSeconds = 1;
		AllowedMethods[] allowedMethods = new AllowedMethods[] { S3Utils
				.getAllowedMethod(requestType) };
		String[] allowedOrigins = new String[] { allowedOrigin };
		String[] allowedHeaders = new String[] { "x-custom-header",
				"x-authorization" };
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

		return element.getText().equals(expectedResponse);

	}

}
