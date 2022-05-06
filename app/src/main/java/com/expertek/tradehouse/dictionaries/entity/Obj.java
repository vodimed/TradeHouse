package com.expertek.tradehouse.dictionaries.entity;

import androidx.annotation.NonNull;

import com.common.extensions.database.Entity;

import java.io.Serializable;

@Entity(tableName = "TH_objects", primaryKeys = {"obj_code", "obj_type"})
public class Obj implements Serializable {
    public @NonNull int obj_code; // код объекта ТН
    public @NonNull String obj_type = ""; // тип объекта ТН
    public String Name; // название
}
