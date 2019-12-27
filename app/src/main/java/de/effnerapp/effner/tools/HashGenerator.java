package de.effnerapp.effner.tools;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashGenerator {
    private String algorithm;
    private Charset charset;
    public HashGenerator(String algorithm, Charset charset) {
        this.algorithm = algorithm;
        this.charset = charset;
    }


    public String generate(String input) throws NoSuchAlgorithmException {
        return getHexString(getHashedBytes(input));
    }

    private byte[] getHashedBytes(String input) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        return messageDigest.digest(input.getBytes(charset));
    }

    private String getHexString(byte[] hash) {
        BigInteger hashI = new BigInteger(1 , hash);
        StringBuilder hexString = new StringBuilder(hashI.toString(16));

        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
}
