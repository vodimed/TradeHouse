package com.common.extensions.database;

import android.annotation.SuppressLint;
import android.util.SparseArray;

import androidx.paging.DataSource;
import androidx.room.paging.LimitOffsetDataSource;

import java.util.AbstractList;
import java.util.ArrayList;

public class PagingList<Value> extends AbstractList<Value> {
    private final static int pagesize = 20;
    private final LimitOffsetDataSource<Value> source;
    private final SparseArray<Value> cache = new SparseArray<Value>(pagesize);
    private final int size;

    @SuppressLint("RestrictedApi")
    public PagingList(DataSource.Factory<Integer, Value> factory) {
        source = (LimitOffsetDataSource<Value>) factory.create();
        size = source.countItems();
    }

    @Override
    public Value get(int index) {
        Value cached = cache.get(index);

        if (cached == null) {
            if (cache.size() > 100 * pagesize) cache.clear();
            final int base = (index / pagesize) * pagesize;

            @SuppressLint("RestrictedApi")
            final ArrayList<Value> data = (ArrayList<Value>) source.loadRange(base, pagesize);
            cached = data.get(index - base);

            for (int i = 0; i < data.size(); i++) {
                cache.put(base + i, data.get(i));
            }
        }
        return cached;
    }

    @Override
    public int size() {
        return size;
    }
}
