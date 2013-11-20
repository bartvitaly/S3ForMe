package test.java;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import me.s3for.common.Common;
import me.s3for.common.S3Utils;
import me.s3for.common.StringUtils;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

public class Api_04_Put_ObjectAcl_Test extends TestInitialize {

	S3Utils s3Utils, s3UtilsAws;
	String fileName = "file.txt";

	File file, fileAws;

	/**
	 * @desc The code to be run before each test
	 */

	@BeforeTest(groups = { "api" })
	public void init() {
		// initiate S3 and AWS
		s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		s3UtilsAws = new S3Utils();

		// set bucket to work with
		s3Utils.setBacket(bucketName);
		s3UtilsAws.setBacket(bucketNameAws);

		// put a file in a basket
		file = S3Utils.createSampleFile();
	}

	@AfterTest(groups = { "api" })
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
	public void bucketGetPutTest() throws Exception {

		PutObjectResult putObjectResult = s3Utils.put(fileName, file,
				CannedAccessControlList.PublicRead);
		PutObjectResult putObjectResultAws = s3UtilsAws.put(fileName, file,
				CannedAccessControlList.PublicRead);

		putObjectResult = s3Utils.put(fileName, file,
				CannedAccessControlList.Private);
		putObjectResultAws = s3UtilsAws.put(fileName, file,
				CannedAccessControlList.Private);

		AccessControlList aclS3 = s3Utils.getObjectAcl(fileName);
		AccessControlList aclS3Aws = s3UtilsAws.getObjectAcl(fileName);

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
