package com.common.extensions.database;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateRoomConverter extends DateConverter {
    @TypeConverter
    @Override
    public String save(Date value) {
        return super.save(value);
    }

    @TypeConverter
    @Override
    public Date load(String value) {
        return super.load(value);
    }
}
