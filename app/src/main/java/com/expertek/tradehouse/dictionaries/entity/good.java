package com.expertek.tradehouse.dictionaries.entity;

import androidx.annotation.NonNull;

import com.common.extensions.database.Entity;
import com.common.extensions.database.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "TH_goods")
public class good implements Serializable {
    @PrimaryKey
    public @NonNull int GoodsID; // идентификатор товара из TH
    public String Name; // название
    public String UnitBase; // основная ед. изм.
    public double PriceBase; // не используется
    public double VAT; // не используется
    public String Country; // страна
    public String Struct; // не используется
    public double FactQnty; // остатки фактическое кол-во
    public double FreeQnty; // остатки свободное кол-во
}
