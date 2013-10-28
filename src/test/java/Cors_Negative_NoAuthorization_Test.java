//package test.java;
//
//import java.io.IOException;
//
//import me.s3for.common.S3Utils;
//import me.s3for.common.WebDriverCommon;
//
//import org.testng.annotations.AfterTest;
//import org.testng.annotations.BeforeTest;
//import org.testng.annotations.Test;
//
//import com.amazonaws.services.s3.model.CORSRule.AllowedMethods;
//
//public class Cors_Negative_NoAuthorization_Test extends TestInitialize {
//
//	static final String RESPONSE_CODE_SUCCESS = "200";
//	static final String RESPONSE_MESSAGE_NETWORK_ERROR = "NS_ERROR_FAILURE";
//
//	String crossOriginUrl = home;
//	String corsJsUri = home + "/cors.js";
//	String nodeXpath = "//body//div";
//
//	S3Utils s3Utils;
//
//	@BeforeTest(groups = { "corsAPI" })
//	public void init() {
//		s3Utils = new S3Utils(false, serverS3);
//		s3Utils.setBacket(bucket);
//	}
//
//	@AfterTest(groups = { "corsAPI" })
//	public void tear() {
//		WebDriverCommon.takeScreenshot(driver);
//		driver.close();
//		driver.quit();
//	}
//
//	@Test(groups = { "corsAPI" })
//	public void positiveGet_Test() throws IOException {
//		testAllowedOrigin(home, "GET", nodeXpath, RESPONSE_CODE_SUCCESS);
//	}
//
//	public void testAllowedOrigin(String allowedOrigin, String requestType,
//			String nodeXpath, String expectedResponse) throws IOException {
//
//		String[] allowedOrigins = new String[] { allowedOrigin };
//
//		AllowedMethods[] allowedMethods = new AllowedMethods[] { S3Utils
//				.getAllowedMethod(requestType) };
//		s3Utils.setCrosConfiguration(allowedOrigins, allowedMethods);
//
//	}
//
// }
