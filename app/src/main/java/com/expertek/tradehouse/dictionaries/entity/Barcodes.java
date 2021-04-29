package com.expertek.tradehouse.dictionaries.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TH_barcodes")
public class Barcodes {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "GoodsID")
    public int GoodsID; // идентификатор товара из TH

    @ColumnInfo(name = "BC")
    public String BC; // бар-код из TH

    @ColumnInfo(name = "PriceBC")
    public float PriceBC; // цена бар-кода

    @ColumnInfo(name = "UnitBC")
    public int UnitBC; // единица измерения бар-кода

    @ColumnInfo(name = "UnitRate")
    public float UnitRate; // коэф. пересчета ед. изм. к основной ед. изм.
}
