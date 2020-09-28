package com.wy.blog.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具测试类
 *
 * @author WY
 * @date 2019/12/9
 */

public class BCryptPasswordEncoderUtils {

    private static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    //加密
    public static String encoderPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    //解密,第一个参数是加密前的密码,第二个参数是加密后的密码,
    //  这个方法是匹配这两个参数加密前和加密后是不是一样的,一样返回true,不一样返回false
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }

    public static void main(String[] args) {
        String password = "0723";
        String encoderPassword = encoderPassword(password);
        System.out.println("encoderPassword = " + encoderPassword);
//        $2a$10$D1AoesLYlCJAShxitvj7seB8/pK1LwlKNcJWPaVyKSkA9VjL4Ztfm
//        $2a$10$hRzXkBeUzourZIfJAykAK.M5ROBqyAkUeJEP/Z7SG01RHurrCyNA.
        System.out.println(matchesPassword("0723", encoderPassword));
    }

}
