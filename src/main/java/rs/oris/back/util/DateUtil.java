package rs.oris.back.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil
{

    public static String  toSerbianTimeZone(LocalDateTime value)
    {

        // Define the timezone for Serbia
        ZoneId serbiaZoneId = ZoneId.of("Europe/Belgrade");

        // Define the timezone for UTC
        ZoneId utcZoneId = ZoneId.of("UTC");

        // Convert LocalDateTime to ZonedDateTime in UTC
        ZonedDateTime utcZonedDateTime = value.atZone(utcZoneId);

        // Convert ZonedDateTime to Serbia timezone
        ZonedDateTime serbiaZonedDateTime = utcZonedDateTime.withZoneSameInstant(serbiaZoneId);

        // Define the formatter to print the ZonedDateTime in a readable format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format and print the ZonedDateTime
        String formattedDateTime = serbiaZonedDateTime.format(formatter);


        return formattedDateTime;
    }

    public static String  toSerbianTimeZone(Timestamp timestamp)
    {

        if (timestamp == null) return "/";
        ZonedDateTime serbiaTime = timestamp.toInstant()
                .atZone(ZoneId.of("Europe/Belgrade"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return serbiaTime.format(formatter);
    }


    public static LocalDate toSerbianTimeZoneLocalDate(LocalDateTime value)
    {

        // Define the timezone for Serbia
        ZoneId serbiaZoneId = ZoneId.of("Europe/Belgrade");

        // Define the timezone for UTC
        ZoneId utcZoneId = ZoneId.of("UTC");

        // Convert LocalDateTime to ZonedDateTime in UTC
        ZonedDateTime utcZonedDateTime = value.atZone(utcZoneId);

        // Convert ZonedDateTime to Serbia timezone
        ZonedDateTime serbiaZonedDateTime = utcZonedDateTime.withZoneSameInstant(serbiaZoneId);

        return serbiaZonedDateTime.toLocalDate();
    }
    public static Date toSerbianTimeZone(Date date) {
        // Define the Serbian time zone
        TimeZone serbianTimeZone = TimeZone.getTimeZone("Europe/Belgrade");

        // Create a SimpleDateFormat with Serbian timezone
        SimpleDateFormat serbianFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        serbianFormat.setTimeZone(serbianTimeZone);

        // Parse the formatted date back into a Date object
        try {
            String serbianTimeString = serbianFormat.format(date);
            return serbianFormat.parse(serbianTimeString);
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
    }
    public static Calendar toSerbianTimeZone(Calendar calendar) {
        // Define the Serbian time zone
        TimeZone serbianTimeZone = TimeZone.getTimeZone("Europe/Belgrade");

        // Set the calendar to Serbian time zone
        calendar.setTimeZone(serbianTimeZone);

        return calendar;
    }
}
