package test.java.api;

import java.io.File;
import java.io.IOException;

import me.s3for.common.S3Utils;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import test.java.TestInitialize;

import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectResult;

public class Api_04_GetPut_ObjectAcl_Test extends TestInitialize {

	S3Utils s3Utils, s3UtilsAws;
	String fileName = "file.txt";
	File file, fileAws;

	final static String PERMISSION_FULL_CONTROL = "FULL_CONTROL";
	final static String PERMISSION_READ = "READ";

	final static String GRANTEE_DEFAULT = "com.amazonaws.services.s3.model.CanonicalGrantee@";
	final static String GRANTEE_ALL_USERS = "GroupGrantee [http://acs.amazonaws.com/groups/global/AllUsers]";

	final static String ERROR_MESSAGE = "Permissions were set incorrectly";

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

		// s3Utils.createUser("userName");

		// create a file
		file = S3Utils.createSampleFile();
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
	public void bucketPut_ObjectAcl_Private_Test() throws Exception {

		PutObjectResult putObjectResult = s3Utils.put(fileName, file,
				CannedAccessControlList.Private);
		PutObjectResult putObjectResultAws = s3UtilsAws.put(fileName, file,
				CannedAccessControlList.Private);

		String md5 = putObjectResult.getContentMd5();
		String md5Aws = putObjectResultAws.getContentMd5();

		Assert.assertEquals(md5, md5Aws);

		AccessControlList aclS3 = s3Utils.getObjectAcl(fileName);
		AccessControlList aclS3Aws = s3UtilsAws.getObjectAcl(fileName);

		Assert.assertTrue(S3Utils.checkGrantee(aclS3, GRANTEE_DEFAULT,
				PERMISSION_FULL_CONTROL), ERROR_MESSAGE);
		Assert.assertTrue(S3Utils.checkGrantee(aclS3Aws, GRANTEE_DEFAULT,
				PERMISSION_FULL_CONTROL), ERROR_MESSAGE);

		Assert.assertFalse(
				S3Utils.checkGrantee(aclS3, GRANTEE_ALL_USERS, PERMISSION_READ),
				ERROR_MESSAGE);
		Assert.assertFalse(S3Utils.checkGrantee(aclS3Aws, GRANTEE_ALL_USERS,
				PERMISSION_READ), ERROR_MESSAGE);

	}

	@Test(groups = { "api" })
	public void bucketPut_ObjectAcl_Public_Test() throws Exception {

		s3Utils.put(fileName, file, CannedAccessControlList.PublicRead);
		s3UtilsAws.put(fileName, file, CannedAccessControlList.PublicRead);

		AccessControlList aclS3 = s3Utils.getObjectAcl(fileName);
		AccessControlList aclS3Aws = s3UtilsAws.getObjectAcl(fileName);

		// Check not authrized user is able to get a public file
		s3Utils = new S3Utils(false, serverS3);
		s3Utils.bucketName = bucketName;

		s3UtilsAws = new S3Utils(false, "");
		s3UtilsAws.bucketName = bucketNameAws;

		long length = s3Utils.get(fileName).getObjectMetadata()
				.getContentLength();
		long lengthAws = s3UtilsAws.get(fileName).getObjectMetadata()
				.getContentLength();

		Assert.assertEquals(length, file.length());
		Assert.assertEquals(lengthAws, file.length());

		Assert.assertTrue(S3Utils.checkGrantee(aclS3, GRANTEE_DEFAULT,
				PERMISSION_FULL_CONTROL), ERROR_MESSAGE);
		Assert.assertTrue(S3Utils.checkGrantee(aclS3Aws, GRANTEE_DEFAULT,
				PERMISSION_FULL_CONTROL), ERROR_MESSAGE);

		Assert.assertTrue(
				S3Utils.checkGrantee(aclS3, GRANTEE_ALL_USERS, PERMISSION_READ),
				ERROR_MESSAGE);
		Assert.assertTrue(S3Utils.checkGrantee(aclS3Aws, GRANTEE_ALL_USERS,
				PERMISSION_READ), ERROR_MESSAGE);

	}

}
