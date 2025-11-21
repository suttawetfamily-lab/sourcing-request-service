package com.pantavanij.sourcingreq.services.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateTimeTHUtil {
    public static final String DATE_FORMAT ="dd/MM/yyyy" ;
    public static final LocalDate FIRST_JANUARY_2020 = LocalDate.of(2020, 1, 1);

    public static final String[] MONTH_THAI = {"มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};

    public static String DateThaiFormat(Timestamp timeStamp) {
        if (timeStamp == null) {
            return null;
        }
        LocalDate localDate = timeStamp.toLocalDateTime().toLocalDate();
        return localDate.getDayOfMonth()+" "+MONTH_THAI[localDate.getMonthValue()-1]+" "+(localDate.getYear()+543);
    }

    public static LocalDate lastDOM( String strDate) {

        LocalDate convertedDate = LocalDate.parse(strDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
        return lastDOM(convertedDate) ;
    }

    public static LocalDate lastDOM( LocalDate convertedDate) {

        LocalDate output  ;
        output = convertedDate.withDayOfMonth(
                convertedDate.getMonth().length(convertedDate.isLeapYear()));
        return output ;
    }

    public static LocalDate firstDOM( String strDate) {

        LocalDate convertedDate = LocalDate.parse(strDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
        return firstDOM(convertedDate) ;
    }

    public static LocalDate firstDOM( LocalDate convertedDate) {

        LocalDate output  ;
        output = LocalDate.of(convertedDate.getYear(), convertedDate.getMonthValue(), 1);
        return output ;

    }

    public static int diffDate( String strDate1  ,String strDate2) {

        LocalDate convertedDate1 = LocalDate.parse(strDate1, DateTimeFormatter.ofPattern(DATE_FORMAT));
        LocalDate convertedDate2 = LocalDate.parse(strDate2, DateTimeFormatter.ofPattern(DATE_FORMAT));
        return diffDate(convertedDate1,convertedDate2) ;
    }

    public static int diffDate( LocalDate d1  ,LocalDate d2) {

        int diff =0;
        diff =(int) ( ChronoUnit.DAYS.between(d1, d2) + 1);

        return diff ;

    }

    public static int diffMonthOfDate( LocalDate d1  ,LocalDate d2) {

        int diff =0;
        diff =(int) ( ChronoUnit.MONTHS.between(d1, d2));

        return diff ;

    }

    public static DateTimeFormatter getFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return formatter ;

    }

    public static Timestamp localDateToTimeStamp(LocalDate d1) {
        return new Timestamp(Date.from(d1.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());

    }

    public static Timestamp localDateToTimeStamp(String strDate1) {
        LocalDate d1 = LocalDate.parse(strDate1, DateTimeFormatter.ofPattern(DATE_FORMAT));
        return new Timestamp(Date.from(d1.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());

    }

    public static Boolean equals( LocalDate d1  ,LocalDate d2) {

        return  d1.getYear() == d2.getYear() && d1.getMonth() == d2.getMonth() && d1.getDayOfMonth() == d2.getDayOfMonth();

    }

    public static Boolean equals( String strDate1  ,String strDate2) {
        LocalDate convertedDate1 = LocalDate.parse(strDate1, DateTimeFormatter.ofPattern(DATE_FORMAT));
        LocalDate convertedDate2 = LocalDate.parse(strDate2, DateTimeFormatter.ofPattern(DATE_FORMAT));
        return equals(convertedDate1,convertedDate2);

    }
    public static Boolean isBetweenRangeClosed(LocalDate startDate  , LocalDate endDate ,LocalDate date) {
        return  date.isAfter(startDate.minusDays(1)) && date.isBefore(endDate.plusDays(1));
    }

    public static Boolean isBetweenRange(LocalDate startDate  , LocalDate endDate ,LocalDate date) {
        return  date.isAfter(startDate) && date.isBefore(endDate);
    }

    public static Boolean isBetweenRangeClosed( String startDateStr  , String endDateStr ,String dateStr) {
        LocalDate startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ofPattern(DATE_FORMAT));
        LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ofPattern(DATE_FORMAT));
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT));
        return  isBetweenRangeClosed(startDate,endDate,date);
    }

    public static LocalDate toLocalDate(Timestamp d1) {
        return d1.toLocalDateTime().toLocalDate();

    }

    public static LocalDate toLocalDate(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();

    }

    public static Boolean isEOM(LocalDate date) {
        return equals(date, lastDOM(date));
    }

    public static Boolean isOverlaps(LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {
        return  (startDate1.isBefore(endDate2) || equals(startDate1, endDate2))
                && (startDate2.isBefore(endDate1) || equals(startDate2, endDate1));
    }

    public static Boolean isOverlaps(Timestamp startDate1, Timestamp endDate1, Timestamp startDate2, Timestamp endDate2) {
        return  isOverlaps(toLocalDate(startDate1), toLocalDate(endDate1), toLocalDate(startDate2), toLocalDate(endDate2))  ;
    }

    public static Boolean isOverlaps(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        return  isOverlaps(toLocalDate(startDate1), toLocalDate(endDate1), toLocalDate(startDate2), toLocalDate(endDate2))  ;
    }

    public static String DateThaiFormatForReport(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.getDayOfMonth()+"-"+MONTH_THAI[localDate.getMonthValue()-1]+"-"+(localDate.getYear()+543);
    }
}
