package com.pantavanij.sourcingreq.services.util;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommonUtils {
    public static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return (OS.contains("win"));

    }

    public static boolean isServer() {
        return OS.contains("server");
    }

    public static boolean isMac() {
        return (OS.contains("mac"));

    }

    public static double round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_EVEN);
        return bd.doubleValue();
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    public static String decodeValue(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    public static <T> OptionDto mapOptionalToOptionDto(Optional<T> optional, Function<T, OptionDto> mapper) {
        return optional.map(mapper).orElse(null);
    }

    public static String generateNamePattern(String fullName) {
        String[] nameParts = fullName.split(" ");
        String firstName = nameParts[0].toLowerCase();

        if (nameParts.length > 1) {
            String lastName = nameParts[nameParts.length - 1].toLowerCase();

            if (lastName.length() > 2) {
                String lastNameShort = lastName.substring(0, 3);
                return firstName + "." + lastNameShort;
            } else {
                return firstName;
            }

        } else {
            return firstName;
        }
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

}
