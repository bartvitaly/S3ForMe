package test.java;

import me.s3for.common.FileUtils;
import me.s3for.common.PropertiesUtils;
import me.s3for.common.WebDriverCommon;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeGroups;

public class TestInitialize extends WebDriverCommon {

	public final static String keyS3 = PropertiesUtils
			.getProperty("S3AccessKeyId");
	public final static String secretS3 = PropertiesUtils
			.getProperty("S3SecretAccessKeyID");
	public final static String serverS3 = PropertiesUtils
			.getProperty("S3server");

	public final static String home = PropertiesUtils.getProperty("home");
	public final static String homeNoHttp = PropertiesUtils
			.getProperty("homeNoHttp");
	final static String browser = PropertiesUtils.getProperty("browser");
	final static String corsXml = PropertiesUtils.getProperty("corsXML");
	public final static String corsJs = PropertiesUtils.getProperty("corsJs");
	final static String corsXmlPath = FileUtils.getRootPath() + "\\static\\"
			+ corsXml;

	public final static String corsJsPath = FileUtils.getRootPath()
			+ "\\static\\" + corsJs;
	public static final String INDEX_FILE = PropertiesUtils
			.getProperty("indexHtm");
	public static final String TEST_FILE = PropertiesUtils
			.getProperty("testFile");

	public final static String path = FileUtils.getRootPath() + "\\static\\"
			+ INDEX_FILE;

	public final static String TEST_OUTPUT_FOLDER = FileUtils.getRootPath()
			+ "\\test-output\\";

	public final static String bucketName = PropertiesUtils
			.getProperty("bucket");
	public final static String bucketNameAws = PropertiesUtils
			.getProperty("AWSbucket");

	public static String[] avoidKeys = { "Last-Modified", "Keep-Alive",
			"Accept-Ranges", "Connection", "Vary" };

	public static String[] avoidKeysMultipart = { "ETag", "Last-Modified",
			"Keep-Alive", "Accept-Ranges", "Connection", "Vary" };

	public static Logger logger = Logger.getLogger(TestInitialize.class);

	@BeforeGroups(groups = { "api", "cors" })
	public void initLogger() {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
		// DOMConfigurator.configure("log4j.xml");
	}
}
