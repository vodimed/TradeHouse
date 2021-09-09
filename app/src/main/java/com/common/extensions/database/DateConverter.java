package com.common.extensions.database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateConverter {
    private static final String sqlDateFormat = "yyyy-MM-dd HH:mm:ss";
    private static final DateFormat date = new SimpleDateFormat(sqlDateFormat);

    public String save(java.util.Date value) {
        if (value != null) {
            return date.format(value);
        } else {
            return null;
        }
    }

    public java.util.Date load(String value) {
        if (value != null) try {
            return date.parse(value);
        } catch (ParseException e) {
            return null;
        } else {
            return null;
        }
    }
}
