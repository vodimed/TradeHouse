package com.common.extensions.database;

public @interface Entity {
    String tableName();
    String[] primaryKeys() default {};
    Index[] indices() default {};
}
