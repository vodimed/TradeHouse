package com.expertek.tradehouse.dictionaries.entity;

import androidx.annotation.NonNull;

import java.io.Serializable;

//TODO ROOM: @Entity(tableName = "TH_barcodes", primaryKeys = {"BC"})
public class barcode implements Serializable {
    public @NonNull int GoodsID; // идентификатор товара из TH
    public @NonNull String BC = ""; // бар-код из TH
    public double PriceBC; // цена бар-кода
    public String UnitBC; // единица измерения бар-кода
    public double UnitRate; // коэф. пересчета ед. изм. к основной ед. изм.
}
