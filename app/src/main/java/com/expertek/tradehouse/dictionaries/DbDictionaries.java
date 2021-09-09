package com.expertek.tradehouse.dictionaries;

//TODO ROOM: import com.expertek.tradehouse.dictionaries.room.*;
import com.expertek.tradehouse.dictionaries.sqlite.*;

public interface DbDictionaries {
    Barcodes barcodes();
    Clients clients();
    Goods goods();
    Objects objects();
    Users users();
}
