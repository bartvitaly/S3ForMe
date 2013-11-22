package test.java.cors;

import java.io.IOException;

import me.s3for.common.FileUtils;
import me.s3for.common.S3Utils;
import me.s3for.common.WebDriverCommon;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import test.java.TestInitializeWebDriver;

import com.amazonaws.services.s3.model.CORSRule.AllowedMethods;

public class Cors_01_Positive_AllowedOrigin_Test extends
		TestInitializeWebDriver {

	static final String RESPONSE_CODE_SUCCESS = "200";

	String crossOriginUrl = serverS3 + "/" + homeNoHttp + "/" + TEST_FILE;
	String corsJsUri = home + "/" + corsJs;
	String nodeXpath = "//body//div";

	S3Utils s3Utils;

	@BeforeMethod(groups = { "cors" })
	public void init() {
		s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		s3Utils.setBacket(bucketName);
	}

	@AfterMethod(groups = { "cors" })
	public void tear() {
		WebDriverCommon.takeScreenshot(driver);
		driver.close();
		driver.quit();
	}

	@Test(groups = { "cors" })
	public void positiveGetAllowedOriginTest() throws IOException {
		Assert.assertEquals(testAllowedOrigin(home, "GET", nodeXpath),
				RESPONSE_CODE_SUCCESS);
	}

	// @Test(groups = { "cors" })
	// public void positivePost_Test() throws IOException {
	// Assert.assertEquals(testAllowedOrigin(home, "POST", nodeXpath),
	// RESPONSE_CODE_SUCCESS);
	// }
	//
	// @Test(groups = { "cors" })
	// public void positivePut_Test() throws IOException {
	// Assert.assertEquals(testAllowedOrigin(home, "PUT", nodeXpath),
	// RESPONSE_CODE_SUCCESS);
	// }
	//
	// @Test(groups = { "cors" })
	// public void positiveDelete_Test() throws IOException {
	// Assert.assertEquals(testAllowedOrigin(home, "DELETE", nodeXpath),
	// RESPONSE_CODE_SUCCESS);
	// }
	//
	// @Test(groups = { "cors" })
	// public void positiveHead_Test() throws IOException {
	// Assert.assertEquals(testAllowedOrigin(home, "HEAD", nodeXpath),
	// RESPONSE_CODE_SUCCESS);
	// }

	public String testAllowedOrigin(String allowedOrigin, String requestType,
			String nodeXpath) throws IOException {

		String ruleId = "rule";
		int maxAgeSeconds = 1;
		AllowedMethods[] allowedMethods = new AllowedMethods[] { S3Utils
				.getAllowedMethod(requestType) };
		String[] allowedOrigins = new String[] { allowedOrigin };
		String[] allowedHeaders = new String[] { "*" };
		String[] exposedHeaders = new String[] { "*" };

		s3Utils.setCorsConfiguration(ruleId, maxAgeSeconds, allowedMethods,
				allowedOrigins, allowedHeaders, exposedHeaders);

		String testHtm = S3Utils.creteCorsHtml(path, corsJsUri, requestType,
				crossOriginUrl, nodeXpath);

		s3Utils.putTextFile(INDEX_FILE, testHtm, bucketName);
		s3Utils.putTextFile(TEST_FILE, "", bucketName);
		s3Utils.putTextFile(corsJs, FileUtils.read(corsJsPath), bucketName);

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
