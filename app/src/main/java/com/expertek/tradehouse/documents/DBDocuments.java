package com.expertek.tradehouse.documents;

import com.expertek.tradehouse.database.DataMigration;

public interface DBDocuments {
    TDocuments documents();
    TLines lines();
    TMarklines marklines();

    DataMigration[] migrations = {
            DBDocuments_v1.upgradeFrom_0,
            DBDocuments_v1.downgradeTo_0
    };
}
