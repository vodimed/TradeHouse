package com.expertek.tradehouse.documents;

import androidx.room.migration.Migration;

public interface DBDocuments {
    TDocuments tDocuments();
    TLines tLines();
    TMarklines tMarklines();

    Migration[] migrations = {
            DBDocuments_v1.upgradeFrom_0,
            DBDocuments_v1.downgradeTo_0
    };
}
