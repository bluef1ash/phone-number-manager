package la.isx.phone_number_manager.test.service;

import org.junit.Test;

import la.isx.phone_number_manager.utils.CommonUtil;

public class EncryptTest {

	@Test
	public void testMd5() throws Exception {
		String data = "12345";
		String encryptMD5 = CommonUtil.MD5(data);
		System.out.println(encryptMD5);
	}
}
