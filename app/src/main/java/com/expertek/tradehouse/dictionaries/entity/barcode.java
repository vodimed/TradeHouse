package com.expertek.tradehouse.dictionaries.entity;

import androidx.annotation.NonNull;

import com.common.extensions.database.Entity;
import com.common.extensions.database.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "TH_barcodes")
public class barcode implements Serializable {
    public @NonNull int GoodsID; // идентификатор товара из TH
    @PrimaryKey
    public @NonNull String BC = ""; // бар-код из TH
    public double PriceBC; // цена бар-кода
    public String UnitBC; // единица измерения бар-кода
    public double UnitRate; // коэф. пересчета ед. изм. к основной ед. изм.
}
