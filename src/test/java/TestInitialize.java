package test.java;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeSuite;

public class TestInitialize {

	public static Logger logger = Logger.getLogger(TestInitialize.class);

	@BeforeSuite(groups = { "test" })
	public void initLogger() {
		BasicConfigurator.configure();
		// DOMConfigurator.configure("log4j.xml");
	}

}
