package com.expertek.tradehouse.dictionaries;

import com.expertek.tradehouse.database.DataMigration;

public interface DbDictionaries {
    TBarcodes barcodes();
    TClients clients();
    TGoods goods();
    TObjects objects();
    TUsers users();

    DataMigration[] migrations = {
            DbDictionaries_v1.upgradeFrom_0,
            DbDictionaries_v1.downgradeTo_0
    };
}
