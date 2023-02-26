package com.github.phonenumbermanager;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.extern.slf4j.Slf4j;

/**
 * 密码加密测试类
 *
 * @author 廿二月的天
 */
@Slf4j
public class PasswordEncryptTest {

    @Test
    public void testPass() {
        String pass = "admin888";
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        String hashPass = encode.encode(pass);
        log.debug(hashPass);
    }
}
