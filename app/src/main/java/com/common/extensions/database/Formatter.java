package com.common.extensions.database;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Formatter {
    public static class Date {
        private static final DateFormat template = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);

        public static String format(java.util.Date value) {
            return template.format(value);
        }

        public static java.util.Date parse(String value) throws ParseException {
            if (value == null) return null;
            return template.parse(value);
        }
    }

    public static class Number {
        private static final NumberFormat template = DecimalFormat.getNumberInstance();
        private static final char separ = DecimalFormatSymbols.getInstance().getDecimalSeparator();

        public static String format(double value) {
            template.setMaximumFractionDigits(4);
            return template.format(value);
        }

        public static double parse(String value) throws ParseException {
            return template.parse(value.replace('.', separ)).doubleValue();
        }
    }

    public static class Currency {
        private static final NumberFormat template = DecimalFormat.getCurrencyInstance();
        private static final char separ = DecimalFormatSymbols.getInstance().getDecimalSeparator();

        public static String format(double value) {
            template.setMinimumFractionDigits(0);
            return template.format(value);
        }

        public static double parse(String value) throws ParseException {
            try {
                return template.parse(value.replace('.', separ)).doubleValue();
            } catch (ParseException e) {
                return Number.parse(value);
            }
        }
    }
}
