package test.java.api;

import java.io.File;
import java.io.IOException;

import me.s3for.common.S3Utils;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import test.java.TestInitialize;

import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;

public class Api_05_Get_BucketAcl_Test extends TestInitialize {

	final static String PERMISSION_FULL_CONTROL = "FULL_CONTROL";
	final static String PERMISSION_READ = "READ";
	final static String GRANTEE_DEFAULT = "com.amazonaws.services.s3.model.CanonicalGrantee@";
	final static String GRANTEE_ALL_USERS = "GroupGrantee [http://acs.amazonaws.com/groups/global/AllUsers]";
	final static String ERROR_MESSAGE = "Permissions were set incorrectly";

	S3Utils s3Utils, s3UtilsAws;
	String fileName = "file.txt";
	File file, fileAws;
	AccessControlList aclS3Default, aclS3AwsDefault;

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

		aclS3Default = s3Utils.getBucketAcl();
		aclS3AwsDefault = s3UtilsAws.getBucketAcl();

		// s3Utils.createUser("userName");

	}

	@AfterTest(groups = { "api" })
	public void tear() {
		s3Utils.setBucketAcl(aclS3Default);
		s3UtilsAws.setBucketAcl(aclS3AwsDefault);
	}

	/**
	 * @desc Check ability to write to a bucket
	 * 
	 * @throws IOException
	 */

	@Test(groups = { "api" })
	public void bucketPut_BucketAcl_Private_Test() throws Exception {

		s3Utils.setBucketAcl(CannedAccessControlList.Private);
		s3UtilsAws.setBucketAcl(CannedAccessControlList.Private);

		AccessControlList aclS3 = s3Utils.getBucketAcl();
		AccessControlList aclS3Aws = s3UtilsAws.getBucketAcl();

		Assert.assertTrue(S3Utils.checkGrantee(aclS3, GRANTEE_DEFAULT,
				PERMISSION_FULL_CONTROL), ERROR_MESSAGE
				+ S3Utils.granteeErrorMessage);
		Assert.assertTrue(S3Utils.checkGrantee(aclS3Aws, GRANTEE_DEFAULT,
				PERMISSION_FULL_CONTROL), ERROR_MESSAGE
				+ S3Utils.granteeErrorMessage);

		Assert.assertFalse(
				S3Utils.checkGrantee(aclS3, GRANTEE_ALL_USERS, PERMISSION_READ),
				ERROR_MESSAGE + S3Utils.granteeErrorMessage);
		Assert.assertFalse(S3Utils.checkGrantee(aclS3Aws, GRANTEE_ALL_USERS,
				PERMISSION_READ), ERROR_MESSAGE + S3Utils.granteeErrorMessage);

	}

	@Test(groups = { "api" })
	public void bucketPut_BucketAcl_Public_Test() throws Exception {

		s3Utils.setBucketAcl(CannedAccessControlList.PublicRead);
		s3UtilsAws.setBucketAcl(CannedAccessControlList.PublicRead);

		AccessControlList aclS3 = s3Utils.getBucketAcl();
		AccessControlList aclS3Aws = s3UtilsAws.getBucketAcl();

		Assert.assertTrue(S3Utils.checkGrantee(aclS3, GRANTEE_DEFAULT,
				PERMISSION_FULL_CONTROL), ERROR_MESSAGE
				+ S3Utils.granteeErrorMessage);
		Assert.assertTrue(S3Utils.checkGrantee(aclS3Aws, GRANTEE_DEFAULT,
				PERMISSION_FULL_CONTROL), ERROR_MESSAGE
				+ S3Utils.granteeErrorMessage);

		// Assert.assertTrue(
		// S3Utils.checkGrantee(aclS3, GRANTEE_ALL_USERS, PERMISSION_READ),
		// ERROR_MESSAGE + S3Utils.granteeErrorMessage);
		Assert.assertTrue(S3Utils.checkGrantee(aclS3Aws, GRANTEE_ALL_USERS,
				PERMISSION_READ), ERROR_MESSAGE + S3Utils.granteeErrorMessage);

	}

}
