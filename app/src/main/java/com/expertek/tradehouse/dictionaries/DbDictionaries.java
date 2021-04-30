package com.expertek.tradehouse.dictionaries;

import androidx.room.migration.Migration;

public interface DbDictionaries {
    TBarcodes tBarcodes();
    TClients tClients();
    TGoods tGoods();
    TObjects tObjects();
    TUsers tUsers();

    Migration[] migrations = {
            DbDictionaries_v1.upgradeFrom_0,
            DbDictionaries_v1.downgradeTo_0
    };
}
