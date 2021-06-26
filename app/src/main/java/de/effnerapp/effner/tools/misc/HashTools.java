/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 18:21.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.tools.misc;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashTools {
    private final String algorithm;
    private final Charset charset;

    public HashTools(String algorithm, Charset charset) {
        this.algorithm = algorithm;
        this.charset = charset;
    }

    public static String sha512(String s) {
        return new HashTools("SHA-512", StandardCharsets.UTF_8).generate(s);
    }

    public String generate(String input) {
        return getHexString(getHashedBytes(input));
    }

    private byte[] getHashedBytes(String input) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert messageDigest != null;
        return messageDigest.digest(input.getBytes(charset));
    }

    private String getHexString(byte[] hash) {
        BigInteger hashI = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(hashI.toString(16));

        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
}
