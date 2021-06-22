/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 22.06.21, 19:43.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.tools.misc;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassUtils {
    public static boolean validateClass(String sClass, String userClass) {
        String regex = "\\d\\d?\\w\\d?";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sClass);

        boolean result = false;

        if (matcher.find()) {
            result = Objects.equals(matcher.group(0), userClass);
        }

        if(!result) {
            result = validateAdvancedClass(sClass, userClass);
        }
        return result;
    }

    public static boolean isAdvancedClass(String userClass) {
        if(userClass == null) return false;
        String regex = "(11|12)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userClass);
        return matcher.find();
    }

    public static int getFirstDigits(String userClass) {
        String regex = "^[^\\d]*(\\d+)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userClass);

        if (matcher.find()) {
            return Integer.parseInt(Objects.requireNonNull(matcher.group(0)));
        }
        return -1;
    }

    private static boolean validateAdvancedClass(String sClass, String userClass) {
        String regex = "(11Q|12Q)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userClass);

        if (matcher.find()) {
            return Objects.equals(matcher.group(0), sClass);
        }
        return false;
    }
}
