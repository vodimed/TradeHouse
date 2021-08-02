package com.common.extensions.database;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for RecyclerView and ListView classes.
 * All ViewHolder controls must use ItemListener interface
 * (pass events through) for listening events of mouse clicks
 */
public abstract class AdapterRecycler<Item>
        extends RecyclerView.Adapter<AdapterRecycler.ViewHolder>
        implements AdapterInterface<Item>, AdapterInterface.Viewer
{
    private final static boolean useActivated = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB);
    private final AdapterTemplate<Item> template;

    public AdapterRecycler(Context context, @NonNull @LayoutRes int... layout) {
        this(ViewHolder.class, context, layout);
    }

    @SafeVarargs
    public AdapterRecycler(Context context, @NonNull Class<? extends View>... layer) {
        this(ViewHolder.class, context, layer);
    }

    protected AdapterRecycler(Class<? extends ViewHolder> holder,
                              Context context, @NonNull@LayoutRes int... layout)
    {
        super();
        template = new AdapterActual(holder, context, layout);
    }

    @SafeVarargs
    protected AdapterRecycler(Class<? extends ViewHolder> holder,
                              Context context, @NonNull Class<? extends View>... layer)
    {
        super();
        template = new AdapterActual(holder, context, layer);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager current = recyclerView.getLayoutManager();
        if (current == null || !LinearLayoutManager.class.equals(current.getClass()))
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        template.registerParentView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        template.unregisterParentView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        final View child = template.acessible(holder.getView(), false);

        if (child instanceof Checkable) {
            final ViewGroup parent = (ViewGroup) child.getParent();
            final AdapterTemplate.Choicer choicer = template.getChoicerInternal(parent);

            if (choicer != null) {
                final int position = template.getPositionForView(parent, child);

                // See AbsListView.updateOnScreenCheckedViews()
                if (child instanceof Checkable) {
                    ((Checkable) child).setChecked(choicer.isItemChecked(position));
                } else if (useActivated) {
                    child.setActivated(choicer.isItemChecked(position));
                }
            }
        }
    }

    public @Nullable Object getDataSet() {
        return template.getDataSet();
    }

    public void setDataSet(@Nullable Object dataset) {
        template.setDataSet(dataset);
    }

    @NonNull
    @Override // not Holder because Java type-conversion restrictions
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return (ViewHolder) template.createViewHolder(parent, viewType);
    }

    @Override // twin method because Java type-conversion restrictions
    public final void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        onBindViewHolder((Holder) holder, position);
    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return template.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return template.isEnabled(position);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        template.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        template.unregisterDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        return template.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return template.getView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return template.getDropDownView(position, convertView, parent);
    }

    @Override
    public int getViewTypeCount() {
        return template.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return template.isEmpty();
    }

    @Override
    public void setOnItemSelectionListener(@Nullable OnItemSelectionListener listener) {
        template.setOnItemSelectionListener(listener);
    }

    @Override
    public void setChoiceMode(ViewGroup parent, int choiceMode) {
        template.setChoiceMode(parent, choiceMode);
    }

    /**
     * ViewHolder template. You should inherit your ViewHolder from this class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements AdapterInterface.Holder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public View getView() {
            return itemView;
        }
    }

    /**
     * Delegate, implementing basic functionality
     */
    private class AdapterActual extends AdapterTemplate<Item> {
        public AdapterActual(Context context, @NonNull int... layout) {
            super(context, layout);
        }

        protected AdapterActual(Class<? extends Holder> holder,
                                Context context, @NonNull int... layout)
        {
            super(holder, context, layout);
        }

        @SafeVarargs
        protected AdapterActual(Class<? extends Holder> holder,
                                Context context, @NonNull Class<? extends View>... layer)
        {
            super(holder, context, layer);
        }

        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return AdapterRecycler.this.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            AdapterRecycler.this.onBindViewHolder(holder, position);
        }

        @Override
        public Item getItem(int position) {
            return AdapterRecycler.this.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return AdapterRecycler.this.getItemId(position);
        }

        @Override
        protected int getPositionForView(ViewGroup parent, View layout) {
            if (parent instanceof RecyclerView) {
                return ((RecyclerView) parent).getChildAdapterPosition(layout);
            } else {
                return super.getPositionForView(parent, layout);
            }
        }

        @Override
        public long getItemIdForView(ViewGroup parent, View layout) {
            if (parent instanceof RecyclerView) {
                return ((RecyclerView) parent).getChildItemId(layout);
            } else {
                return super.getItemIdForView(parent, layout);
            }
        }

        @Override
        protected boolean performItemClick(ViewGroup parent, View layout, View view, int position) {
            final boolean result = super.performItemClick(parent, layout, view, position);

            if (parent instanceof RecyclerView) {
                final Choicer choicer = getChoicerInternal(parent);
                if (choicer != null) {
                    final RecyclerView.LayoutManager owner = ((RecyclerView) parent).getLayoutManager();
                    final int count = owner.getChildCount();

                    for (int i = 0; i < count; i++) {
                        final View child = owner.getChildAt(i);
                        position = getPositionForView(parent, child);

                        // See AbsListView.updateOnScreenCheckedViews()
                        if (child instanceof Checkable) {
                            ((Checkable) child).setChecked(choicer.isItemChecked(position));
                        } else if (useActivated) {
                            child.setActivated(choicer.isItemChecked(position));
                        }
                    }
                }
            }
            return result;
        }

        @Override
        protected void performDataSetChanged(ViewGroup parent) {
            if (parent instanceof RecyclerView) {
                AdapterRecycler.this.notifyDataSetChanged();
            } else {
                super.performDataSetChanged(parent);
            }
        }
    }
}
