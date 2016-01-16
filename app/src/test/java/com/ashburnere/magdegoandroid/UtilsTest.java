package com.ashburnere.magdegoandroid;

import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class UtilsTest {

    // ISO 8601 date
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    @Test
    public void parseJsonDate() throws Exception {
        String departureString = "2016-01-12T10:05:22.525";

        //toCalendar(departureString);
        try {
            Date d = dateFormat.parse(departureString);
            d.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(4, 2 + 2);
    }

    /** Transform ISO 8601 string to Calendar. */
    public static Calendar toCalendar(final String iso8601string)
            throws ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 23) + s.substring(24);  // to get rid of the ":"
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid length", 0);
        }
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SSSZ").parse(s);
        calendar.setTime(date);
        return calendar;
    }
}