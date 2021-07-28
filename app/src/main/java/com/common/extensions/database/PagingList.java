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
    private final int count;
    private int size;
    private final SparseArray<Value> cache = new SparseArray<Value>(pagesize);
    private final SparseArray<Value> update = new SparseArray<Value>(pagesize);

    @SuppressLint("RestrictedApi")
    public PagingList(DataSource.Factory<Integer, Value> factory) {
        source = (LimitOffsetDataSource<Value>) factory.create();
        size = count = source.countItems();
    }

    @Override
    public Value get(int index) {
        Value cached = update.get(index);

        if (cached == null)
            cached = cache.get(index);

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
    public Value set(int index, Value element) {
        if (index < size) {
            final Value previous = update.get(index);
            update.put(index, element);
            return previous;
        } else if (index == size) {
            size++;
            update.put(index, element);
            return null;
        } else {
            return super.set(index, element);
        }
    }

    @Override
    public boolean add(Value element) {
        set(size, element);
        return true;
    }

    @Override
    public Value remove(int index) {
        if (index < size) {
            final Value previous = update.get(index);
            update.delete(index);
            return previous;
        } else {
            return super.remove(index);
        }
    }

    @Override
    public int size() {
        return size;
    }

    void commit() {

    }

    void rollback() {
        update.clear();
        size = count;
    }
}
