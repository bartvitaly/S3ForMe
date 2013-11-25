package test.java.api;

import java.io.IOException;
import java.util.Map;

import me.s3for.common.Common;
import me.s3for.common.FileUtils;
import me.s3for.common.S3Utils;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import test.java.TestInitialize;

import com.amazonaws.services.s3.model.ObjectMetadata;

public class Api_08_MultipartUpload_Test extends TestInitialize {

	S3Utils s3Utils, s3UtilsAws;
	String fileName = "test_5mb.file";
	String filePath = FileUtils.getRootPath() + "\\static\\" + fileName;
	int partSizeMb = 5;

	String fileNameNew = "test_5mb_new.file";
	String fileNameNewAws = "test_5mb_aws.file";
	String filePathNew = TEST_OUTPUT_FOLDER + fileNameNew;
	String filePathNewAws = TEST_OUTPUT_FOLDER + fileNameNewAws;

	/**
	 * @desc The code to be run before each test
	 */

	@BeforeMethod(groups = { "api" })
	public void before() {
		FileUtils.createFolder(TEST_OUTPUT_FOLDER);

		// initiate S3 and AWS
		s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		s3UtilsAws = new S3Utils();

		// set bucket to work with
		s3Utils.setBacket(bucketName);
		s3UtilsAws.setBacket(bucketNameAws);

		// put a multipart file into a basket
		s3Utils.multipartUpload(fileName, filePath, partSizeMb);
		s3UtilsAws.multipartUpload(fileName, filePath, partSizeMb);

	}

	/**
	 * @desc Check ability to upload object into a bucket with multiple parts
	 * 
	 * @throws IOException
	 */

	@Test(groups = { "api" })
	public void multipartUploadTest() throws Exception {

		ObjectMetadata s3ObjectMetadata = s3Utils.getMetadata(fileName,
				filePathNew);
		ObjectMetadata s3ObjectMetadataAws = s3UtilsAws.getMetadata(fileName,
				filePathNewAws);

		Assert.assertEquals(FileUtils.getLength(filePath),
				s3ObjectMetadata.getContentLength());
		Assert.assertEquals(FileUtils.getLength(filePath),
				s3ObjectMetadataAws.getContentLength());

		Map<String, Object> map = Common.compareMaps(
				s3ObjectMetadata.getRawMetadata(),
				s3ObjectMetadataAws.getRawMetadata(), avoidKeysMultipart);

		System.out.println("Metadata: S3 vs AWS");
		Common.printMap(map);

		Assert.assertTrue(map.size() == 0, "Objects' metadata are not the same");

	}
}
