package com.expertek.tradehouse.documents;

import com.expertek.tradehouse.database.Baseface;

public interface DBDocuments extends Baseface {
    TDocuments tDocuments();
    TLines tLines();
    TMarklines tMarklines();
}
