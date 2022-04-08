package com.common.extensions.database;

public @interface PrimaryKey {
    boolean autoGenerate() default false;
}
