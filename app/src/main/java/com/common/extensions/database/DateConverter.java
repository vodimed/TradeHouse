package com.common.extensions.database;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateConverter {
    private static final String sqlDateFormat = "yyyy-MM-dd HH:mm:ss";
    @SuppressLint("SimpleDateFormat")
    private static final DateFormat template = new SimpleDateFormat(sqlDateFormat);

    public static String get(java.util.Date value) {
        if (value != null) {
            return template.format(value);
        } else {
            return null;
        }
    }

    public static java.util.Date get(String value) {
        if (value != null) try {
            return template.parse(value);
        } catch (ParseException e) {
            return null;
        } else {
            return null;
        }
    }

    public String save(java.util.Date value) {
        return get(value);
    }

    public java.util.Date load(String value) {
        return get(value);
    }
}
