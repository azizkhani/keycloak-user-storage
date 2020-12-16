package com.example.keycloakuserstore.security;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class KhabirPasswordEncoder implements PasswordEncoder {

    private MessageDigest md = null;
    static private KhabirPasswordEncoder khabirPasswordEncoder = null;
    private static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    public KhabirPasswordEncoder() throws NoSuchAlgorithmException {
        md = MessageDigest.getInstance("MD5");
    }

    public static KhabirPasswordEncoder getInstance() throws NoSuchAlgorithmException {
        if (khabirPasswordEncoder == null) {
            khabirPasswordEncoder = new KhabirPasswordEncoder();

        }
        return (khabirPasswordEncoder);
    }

    private String hashData(byte[] dataToHash) {
        return hexStringFromBytes((calculateHash(dataToHash)));
    }

    private byte[] calculateHash(byte[] dataToHash) {
        md.update(dataToHash, 0, dataToHash.length);
        return (md.digest());
    }

    private String hexStringFromBytes(byte[] b) {
        String hex = "";
        int msb;
        int lsb = 0;
        int i;
        // MSB maps to idx 0
        for (i = 0; i < b.length; i++) {
            msb = ((int) b[i] & 0x000000FF) / 16;
            lsb = ((int) b[i] & 0x000000FF) % 16;
            hex = hex + hexChars[msb] + hexChars[lsb];
        }
        return (hex);
    }

    public static void main2(String[] args) {
        try {
            KhabirPasswordEncoder md = KhabirPasswordEncoder.getInstance();
            System.out.println(md.hashData("123".getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(System.out);
        }
    }


    @Override
    public String encode(String rawPassword) {
        return hexStringFromBytes((calculateHash(rawPassword.getBytes())));
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return hexStringFromBytes((calculateHash(rawPassword.getBytes()))).equals(encodedPassword);
    }
}
