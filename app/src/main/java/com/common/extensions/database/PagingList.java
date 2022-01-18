package com.common.extensions.database;

import android.annotation.SuppressLint;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.SparseArray;
import android.util.SparseBooleanArray;

import androidx.annotation.Nullable;
import androidx.paging.DataSource;

import com.common.extensions.Logger;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class PagingList<Value> extends AbstractList<Value> implements AdapterInterface.Observable<DataSetObserver> {
    private static final int pagesize = 20;
    private final DataSetObservable notifier = new DataSetObservable();
    private final DataSource.Factory<Integer, Value> factory;
    private DataSource<Integer, Value> source;
    private int count;
    private int size;
    @SuppressWarnings("unchecked") // Java generic restrictions
    private final PagingCache<Value>[] cache = new PagingCache[PagingCache.base];
    private final SparseArray<Value> update = new SparseArray<Value>(pagesize);
    private final SparseBooleanArray delete = new SparseBooleanArray(pagesize);

    /**
     * Commit Listener Interface
     */
    public interface Commit<Value> {
        void renew(Value objects);
        void delete(Value objects);
    }

    @SuppressLint("RestrictedApi")
    public PagingList(DataSource.Factory<Integer, Value> factory) {
        this.factory = factory;
        reloadList();
    }

    private void reloadList() {
        source = factory.create();
        size = count = countItems(source);
    }

    // Restore original index in the DataSet
    protected int identifier(int position) {
        int index = delete.indexOfKey(position);
        if (index >= 0) {
            int sequence = delete.keyAt(index);
            do {
                index++;
                sequence++;
            } while ((index >= 0) && (delete.keyAt(index) == sequence));
            index = ~index;
        }
        return position + (~index);
    }

    // Restore actual position in the Adapter
    protected int position(int identifier) {
        return identifier - (~delete.indexOfKey(identifier));
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

        if ((page != null) && (page.size() > 0)) {
            return page.get(identifier - header);
        } else {
            return null;
        }
    }

    @Override
    public Value get(int position) {
        final int identifier = identifier(position);
        Value cached = update.get(identifier);
        if (cached == null) cached = retrieve(identifier);
        return cached;
    }

    @Override
    public Value set(int position, Value element) {
        assert (position >= 0) && (position < size);
        final int identifier = identifier(position);
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
    public Value remove(int position) {
        assert (position >= 0) && (position < size);
        final int identifier = identifier(position);
        update.delete(identifier);
        delete.put(identifier, true);
        size--;
        notifier.notifyChanged();
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int indexOf(@Nullable Object o) {
        final Value element = (Value) o;
        int identifier = 0;
        for (Iterator<Value> itr = iterator(); itr.hasNext(); identifier++) {
            final Value exists = itr.next();
            if (exists.equals(element)) {
                return position(identifier);
            }
        }
        return -1;
    }

    @Override
    public int size() {
        return size;
    }

    public void dismiss(int position) {
        assert (position >= 0) && (position < size);
        final int identifier = identifier(position);
        update.put(identifier, null);
        notifier.notifyChanged();
    }

    public void recover(int position) {
        assert (position >= 0) && (position < size);
        final int identifier = identifier(position);
        update.put(identifier, retrieve(identifier));
        notifier.notifyChanged();
    }

    public void commit(Commit<Value> listener) {
        for (int i = delete.size() - 1; i >= 0; i--) {
            listener.delete(retrieve(delete.keyAt(i)));
        }

        for (int i = update.size() - 1; i >= 0; i--) {
            listener.renew(update.valueAt(i));
        }

        Arrays.fill(cache, null);
        update.clear();
        delete.clear();
        reloadList();
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
     * Cache element
     */
    private static class PagingCache<Value> {
        private static final int base = 31;
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

        public static int header(int identifier) {
            return (identifier / pagesize) * pagesize;
        }

        public static int slot(int identifier) {
            return (identifier / pagesize) % base;
        }
    }

    private int countItems(DataSource<Integer, Value> source) {
        try {
            final Method countItems = source.getClass().getMethod("countItems"); // LimitOffsetDataSource
            return (Integer) countItems.invoke(source);
        } catch (ReflectiveOperationException e) {
            Logger.e(e);
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Value> loadRange(DataSource<Integer, Value> source, int header, int pagesize) {
        try {
            final Method loadRange = source.getClass().getMethod("loadRange", Integer.TYPE, Integer.TYPE); // LimitOffsetDataSource
            return (ArrayList<Value>) loadRange.invoke(source, header, pagesize);
        } catch (ReflectiveOperationException e) {
            Logger.e(e);
            return null;
        }
    }
}
