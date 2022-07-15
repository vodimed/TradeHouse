package com.expertek.tradehouse.components;

import com.expertek.tradehouse.Application;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BarcodeMarker implements Serializable {
    private static final List<String> pref = Arrays.asList("01", "02");
    private static final List<Integer> lens2x = Arrays.asList(21, 25, 29);
    private static final StringBuffer sbuff = new StringBuffer(160);
    public final String scanned;
    public final String ident;
    public final String gtin;
    public final String serial;
    public final double weight;

    public BarcodeMarker(String scanned) {
        this.scanned = scanned;

        final int pos29 = scanned.indexOf((char) 29);
        if (pos29 >= 0) scanned = scanned.substring(0, pos29);

        if (scanned.indexOf(')', scanned.indexOf('(')) >= 0) {
            final Matcher brackets = Pattern.compile("\\(\\d{2}\\)").matcher(scanned);
            while (brackets.find()) brackets.appendReplacement(sbuff,
                    scanned.substring(brackets.start() + 1, brackets.end() - 1));
            brackets.appendTail(sbuff);
            scanned = sbuff.toString();
            sbuff.setLength(0);
        }

        final int length = scanned.length();
        String ident = null;
        boolean valid = true;

        switch (length) {
            case 20:
                gtin = scanned.substring(3, 7);
                serial = scanned.substring(10, 20);
                weight = 1;
                break;
            case 21: // Storage::getGtinByDM
            case 25: // Storage::getGtinByDM
            case 29:
                gtin = scanned.substring(0, 14);
                serial = scanned.substring(14, 21);
                weight = 1;
                break;
            case 31:
                valid &= pref.contains(scanned.substring(0, 2));
                gtin = scanned.substring(2, 16);
                valid &= scanned.startsWith("21", 16);
                serial = scanned.substring(19, 24);
                valid &= scanned.startsWith("93", 25);
                weight = 1;
                break;
            case 38:
                valid &= pref.contains(scanned.substring(0, 2));
                gtin = scanned.substring(2, 16);
                valid &= scanned.startsWith("21", 16);
                serial = scanned.substring(18, 31);
                valid &= scanned.startsWith("93", 32);
                weight = 1;
                break;
            case 42:
                valid &= pref.contains(scanned.substring(0, 2));
                gtin = scanned.substring(2, 16);
                valid &= scanned.startsWith("21", 16);
                serial = scanned.substring(19, 24);
                valid &= scanned.startsWith("93", 25);
                valid &= scanned.startsWith("3103", 32);
                weight = ((double) Integer.parseInt(scanned.substring(36, 42))) / 1000;
                break;
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
                valid &= pref.contains(scanned.substring(0, 2));
                gtin = scanned.substring(2, 16);
                valid &= scanned.startsWith("21", 16);
                serial = scanned.substring(18, 25);
                valid &= scanned.startsWith("8005", 26);
                valid &= scanned.startsWith("93", 37);
                weight = 1;
                break;
            case 78:
                valid &= pref.contains(scanned.substring(0, 2));
                gtin = scanned.substring(2, 16);
                valid &= scanned.startsWith("21", 16);
                serial = scanned.substring(18, 24);
                valid &= scanned.startsWith("91", 25);
                valid &= scanned.startsWith("92", 32);
                weight = 1;
                break;
            case 92:
                valid &= pref.contains(scanned.substring(0, 2));
                gtin = scanned.substring(2, 16);
                valid &= scanned.startsWith("21", 16);
                serial = scanned.substring(18, 38);
                valid &= scanned.startsWith("91", 39);
                valid &= scanned.startsWith("92", 46);
                weight = 1;
                break;
            case 85:
            case 129:
                valid &= pref.contains(scanned.substring(0, 2));
                gtin = scanned.substring(2, 16);
                valid &= scanned.startsWith("21", 16);
                serial = scanned.substring(18, 31);
                valid &= scanned.startsWith("91", 32);
                valid &= scanned.startsWith("92", 39);
                weight = 1;
                break;
            default:
                // Storage::GetBCInfo
                if ((length >= 12) && MainSettings.BarcodePrefixes.contains(scanned.substring(0, 2))) {
                    ident = scanned;
                    gtin = scanned.substring(2, 7);
                    serial = null;
                    weight = ((double) Integer.parseInt(scanned.substring(7, 12))) / 1000;
                }

                // Storage::getGtinByDM
                else if (length <= 14) {
                    final double rate = Application.dictionaries.db().barcodes().getRate(scanned);
                    gtin = ident = scanned;
                    serial = null;
                    weight = (rate != 0 ? rate : 1);
                }

                // Storage::GetCodeIdent, Storage::getGtinByDM
                else {
                    ident = getIdent(scanned, length);
                    gtin = getGtin(ident, ident.length());
                    serial = null;
                    weight = 1;
                }
        }

        if (!valid) {
            this.ident = null;
        } else if (ident != null) {
            this.ident = ident;
        } else {
            this.ident = getIdent(scanned, length);
        }
    }

    public boolean isWellformed() {
        return (this.ident != null);
    }

    private static final BiPredicate<String, Integer> base21 = new BiPredicate<String, Integer>() {
        @Override
        public boolean test(String scanned, Integer length) {
            return ((length >= 18) && scanned.startsWith("21", 16) &&
                    pref.contains(scanned.substring(0, 2)));
        }
    };

    public static String getGtin(String scanned, int length) {
        if ((length >= 18) && base21.test(scanned, length)) {
            return scanned.substring(2, 16);
        } else if (lens2x.contains(length)) {
            return scanned.substring(0, 14);
        }
        return scanned.substring(0, Math.min(length, 14));
    }

    public static String getIdent(String scanned, int length) {
        if (lens2x.contains(length) && !base21.test(scanned, length)) {
            return scanned.substring(0, 21);
        } else if ((length >= 25) && base21.test(scanned, length)) {
            return scanned.substring(0, 25);
        }
        return parseIdent(scanned, length);
    }

    // Storage::GetNextElement
    private static String parseIdent(String scanned, int length) {
        final List<String> elem = Arrays.asList("00", "01", "02", "21", "17", "11", "13");
        final int[] size = {27, 14, 14, 7, 6, 6, 6};
        final StringBuilder ident = new StringBuilder(5);

        for (int startat = 0; startat < length - 2;) {
            final int index = elem.indexOf(scanned.substring(startat, startat + 2));
            if (index < 0) break;

            ident.append(scanned.substring(startat, Math.min(startat + 2 + size[index], length)));
            startat += (2 + size[index]);
        }

        if (ident.length() > 0) return ident.toString();
        return scanned.substring(0, Math.min(length, 21));
    }
}
