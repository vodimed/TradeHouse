package com.expertek.tradehouse.dictionaries;

import com.expertek.tradehouse.database.Baseface;

public interface DbDictionaries extends Baseface {
    TBarcodes tBarcodes();
    TClients tClients();
    TGoods tGoods();
    TObjects tObjects();
    TUsers tUsers();
}
