package test.java;

import java.util.concurrent.TimeUnit;

import me.s3for.common.FileUtils;
import me.s3for.common.PropertiesUtils;
import me.s3for.common.WebDriverCommon;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

public class TestInitialize extends WebDriverCommon {

	final static String keyS3 = PropertiesUtils.getProperty("S3AccessKeyId");
	final static String secretS3 = PropertiesUtils
			.getProperty("S3SecretAccessKeyID");
	final static String serverS3 = PropertiesUtils.getProperty("S3server");

	final static String home = PropertiesUtils.getProperty("home");
	final static String homeAlias = PropertiesUtils.getProperty("homeAlias");
	final static String browser = PropertiesUtils.getProperty("browser");
	final static String corsXml = PropertiesUtils.getProperty("corsXML");
	final static String corsXmlPath = FileUtils.getRootPath() + "\\static\\"
			+ corsXml;

	static final String INDEX_FILE = PropertiesUtils.getProperty("indexHtm");;
	final static String path = FileUtils.getRootPath() + "\\static\\"
			+ INDEX_FILE;

	final static String bucket = PropertiesUtils.getProperty("bucket");

	public static Logger logger = Logger.getLogger(TestInitialize.class);

	@BeforeSuite(groups = { "test", "bucketWrite", "corsWD", "corsWDNew",
			"corsAPI", "corsAPI_Negative" })
	public void initLogger() {
		BasicConfigurator.configure();
		// DOMConfigurator.configure("log4j.xml");
	}

	@BeforeTest(groups = { "corsWD", "corsWDNew", "corsAPI" })
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
