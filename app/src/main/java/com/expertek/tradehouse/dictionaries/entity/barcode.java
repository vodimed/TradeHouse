package com.expertek.tradehouse.dictionaries.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "TH_barcodes")
public class barcode implements Serializable {
    @ColumnInfo(name = "GoodsID")
    @NonNull
    public int GoodsID; // идентификатор товара из TH

    @PrimaryKey
    @ColumnInfo(name = "BC")
    @NonNull
    public String BC = ""; // бар-код из TH

    @ColumnInfo(name = "PriceBC")
    public double PriceBC; // цена бар-кода

    @ColumnInfo(name = "UnitBC")
    public String UnitBC; // единица измерения бар-кода

    @ColumnInfo(name = "UnitRate")
    public double UnitRate; // коэф. пересчета ед. изм. к основной ед. изм.
}
