package test.java;

import java.io.File;
import java.io.IOException;

import me.s3for.common.S3Utils;

import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

public class Api_03_Delete_Object_Test extends TestInitialize {

	S3Utils s3Utils, s3UtilsAws;
	String fileName = "file.txt";
	File file, fileAws;

	/**
	 * @desc The code to be run before each test
	 */

	@BeforeGroups(groups = { "api" })
	public void before() {
		// initiate S3 and AWS
		s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		s3UtilsAws = new S3Utils();

		// set bucket to work with
		s3Utils.setBacket(bucketName);
		s3UtilsAws.setBacket(bucketNameAws);

		// put a file in a basket
		file = S3Utils.createSampleFile();
		s3Utils.put(fileName, file);
		s3UtilsAws.put(fileName, file);

	}

	/**
	 * @desc Check ability to delete object from a bucket
	 * 
	 * @throws IOException
	 */

	@Test(groups = { "api" })
	public void bucketWriteTest() throws Exception {

		// Delete objects
		s3Utils.deleteObject(fileName);
		s3UtilsAws.deleteObject(fileName);

		// Get S3 objects
		Assert.assertNull(s3Utils.get(fileName), "Object was not deleted");
		Assert.assertNull(s3UtilsAws.get(fileName), "Object was not deleted");

	}
}
