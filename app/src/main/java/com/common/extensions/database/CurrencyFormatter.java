package com.common.extensions.database;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class CurrencyFormatter {
    private static final NumberFormat currency = new DecimalFormat("#0.00");

    public static String format(double value) {
        return currency.format(value);
    }

    public static double parse(String value) throws ParseException {
        return currency.parse(value).doubleValue();
    }
}
