package test.java;

import me.s3for.common.FileUtils;
import me.s3for.common.PropertiesUtils;
import me.s3for.common.WebDriverCommon;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeSuite;

public class TestInitialize extends WebDriverCommon {

	final static String keyS3 = PropertiesUtils.getProperty("S3AccessKeyId");
	final static String secretS3 = PropertiesUtils
			.getProperty("S3SecretAccessKeyID");
	final static String serverS3 = PropertiesUtils.getProperty("S3server");

	final static String home = PropertiesUtils.getProperty("home");
	final static String homeNoHttp = PropertiesUtils.getProperty("homeNoHttp");
	final static String browser = PropertiesUtils.getProperty("browser");
	final static String corsXml = PropertiesUtils.getProperty("corsXML");
	final static String corsJs = PropertiesUtils.getProperty("corsJs");
	final static String corsXmlPath = FileUtils.getRootPath() + "\\static\\"
			+ corsXml;

	final static String corsJsPath = FileUtils.getRootPath() + "\\static\\"
			+ corsJs;
	static final String INDEX_FILE = PropertiesUtils.getProperty("indexHtm");
	static final String TEST_FILE = PropertiesUtils.getProperty("testFile");

	final static String path = FileUtils.getRootPath() + "\\static\\"
			+ INDEX_FILE;

	final static String TEST_OUTPUT_FOLDER = FileUtils.getRootPath()
			+ "\\test-output\\";

	final static String bucketName = PropertiesUtils.getProperty("bucket");
	final static String bucketNameAws = PropertiesUtils
			.getProperty("AWSbucket");

	public static String[] avoidKeys = { "Last-Modified", "Keep-Alive",
			"Accept-Ranges", "Connection", "Vary" };

	public static String[] avoidKeysMultipart = { "ETag", "Last-Modified",
			"Keep-Alive", "Accept-Ranges", "Connection", "Vary" };

	public static Logger logger = Logger
			.getLogger(TestInitializeWebDriver.class);

	@BeforeSuite(groups = { "cors" })
	public void initLogger() {
		BasicConfigurator.configure();
		// DOMConfigurator.configure("log4j.xml");
	}

}
