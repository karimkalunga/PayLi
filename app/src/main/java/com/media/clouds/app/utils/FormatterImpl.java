package com.media.clouds.app.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Formatter.class
 * This class coverts input from one format to another.
 * For instance; number to currency, date, etc.
 */
public class FormatterImpl {
    private static FormatterImpl instance = null;
    private FormatterImpl() {}

    /**
     * Singleton.
     * @return class instance.
     */
    public static synchronized FormatterImpl getInstance() {
        if (instance == null) {
            instance = new FormatterImpl();
        }
        return instance;
    }

    /**
     * Add commas to thousands from integer number input.
     * @param number integer.
     * @return String.
     */
    public String formatNumberAddCommas(int number) {
        DecimalFormat fm = new DecimalFormat("#,###,###");
        return fm.format(number);
    }

    /**
     * Add commas to thousands from double number input.
     * @param number double.
     * @return String.
     */
    public String formatNumberAddCommas(double number) {
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        return formatter.format(number);
    }

    /**
     * Formats date in timestamp format i.e. 2020-09-13 20:08:20.
     * @param date as input you want to format.
     * @param to formatting string.
     * @return formatted date as string or null case of error.
     */
    public String formatTimestamp(String date, String to) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date dt = sdf.parse(date);
            sdf = new SimpleDateFormat(to, Locale.getDefault());
            assert dt != null;
            return sdf.format(dt);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Formats time to seconds, minutes, hours and days.
     * @param timeMs time in milliseconds.
     * @return formatted time.
     */
    public String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds =  timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
}
