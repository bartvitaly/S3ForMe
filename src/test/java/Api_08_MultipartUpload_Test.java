package test.java;

import java.io.IOException;
import java.util.Map;

import me.s3for.common.Common;
import me.s3for.common.FileUtils;
import me.s3for.common.S3Utils;

import org.apache.log4j.Level;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

public class Api_08_MultipartUpload_Test extends TestInitialize {

	S3Utils s3Utils, s3UtilsAws;
	String fileName = "test_5mb.file";
	String filePath = FileUtils.getRootPath() + "\\static\\" + fileName;
	int partSizeMb = 5;

	/**
	 * @desc The code to be run before each test
	 */

	@BeforeGroups(groups = { "api" })
	public void before() {
		logger.setLevel(Level.ERROR);

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

		// Get S3 objects
		S3Object s3Object = s3Utils.get(fileName);
		S3Object s3ObjectAws = s3UtilsAws.get(fileName);

		ObjectMetadata s3ObjectMetadata = s3Object.getObjectMetadata();
		ObjectMetadata s3ObjectMetadataAws = s3ObjectAws.getObjectMetadata();

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
