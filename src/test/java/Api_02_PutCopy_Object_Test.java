package test.java;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import me.s3for.common.Common;
import me.s3for.common.S3Utils;
import me.s3for.common.StringUtils;

import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

public class Api_02_PutCopy_Object_Test extends TestInitialize {

	S3Utils s3Utils, s3UtilsAws;
	String fileName = "file.txt";
	String fileNameNew = "file_new.txt";
	File file, fileAws;
	PutObjectResult putObjectResult, putObjectResultAws;

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
		putObjectResult = s3Utils.put(fileName, file);
		putObjectResultAws = s3UtilsAws.put(fileName, file);

	}

	/**
	 * @desc Check ability to write to a bucket
	 * 
	 * @throws IOException
	 */

	@Test(groups = { "api" })
	public void bucketWriteTest() throws Exception {

		// Copy objects
		CopyObjectResult copyObjectResult = s3Utils.copyObject(fileName,
				fileNameNew);
		CopyObjectResult copyObjectResultAws = s3UtilsAws.copyObject(fileName,
				fileNameNew);

		Assert.assertEquals(copyObjectResult.getETag(),
				putObjectResult.getETag());
		Assert.assertEquals(copyObjectResultAws.getETag(),
				putObjectResultAws.getETag());

		// Get S3 objects
		S3Object s3Object = s3Utils.get(fileName);
		S3Object s3ObjectNew = s3Utils.get(fileNameNew);
		S3Object s3ObjectAws = s3UtilsAws.get(fileNameNew);

		ObjectMetadata s3ObjectMetadata = s3Object.getObjectMetadata();
		ObjectMetadata s3ObjectMetadataNew = s3ObjectNew.getObjectMetadata();
		ObjectMetadata s3ObjectMetadataAws = s3ObjectAws.getObjectMetadata();

		// Get file content
		String content = StringUtils.inputStreamToString(s3Object
				.getObjectContent());
		String contentNew = StringUtils.inputStreamToString(s3ObjectNew
				.getObjectContent());
		String contentAws = StringUtils.inputStreamToString(s3ObjectAws
				.getObjectContent());

		Assert.assertEquals(content, contentAws);
		Assert.assertEquals(content, contentNew);

		Map<String, Object> map = Common.compareMaps(
				s3ObjectMetadata.getRawMetadata(),
				s3ObjectMetadataNew.getRawMetadata(), avoidKeys);

		System.out.println("\nMetadata: S3 vs S3New");
		Common.printMap(map);

		Assert.assertTrue(map.size() == 0,
				"Objects' metadata are not the same. S3 vs S3New");

		map = Common.compareMaps(s3ObjectMetadata.getRawMetadata(),
				s3ObjectMetadataAws.getRawMetadata(), avoidKeys);

		System.out.println("Metadata: S3 vs AWS");
		Common.printMap(map);

		Assert.assertTrue(map.size() == 0,
				"Objects' metadata are not the same. S3 vs AWS");

	}
}
