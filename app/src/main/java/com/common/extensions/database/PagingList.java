package com.common.extensions.database;

import android.annotation.SuppressLint;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.SparseArray;
import android.util.SparseBooleanArray;

import androidx.paging.DataSource;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;

public class PagingList<Value> extends AbstractList<Value> implements AdapterInterface.Observable<DataSetObserver> {
    private final static int pagesize = 20;
    private final DataSetObservable notifier = new DataSetObservable();
    private final DataSource<Integer, Value> source;
    private final int count;
    private int size;
    @SuppressWarnings("unchecked") // Java generic restrictions
    private final PagingCache<Value>[] cache = new PagingCache[PagingCache.base];
    private final SparseArray<Value> update = new SparseArray<Value>(pagesize);
    private final SparseBooleanArray delete = new SparseBooleanArray(pagesize);

    @SuppressLint("RestrictedApi")
    public PagingList(DataSource.Factory<Integer, Value> factory) {
        source = factory.create();
        size = count = countItems(source);
    }

    // Restore original index in the DataSet
    protected int identifier(int index) {
        int skip = delete.indexOfKey(index);
        while (skip >= 0) {
            skip++;
            if ((skip >= delete.size()) || (delete.keyAt(skip - 1) != delete.keyAt(skip) - 1)) {
                skip = ~skip;
            }
        }
        return index + (~skip);
    }

    // Retrieve page of data and put it into cache
    protected Value retrieve(int identifier) {
        final int header = PagingCache.header(identifier);
        final int slot = PagingCache.slot(identifier);
        ArrayList<Value> page = (cache[slot] != null ? cache[slot].page(header) : null);

        if (page == null) {
            @SuppressLint("RestrictedApi")
            final ArrayList<Value> data = loadRange(source, header, pagesize);
            cache[slot] = new PagingCache<Value>(header, data);
            page = cache[slot].page(header);
        }

        return (page.size() > 0 ? page.get(identifier - header) : null);
    }

    @Override
    public Value get(int index) {
        final int identifier = identifier(index);
        Value cached = update.get(identifier);
        if (cached == null) cached = retrieve(identifier);
        return cached;
    }

    @Override
    public Value set(int index, Value element) {
        assert (index >= 0) && (index < size);
        final int identifier = identifier(index);
        update.put(identifier, element);
        notifier.notifyChanged();
        return null;
    }

    @Override
    public boolean add(Value element) {
        final int identifier = identifier(size);
        update.put(identifier, element);
        size++;
        notifier.notifyChanged();
        return true;
    }

    @Override
    public Value remove(int index) {
        assert (index >= 0) && (index < size);
        final int identifier = identifier(index);
        update.delete(identifier);
        delete.put(identifier, true);
        size--;
        notifier.notifyChanged();
        return null;
    }

    public void dismiss(int index) {
        assert (index >= 0) && (index < size);
        final int identifier = identifier(index);
        update.put(identifier, null);
        notifier.notifyChanged();
    }

    public void recover(int index) {
        assert (index >= 0) && (index < size);
        final int identifier = identifier(index);
        update.put(identifier, retrieve(identifier));
        notifier.notifyChanged();
    }

    @Override
    public int size() {
        return size;
    }

    public void commit(Commit<Value> listener) {
        for (int i = delete.size() - 1; i >= 0; i--) {
            listener.delete(retrieve(delete.keyAt(i)));
        }

        for (int i = update.size() - 1; i >= 0; i--) {
            listener.replace(update.valueAt(i));
        }

        //update.clear(); TODO!
        //delete.clear();
        //notifier.notifyChanged();
    }

    public void rollback() {
        update.clear();
        delete.clear();
        size = count;
        notifier.notifyChanged();
    }

    @Override
    public void registerObserver(DataSetObserver observer) {
        notifier.registerObserver(observer);
    }

    @Override
    public void unregisterObserver(DataSetObserver observer) {
        notifier.unregisterObserver(observer);
    }

    @Override
    public void unregisterAll() {
        notifier.unregisterAll();
    }

    /**
     * Commit Listener Interface
     */
    public interface Commit<DAO> {
//TODO        void replace(DAO... objects);
//TODO        void delete(DAO... objects);
        void replace(DAO objects);
        void delete(DAO objects);
    }

    /**
     * Cache element
     */
    private static class PagingCache<Value> {
        private final static int base = 31;
        private final WeakReference<ArrayList<Value>> page;
        private final int header;

        public PagingCache(int header, ArrayList<Value> page) {
            this.header = header;
            this.page = new WeakReference<ArrayList<Value>>(page);
        }

        public ArrayList<Value> page(int header) {
            if (this.header != header) {
                return null;
            } else {
                return page.get();
            }
        }

        public static int header(int index) {
            return (index / pagesize) * pagesize;
        }

        public static int slot(int index) {
            return (index / pagesize) % base;
        }
    }

    private int countItems(DataSource<Integer, Value> source) {
        try {
            final Method countItems = source.getClass().getMethod("countItems"); // LimitOffsetDataSource
            return (Integer) countItems.invoke(source);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Value> loadRange(DataSource<Integer, Value> source, int header, int pagesize) {
        try {
            final Method loadRange = source.getClass().getMethod("loadRange", Integer.TYPE, Integer.TYPE); // LimitOffsetDataSource
            return (ArrayList<Value>) loadRange.invoke(source, header, pagesize);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
