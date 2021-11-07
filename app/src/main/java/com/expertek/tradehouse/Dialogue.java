package com.expertek.tradehouse;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.StringRes;

import java.io.Serializable;

public class Dialogue {
    public static void Question(Context context, @StringRes int message, DialogInterface.OnClickListener listener) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.confirmation);
        dialog.setMessage(message);
        display2(dialog, listener);
    }

    public static void Duplicate(Context context, Serializable record, DialogInterface.OnClickListener listener) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.duplicate);
        dialog.setMessage(record.toString());
        display1(dialog, listener);
    }

    public static void Error(Context context, Throwable e) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.exception);
        dialog.setMessage(e.getMessage());
        display1(dialog, null);
    }

    public static void Delete(Context context, Serializable record, DialogInterface.OnClickListener listener) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.deletion);
        dialog.setMessage(record.toString());
        display2(dialog, listener);
    }

    public static String format(Context context, @StringRes int message, Object... formatArgs) {
        return context.getString(message, formatArgs);
    }

    private static void display2(AlertDialog.Builder dialog, DialogInterface.OnClickListener listener) {
        dialog.setNegativeButton(android.R.string.cancel, listener);
        display1(dialog, listener);
    }

    private static void display1(AlertDialog.Builder dialog, DialogInterface.OnClickListener listener) {
        dialog.setPositiveButton(android.R.string.ok, listener);
        dialog.create().show();
    }
}
