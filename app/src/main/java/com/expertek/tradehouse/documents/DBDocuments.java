package com.expertek.tradehouse.documents;

import androidx.room.migration.Migration;

import com.expertek.tradehouse.database.BaseFace;

public interface DBDocuments extends BaseFace {
    TDocuments tDocuments();
    TLines tLines();
    TMarklines tMarklines();

    Migration[] migrations = {
            DBDocuments_v1.upgradeFrom_0,
            DBDocuments_v1.downgradeTo_0
    };
}
