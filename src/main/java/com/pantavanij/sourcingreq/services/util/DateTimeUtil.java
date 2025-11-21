package com.pantavanij.sourcingreq.services.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.sql.Timestamp;
import java.text.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtil {

    public static Date addDate(Date currentDate , int addNumDate){
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE , addNumDate);
        return  c.getTime();
    }

    public static Date subtractHour(Date currentDate, int hourNumber) {
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.HOUR_OF_DAY, -hourNumber);
        return  c.getTime();
    }

    public static Timestamp getTimestampUTC() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Timestamp.valueOf(f.format(new Date()));
    }

    public static Timestamp getTimestamp() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
        return Timestamp.valueOf(f.format(new Date()));
    }

    public static String getTimestamp(String pattern) {
        SimpleDateFormat f = new SimpleDateFormat(pattern, Locale.ENGLISH);
        return f.format(new Date());
    }

    public static Timestamp getTimestampByTimeZone(Timestamp timestamp, String timeZoneId) {
        String str = timestamp.toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime dateTime = LocalDateTime.parse(StringUtils.rightPad(str,23,"0"), formatter);

        ZonedDateTime zdt = ZonedDateTime.of(dateTime, ZoneId.of(timeZoneId));
        return Timestamp.valueOf(zdt.toLocalDateTime().plusSeconds(zdt.getOffset().getTotalSeconds()));
    }

    public static Timestamp getTimestampUTC(Date date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Timestamp.valueOf(f.format(date));
    }

    public static String convertThaiMonthShortnameToNumber(String thaiMonthShortname) {
        String month;
        switch (thaiMonthShortname) {
            case "ม.ค.":
                month = "01";
                break;
            case "ก.พ.":
                month = "02";
                break;
            case "มี.ค.":
                month = "03";
                break;
            case "เม.ย.":
                month = "04";
                break;
            case "พ.ค.":
                month = "05";
                break;
            case "มิ.ย.":
                month = "06";
                break;
            case "ก.ค.":
                month = "07";
                break;
            case "ส.ค.":
                month = "08";
                break;
            case "ก.ย.":
                month = "09";
                break;
            case "ต.ค.":
                month = "10";
                break;
            case "พ.ย.":
                month = "11";
                break;
            case "ธ.ค.":
                month = "12";
                break;
            default:
                month = null;
                break;
        }

        return month;
    }

    public static Timestamp convertTimestampByUserTimeZone(Timestamp oriTimestamp, String timeZone) {
        if (oriTimestamp == null || timeZone == null) return oriTimestamp;
        Timestamp targetTimestamp =
                timeZone != null ? DateTimeUtil.getTimestampByTimeZone(oriTimestamp, timeZone) : oriTimestamp;
        return targetTimestamp;
    }

    public static String convertTimestampByUserTimeZoneStr(Timestamp oriTimestamp, String timeZone) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        if (oriTimestamp == null || timeZone == null) return "";
        Timestamp targetTimestamp = timeZone != null ? DateTimeUtil.getTimestampByTimeZone(oriTimestamp, timeZone) : oriTimestamp;
        return f.format(targetTimestamp);
    }

    public static String convertTimeStampToDateStr(Date date) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            return f.format(date);
        } catch (DateTimeParseException e) {
            return "";
        }
    }

    public static String convertTimeStampToDateTimeStr(Date date) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
            return f.format(date);
        } catch (DateTimeParseException e) {
            return "";
        }
    }

    public static String convertTimeStampToDateTimeMinStr(Date date) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
            return f.format(date);
        } catch (DateTimeParseException e) {
            return "";
        }
    }

    public static String convertTimeStampToDateStrWithLocale(Date date, Locale locale) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", locale);
            return f.format(date);
        } catch (DateTimeParseException e) {
            return "";
        }
    }

    public static boolean isValid(Date date) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            f.format(date);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isValidDateString(String date, String pattern) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static String getMonth(Timestamp date, String timeZone) {
        if (date == null) return null;
        try {
            String dateTime = convertTimestampByUserTimeZoneStr(date, timeZone);
            String[] txtDate = dateTime.split("/");
            return txtDate[1];
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String getYear(Timestamp date, String timeZone) {
        if (date == null) return null;
        try {
           String dateTime = convertTimestampByUserTimeZoneStr(date, timeZone);
           String[] txtDate = dateTime.split("/");
           return txtDate[2];
        } catch (DateTimeParseException e) {
           return null;
        }
    }

    public static Date getExcelDate(Cell cell) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date = null;
        switch (cell.getCellTypeEnum()) {
            case STRING:
                if (DateTimeUtil.isValidDateString(cell.getStringCellValue().trim(), "dd/MM/yyyy")) {
                    date = formatter.parse(cell.getStringCellValue());
                }
                break;

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    date = cell.getDateCellValue();
                }
                break;
        }
        return date;
    }

    public static String convertDateToString(Date date, String pattern) {
        if (date == null) return null;
        SimpleDateFormat formatDate = new SimpleDateFormat(pattern, Locale.ENGLISH);
        formatDate.setTimeZone(TimeZone.getTimeZone("UTC")); // หรือปรับ timezone ที่ต้องการ
        return formatDate.format(date);
    }


    public static Date getCurrentDateWithTimeZone(String timezone) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        return calendar.getTime();
    }
}
