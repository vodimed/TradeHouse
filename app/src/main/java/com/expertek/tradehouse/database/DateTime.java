package com.expertek.tradehouse.database;

import androidx.room.TypeConverter;

public class DateTime extends java.util.Date {
    public DateTime(long date) {
        super(date);
    }

    public static class RoomConverter {
        @TypeConverter
        public long save(DateTime value) {
            return value.getTime();
        }

        @TypeConverter
        public DateTime load(long value) {
            return new DateTime(value);
        }
    }
}
