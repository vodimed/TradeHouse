package com.common.extensions.database;

public @interface Index {
    String name();
    String[] value();
    boolean unique() default false;
}
