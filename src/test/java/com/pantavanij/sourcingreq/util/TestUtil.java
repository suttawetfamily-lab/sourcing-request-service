package com.pantavanij.sourcingreq.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestUtil {
    public static Timestamp getMockTimestamp() {
        String inDate = "01/10/2020 00:00:00";
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date;
        try {
            date = df.parse(inDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        long time = date.getTime();
        return new Timestamp(time);
    }

    public static String getMockTimeZone() {
        return "Asia/Bangkok";
    }
}
