package com.example.keycloakuserstore.security;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class SSH512PasswordEncoder implements PasswordEncoder {

    private String apiUrl;

    public SSH512PasswordEncoder(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public static String get_SHA_512_SecurePasswordString(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            if (salt.length() > 0)
                md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public static byte[] get_SHA_512_SecurePassword_byte(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            if (salt.length() > 0)
                md.update(salt.getBytes(StandardCharsets.UTF_8));
            return md.digest(passwordToHash.getBytes());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String byte_to_string(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(Integer.toString((array[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static byte[] get_SHA_512_SecurePassword(String passwordToHash, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            return bytes;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void decode_sample() {
        byte[] s = Base64.getDecoder().decode("NwXYSw8YI7nb2PnE8eJxVoLzuBQ81wjOXh4=".getBytes());
        for (int i = 0; i < s.length; i++) {
            System.out.println(Byte.toUnsignedInt(s[i]));
        }
    }

    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    private static String toHex(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(Integer.toString((array[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    private static boolean verify_ssha512_password(String challenge_password, String plain_password) {
        byte[] challenge_bytes = Base64.getDecoder().decode(challenge_password.getBytes());
        byte[] digest = Arrays.copyOfRange(challenge_bytes, 0, 64);
        byte[] salt = Arrays.copyOfRange(challenge_bytes, 64, challenge_bytes.length);
//        for (int i = 0; i < salt.length; i++) {
//            salt[i]= (byte) (salt[i] & 0xff);
//        }
        return Arrays.equals(get_SHA_512_SecurePassword(plain_password, salt), digest);
    }

    private static boolean verify_sha512_password(String challenge_password, String plain_password) {
        byte[] challenge_bytes = Base64.getDecoder().decode(challenge_password.getBytes());
        byte[] digest = Arrays.copyOfRange(challenge_bytes, 0, 64);
        return Arrays.equals(get_SHA_512_SecurePassword_byte(plain_password, ""), digest);
    }

    public static boolean verifyHashRestApi(String apiUrl, String challengePassword, String plainPassword) {

        OkHttpClient client = new OkHttpClient();

        String url = null;
        try {
            url = String.format("%s/?challenge_password=%s&plain_password=%s",
                    apiUrl,
                    URLEncoder.encode(challengePassword, "UTF-8"),
                    URLEncoder.encode(plainPassword, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string().equals("yes");
        } catch (IOException e) {
            return false;
        }

    }

    public static void main(String[] args) throws IOException {
        verifyHashRestApi("http://127.0.0.1:5000", "0F2/psc+tkd8KrWmoT/LOusFk7cH6mic8nmF4v+qVmVtoxYgJXVEWEfAFfYYLRPQkwRGjgIN5FyWM9ZYdn58Tvs6lloA2SwW", "Aa123!@#");

//        decode_sample();
        //verify_sha512_password("dqJAci5XFoWLlHD6Q+DB46MY47KlfuUujNmmvB78Y2L0D/uTutTfItG0c5vMP4FU4283jLvhQo/saDalW8r0iA==", "Aa123!@#");
        verify_ssha512_password("0F2/psc+tkd8KrWmoT/LOusFk7cH6mic8nmF4v+qVmVtoxYgJXVEWEfAFfYYLRPQkwRGjgIN5FyWM9ZYdn58Tvs6lloA2SwW", "Aa123!@#");
        System.out.println(Base64.getEncoder().encodeToString(get_SHA_512_SecurePassword_byte("Aa123!@#", "")));
    }

    @Override
    public String encode(String rawPassword) {
        return "";
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return verifyHashRestApi(this.apiUrl, encodedPassword, rawPassword);
    }
}
