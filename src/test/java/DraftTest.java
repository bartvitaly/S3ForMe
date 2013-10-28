package test.java;

import me.s3for.common.S3Utils;

import org.testng.annotations.Test;

public class DraftTest {

	@Test
	public void f() {

		String path = "D:\\work\\projects\\eclipse\\workspace\\S3ForMe\\static\\index.htm";
		String corsJsUri = "http://rest.s3for.me/www.artguide.com.ua/cors.js";
		String requestType = "GET";
		String crossOriginUrl = "http://rest.s3for.me/www.artguide.com.ua/";
		String nodeXpath = "//body//div[@name='response']/script";

		System.out.println(S3Utils.creteCorsHtml(path, corsJsUri, requestType,
				crossOriginUrl, nodeXpath));

	}

}
