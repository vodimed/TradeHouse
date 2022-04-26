package com.expertek.tradehouse.components;

import android.util.ArraySet;

import java.io.Serializable;
import java.util.Set;

public class Marker implements Serializable {
    private static final Set<String> pref = new ArraySet<String>(new String[]{"01", "02"});
    public final String scanned;
    public final String gtin;
    public final String serial;
    public final double weight;
    private boolean valid = true;

    public Marker(String scanned) {
        this.scanned = scanned;

        switch (scanned.length()) {
            case 20:
                gtin = scanned.substring(3, 7);
                serial = scanned.substring(10, 20);
                weight = 1;
                break;
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
                if (MainSettings.BarcodePrefixes.contains(scanned.substring(0, 2))) {
                    gtin = scanned.substring(2, 7);
                    serial = null;
                    weight = ((double) Integer.parseInt(scanned.substring(7, 12))) / 1000;
                } else {
                    gtin = scanned;
                    serial = null;
                    weight = 1;
                }
        }
    }

    public boolean isValid() {
        return valid;
    }
}
