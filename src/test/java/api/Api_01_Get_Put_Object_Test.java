package test.java.api;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import me.s3for.common.Common;
import me.s3for.common.S3Utils;
import me.s3for.common.StringUtils;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import test.java.TestInitialize;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

public class Api_01_Get_Put_Object_Test extends TestInitialize {

	S3Utils s3Utils, s3UtilsAws;
	String fileName = "file.txt";
	// String fileName = "test_5mb.file";
	// String filePath = FileUtils.getRootPath() + "\\static\\" + fileName;

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
		// file = new File(filePath);
		PutObjectResult putObjectResult = s3Utils.putAsInputStream(fileName,
				file);
		PutObjectResult putObjectResultAws = s3UtilsAws.putAsInputStream(
				fileName, file);

		String md5 = putObjectResult.getContentMd5();
		String md5Aws = putObjectResultAws.getContentMd5();

		Assert.assertEquals(md5, md5Aws);

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

	@Test(groups = { "api" }, testName = "bucketGetPutTest")
	public void bucketGetPutTest() throws Exception {

		// Get S3 objects
		S3Object s3Object = s3Utils.get(fileName);
		S3Object s3ObjectAws = s3UtilsAws.get(fileName);

		ObjectMetadata s3ObjectMetadata = s3Object.getObjectMetadata();
		ObjectMetadata s3ObjectMetadataAws = s3ObjectAws.getObjectMetadata();

		Assert.assertEquals(file.length(), s3ObjectMetadata.getContentLength());
		Assert.assertEquals(file.length(),
				s3ObjectMetadataAws.getContentLength());

		// Get file content
		String content = StringUtils.inputStreamToString(s3Object
				.getObjectContent());
		String contentAws = StringUtils.inputStreamToString(s3ObjectAws
				.getObjectContent());

		Assert.assertEquals(content, contentAws);

		Map<String, Object> map = Common.compareMaps(
				s3ObjectMetadata.getRawMetadata(),
				s3ObjectMetadataAws.getRawMetadata(), avoidKeys);

		System.out.println("Metadata: S3 vs AWS");
		Common.printMap(map);

		Assert.assertTrue(map.size() == 0, "Objects' metadata are not the same");

	}
}
