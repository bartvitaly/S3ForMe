package test.java.api;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.s3for.common.Common;
import me.s3for.common.FileUtils;
import me.s3for.common.S3Utils;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import test.java.TestInitialize;

import com.amazonaws.services.s3.model.S3ObjectSummary;

public class Api_07_Get_ObjectList_Test extends TestInitialize {

	S3Utils s3Utils, s3UtilsAws;
	String fileName = "file.txt";

	File file, fileAws;

	/**
	 * @desc The code to be run before each test
	 */

	@BeforeMethod(groups = { "api" })
	public void init() {
		// initiate S3 and AWS
		s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		s3UtilsAws = new S3Utils();

		// set bucket to work with
		s3Utils.setBacket(bucketName);
		s3UtilsAws.setBacket(bucketNameAws);

		// put a file in a basket
		file = S3Utils.createSampleFile();

		Assert.assertFalse(s3Utils.deleteObject(fileName),
				"An object was not deleted");
		Assert.assertFalse(s3UtilsAws.deleteObject(fileName),
				"An object was not deleted");

	}

	@AfterMethod(groups = { "api" })
	public void tear() {
		s3Utils.deleteObject(fileName);
		s3UtilsAws.deleteObject(fileName);
	}

	/**
	 * @desc Check ability to write to a bucket
	 * 
	 * @throws IOException
	 */

	@Test(groups = { "api" })
	public void bucketGetObjectsList_Test() throws Exception {

		List<S3ObjectSummary> objects = s3Utils.getObjectList();
		List<S3ObjectSummary> objectsAws = s3UtilsAws.getObjectList();

		String object = "";
		String objectAws = "";
		System.out.println("\nS3 bucket:\n");
		for (int i = 0; i < objects.size(); i++) {
			object = object + "\n " + objects.get(i).getKey();
			System.out.println(objects.get(i).getKey());
		}

		System.out.println("\nAws bucket:\n");
		for (int i = 0; i < objectsAws.size(); i++) {
			objectAws = objectAws + "\n " + objectsAws.get(i).getKey();
			System.out.println(objectsAws.get(i).getKey());
		}

		FileUtils.write(FileUtils.getRootPath() + "\\Api_07\\result.txt",
				object + "\n Aws: \n" + objectAws);

		s3Utils.put(fileName, file);
		s3UtilsAws.put(fileName, file);

		Common.waitSec(1);

		System.out.println("\nAn object was put");
		List<S3ObjectSummary> objectsNew = s3Utils.getObjectList();
		List<S3ObjectSummary> objectsAwsNew = s3UtilsAws.getObjectList();

		String objectNew = "";
		String objectAwsNew = "";
		System.out.println("\nS3 bucket:\n");
		for (int i = 0; i < objectsNew.size(); i++) {
			objectNew = objectNew + "\n " + objectsNew.get(i).getKey();
			System.out.println(objectsNew.get(i).getKey());
		}

		System.out.println("\nAws bucket:\n");
		for (int i = 0; i < objectsAwsNew.size(); i++) {
			objectAwsNew = objectAwsNew + "\n " + objectsAwsNew.get(i).getKey();
			System.out.println(objectsAwsNew.get(i).getKey());
		}

		FileUtils.createFolder(FileUtils.getRootPath() + "\\Api_07");
		FileUtils.write(FileUtils.getRootPath() + "\\Api_07\\result2.txt",
				objectNew + "\n Aws: \n" + objectAwsNew);

		Assert.assertEquals(objects.size() + 1, objectsNew.size());
		Assert.assertEquals(objectsAws.size() + 1, objectsAwsNew.size());

	}
}
