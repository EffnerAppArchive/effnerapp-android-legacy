package de.effnerapp.effner.data.utils;

public class DigitsParser {
    private String[] DIGITS = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};

    public DigitsParser() {

    }

    public String parse(int digit) {
        return DIGITS[digit];
    }
}
