package com.example.utils;

import java.util.Random;


public class VerificationCode {

    /**
     * 生成验证码
     * @return
     */
    public static String verification(Integer length) {

        String list = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < length; i++) {
            int index = r.nextInt(list.length());
            sb.append(list.charAt(index));
        }
        return sb.toString();
    }

}