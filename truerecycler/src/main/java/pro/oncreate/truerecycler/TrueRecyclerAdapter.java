package pro.oncreate.truerecycler;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Andrii Konovalenko, 2014-2017 years.
 * Copyright © 2017 [Andrii Konovalenko]. All Rights Reserved.
 */

@SuppressWarnings("unused,WeakerAccess")
public abstract class TrueRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    //
    // Data and states
    //


    /**
     * The collection of the any elements that will be presented in the form of a data model representation.
     */
    protected List<T> items = new ArrayList<>();

    /**
     * The collection of headers views.
     */
    protected List<View> headers = new ArrayList<>();

    /**
     * The collection of footers views.
     */
    protected List<View> footers = new ArrayList<>();

    /**
     * Load more state. Is load more now.
     * Default value false.
     */
    private boolean isLoading = false;

    /**
     * Load more state. Is last page loaded.
     * Default value false.
     */
    private boolean allLoaded = false;

    /**
     * Load more listener.
     */
    private LoadMoreListener loadMoreListener;

    /**
     * Number of items per page.
     * Default value 20.
     */
    private int pageCount = 20;

    /**
     * The number of items before calling load more.
     */
    private int visibleThreshold = 3;

    /**
     * The number of items before calling load more.
     */
    public static final int POSITION_NONE = -1;

    /**
     * Empty view instance.
     */
    private Object emptyView;


    //
    // Add headers and footers views
    //


    /**
     * Add new header view.
     */
    public void addHeader(View v) {
        this.headers.add(v);
        notifyItemInserted(headers.size() - 1);
    }

    /**
     * Add new footer view.
     */
    public void addFooter(View v) {
        this.footers.add(v);
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * Remove header.
     *
     * @param position header position
     */
    public void removeHeader(int position) {
        if (position >= 0 && position < this.headers.size()) {
            this.headers.remove(position);
            this.notifyItemRemoved(position);
        }
    }


    //
    // Get adapter basic data and states
    //


    /**
     * @return view count = total count of items + headers + footers.
     */
    @Override
    public int getItemCount() {
        return items.size() + headers.size() + footers.size();
    }

    /**
     * @return items collection.
     */
    public List<T> getItems() {
        return items;
    }

    /**
     * @return total count of items collection.
     */
    public int getItemsSize() {
        return items.size();
    }

    /**
     * @return total count of items collection.
     */
    public int getHeadersSize() {
        return headers.size();
    }

    /**
     * @return true if 0 items in collection, false - contrariwise.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * @param positionInAdapter of view in adapter.
     * @return position in items collection.
     */
    public int getRelativeItemPosition(int positionInAdapter) {
        return positionInAdapter - headers.size();
    }

    /**
     * @param positionInAdapter of view in adapter.
     * @return position in headers collection.
     */
    public int getRelativeFooterPosition(int positionInAdapter) {
        return positionInAdapter - headers.size() - items.size();
    }

    /**
     * @return last item in collection, if collection is empty, method return null.
     */
    public T getLastItem() {
        if (getItemCount() > 0)
            return items.get(items.size() - 1);
        else return null;
    }

    /**
     * @return first item in collection, if collection is empty, method return null.
     */
    public T getFirstItem() {
        if (getItemCount() > 0)
            return items.get(0);
        else return null;
    }

    /**
     * @param model - search item
     * @return position in items collection or -1 if item not found.
     */
    public int getItemPosition(T model) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == model)
                return i;
        }
        return POSITION_NONE;
    }

    /**
     * @param position in items collection.
     * @return search item.
     */
    public T getItem(int position) {
        return items.get(position);
    }

    /**
     * The default implementation.
     *
     * @param position in adapter.
     * @return a unique id.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    //
    // Default actions with collection
    //


    /**
     * Add more items to the collection.
     *
     * @param items - not an empty collection.
     */
    public void addAll(List<T> items) {
        if (items != null && items.size() > 0) {
            this.items.addAll(items);
            int from = headers.size() + this.items.size() - items.size();
            int count = items.size() + footers.size();
            this.notifyItemRangeInserted(from, count);
        }
    }

    /**
     * Add more items to the collection.
     *
     * @param collection - not an empty collection.
     */
    public void addAll(Collection<? extends T> collection) {
        if (collection != null && collection.size() > 0) {
            this.items.addAll(collection);
            int from = headers.size() + this.items.size() - collection.size();
            int count = collection.size() + footers.size();
            this.notifyItemRangeInserted(from, count);
        }
    }

    /**
     * Add more items to the collection.
     *
     * @param items - not an empty collection.
     */
    public void addAll(int position, ArrayList<T> items) {
        if (items != null && position >= 0 && position < items.size()) {
            this.items.addAll(position, items);
            int from = headers.size() + position - items.size();
            int count = items.size();
            this.notifyItemRangeInserted(from, count);
        }
    }

    /**
     * Add item to the collection.
     */
    public void add(T item) {
        if (item == null)
            throw new NullPointerException("item is null");
        ArrayList<T> items = new ArrayList<>();
        items.add(item);
        addAll(items);
    }

    /**
     * Add item to the collection.
     */
    public void add(int position, T item) {
        if (item == null)
            throw new NullPointerException("item is null");
        ArrayList<T> items = new ArrayList<>();
        items.add(position, item);
        addAll(items);
    }

    /**
     * Clear all data in items collection.
     * By default set last loading false.
     *
     * @see TrueRecyclerAdapter#clear(boolean)
     */
    public void clear() {
        int oldSize = items.size();
        this.items.clear();
        this.notifyItemRangeRemoved(headers.size(), oldSize);
        loadingFinish(false);
    }

    /**
     * Clear all data in items collection.
     * You manage last loading state.
     *
     * @see TrueRecyclerAdapter#clear()
     */
    public void clear(boolean lastLoading) {
        int oldSize = items.size();
        this.items.clear();
        this.notifyItemRangeRemoved(headers.size(), oldSize);
        this.loadingFinish(lastLoading);
    }

    /**
     * Remove item in items collections and remove items view from adapter.
     *
     * @param position in items collection
     * @see TrueRecyclerAdapter#remove(Object)
     */
    public void remove(int position) {
        if (position >= 0 && position < this.items.size()) {
            this.items.remove(position);
            this.notifyItemRemoved(headers.size() + position);
        }
    }

    /**
     * Remove item in items collections and remove items view from adapter (if exist).
     *
     * @param model the element of items collection
     * @see TrueRecyclerAdapter#remove(int)
     */
    public void remove(T model) {
        int position = getItemPosition(model);
        if (position >= 0)
            remove(position);
    }


    //
    // Methods for controlling the types of view
    //


    /**
     * Method compares the presentation stand with a collection of elements.
     * The main type of cells has VIEW_TYPES#NORMAL.
     * Headers and footers have as a type of its own serial number offset to a certain constant.
     *
     * @param position of adapter view.
     * @return the type of view.
     */
    @Override
    public int getItemViewType(int position) {
        int type;
        if (headers.size() > 0 && position < headers.size())
            type = VIEW_TYPES.HEADER + position;
        else if (!footers.isEmpty() && position >= headers.size() + items.size())
            type = position - headers.size() - items.size() + VIEW_TYPES.FOOTER;
        else
            type = getNormalType(position);
        return type;
    }

    /**
     * Method returns an integer variable which is associated with the normal type.
     * By default method return VIEW_TYPES$NORMAL.
     * You must override this method if your items may be presented in different forms.
     *
     * @param position of adapter view.
     * @return int value.
     */
    protected int getNormalType(int position) {
        return VIEW_TYPES.NORMAL;
    }


    public int getItemType(int position) {
        if (!headers.isEmpty() && position < headers.size())
            return VIEW_TYPES.HEADER;
        else if (!footers.isEmpty() && position >= headers.size() + items.size())
            return VIEW_TYPES.FOOTER;
        else return VIEW_TYPES.NORMAL;
    }

    /**
     * @param parent views container
     * @param type   value of VIEW_TYPES
     * @return the viewHolder for the specified type
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View itemLayoutView;
        if (type == VIEW_TYPES.NORMAL) {
            return onCreateNormalHolder(parent);
        } else if (type >= VIEW_TYPES.HEADER) {
            itemLayoutView = headers.get(type - VIEW_TYPES.HEADER);
            TrueUtils.removeParent(itemLayoutView);
            itemLayoutView.setLayoutParams(parent.getLayoutParams());
            return onCreateHeaderHolder(itemLayoutView, parent);
        } else if (type <= VIEW_TYPES.FOOTER) {
            itemLayoutView = footers.get(type - VIEW_TYPES.FOOTER);
            TrueUtils.removeParent(itemLayoutView);
            ViewGroup.LayoutParams lp = parent.getLayoutParams();
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            itemLayoutView.setLayoutParams(lp);
            return onCreateFooterHolder(itemLayoutView, parent);
        } else return onCreateOtherHolder(parent, type);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemType(position);
        if (type == VIEW_TYPES.NORMAL) {
            onBindNormalHolder((VH) holder, position, getItem(getRelativeItemPosition(position)));
        } else if (type >= VIEW_TYPES.HEADER) {
            onBindHeaderHolder((DefaultHeaderViewHolder) holder, position);
        } else if (type <= VIEW_TYPES.FOOTER) {
            onBindFooterHolder((DefaultFooterViewHolder) holder, position);
        } else onBindOtherHolder(holder, position);
    }

    /**
     * You must override this method for change header holder.
     *
     * @param v      - view from headers collection from position
     * @param parent - root element
     * @return DefaultViewHolder(v);
     */
    protected RecyclerView.ViewHolder onCreateHeaderHolder(View v, ViewGroup parent) {
        return new DefaultHeaderViewHolder(v);
    }

    /**
     * You must override this method for change footer holder.
     *
     * @param v      - view from footers collection from position
     * @param parent - root element
     * @return DefaultViewHolder(v);
     */
    protected RecyclerView.ViewHolder onCreateFooterHolder(View v, ViewGroup parent) {
        return new DefaultFooterViewHolder(v);
    }

    /**
     * You must override this method for change VIEW_TYPES$NORMAL holder.
     *
     * @param parent - root element
     * @return DefaultViewHolder(v);
     */
    protected RecyclerView.ViewHolder onCreateNormalHolder(ViewGroup parent) {
        return new DefaultViewHolder(parent);
    }

    /**
     * You must override this method for change other types view holders.
     *
     * @param parent - root element
     * @param type   - code of custom type
     * @return DefaultViewHolder(v);
     */
    protected RecyclerView.ViewHolder onCreateOtherHolder(ViewGroup parent, int type) {
        return new DefaultViewHolder(parent);
    }

    public void onBindNormalHolder(VH holder, int position, T model) {

    }

    public void onBindHeaderHolder(DefaultHeaderViewHolder holder, int position) {

    }

    public void onBindFooterHolder(DefaultFooterViewHolder holder, int position) {

    }

    public void onBindOtherHolder(RecyclerView.ViewHolder holder, int position) {

    }


    /**
     * The element types numerically
     */
    private class VIEW_TYPES {
        static final int HEADER = 1000;
        static final int NORMAL = 0;
        static final int FOOTER = -1000;
    }

    /**
     * Default empty viewHolder
     */
    public class DefaultViewHolder extends RecyclerView.ViewHolder {
        public DefaultViewHolder(View v) {
            super(v);
        }
    }

    /**
     * Default header viewHolder
     */
    public class DefaultHeaderViewHolder extends RecyclerView.ViewHolder {
        public DefaultHeaderViewHolder(View v) {
            super(v);
        }
    }

    /**
     * Default footer viewHolder
     */
    public class DefaultFooterViewHolder extends RecyclerView.ViewHolder {
        public DefaultFooterViewHolder(View v) {
            super(v);
        }
    }


    //
    // Load more functional
    //


    /**
     * Call this method if you want to start track challenge load more.
     */
    public void enableLoadMore(RecyclerView recyclerView, TrueRecyclerAdapter.LoadMoreListener loadMoreListener) {
        setLoadMoreListener(loadMoreListener);
        if (recyclerView != null)
            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            int totalItemCount;
                            int lastVisibleItem;

                            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                totalItemCount = linearLayoutManager.getItemCount();
                                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                            } else {
                                throw new IllegalArgumentException("Unsupported LayoutManager for load more");
                            }

                            if (!isLoading() && !isAllLoaded()
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold) && !items.isEmpty() && loadMoreCondition()) {
                                if (TrueRecyclerAdapter.this.loadMoreListener != null)
                                    TrueRecyclerAdapter.this.loadMoreListener.loadMore();
                                loadingStartWithLoadMore();
                            }
                        }
                    });
    }

    /**
     * Call this method if you want to inform the adapter to beginning loading next items.
     * This method starts by default adapter.
     */
    void loadingStartWithLoadMore() {
        this.setLoading(true);
        this.onChangeLoadMoreProgressState(true);
    }

    /**
     * Call this method if you want to inform the adapter to finishing loading items.
     */
    public void loaded() {
        this.onChangeLoadMoreProgressState(false);
        this.setLoading(false);
    }

    /**
     * Get loading all page state value.
     *
     * @return boolean
     */
    public boolean isAllLoaded() {
        return allLoaded;
    }

    /**
     * Get visible threshold value.
     *
     * @return int
     */
    public int getVisibleThreshold() {
        return visibleThreshold;
    }

    /**
     * Set visible threshold value.
     *
     * @param visibleThreshold call load more before N - visibleThreshold elements.
     */
    public void setVisibleThreshold(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    /**
     * Set load more listener.
     */
    public void setLoadMoreListener(TrueRecyclerAdapter.LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    /**
     * Set count items on page.
     */
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * Get count items on page.
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * Set loading now state. If true load more events will not come.
     *
     * @param isLoading loading now
     */
    void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    /**
     * Set loading now state true.
     */
    public void loading() {
        setLoading(true);
    }

    /**
     * Get loading now state value.
     *
     * @return boolean
     */
    public boolean isLoading() {
        return isLoading;
    }

    /**
     * Set loading all pages complete state. If true load more events will not come.
     *
     * @param allLoaded last item loading
     */
    public void loadingFinish(boolean allLoaded) {
        this.allLoaded = allLoaded;
        emptyViewSyncState();
    }

    /**
     * By default, the download starts if the following conditions: loading is not happening at the moment, all pages have not yet been uploaded, items array not empty and current element is not visible at threshold position.
     * But you can extend these conditions by overriding this method.
     *
     * @return false - call for ban load more.
     */
    protected boolean loadMoreCondition() {
        return true;
    }

    /**
     * You should override this method to define the presentation of progress view load more.
     *
     * @return view of footer progress.
     */
    public View getFooterProgress() {
        return null;
    }


    /**
     * The method shows or hides the progress indicator in footer for load more functional.
     *
     * @param show true - show, false - hide footer progress.
     */
    private void onChangeLoadMoreProgressState(boolean show) {
        if (show && footers.isEmpty()) {
            footers.add(getFooterProgress());
            notifyItemInserted(getItemCount() - 1);
        } else if (!show && !footers.isEmpty()) {
            footers.remove(footers.size() - 1);
            notifyItemRemoved(getItemCount());
            notifyItemChanged(getItemCount(), null);
        }
    }

    /**
     * Listener interface load more.
     */
    public interface LoadMoreListener {
        void loadMore();
    }


    //
    // Empty view
    //

    /**
     * Use only EmptyViewAdapterInterface instance or ready-made solution: https://github.com/jaksab/EmptyView
     */
    public void setEmptyView(Object emptyView) {
        if (emptyView instanceof EmptyViewAdapterInterface ||
                emptyView.getClass().getName().equals("pro.oncreate.emptyview.EmptyView"))
            this.emptyView = emptyView;
        else
            throw new NullPointerException("Use only EmptyViewAdapterInterface instance or ready-made solution: https://github.com/jaksab/EmptyView");
    }

    protected void emptyViewSyncState() {
        try {
            if (this.emptyView != null && allLoaded && this.isEmpty()) {
                emptyView.getClass().getDeclaredMethod("empty").invoke(emptyView);
            } else if (this.emptyView != null) {
                emptyView.getClass().getDeclaredMethod("reset").invoke(emptyView);
            }
        } catch (Exception ignored) {
            Log.e("TrueRecyclerView", "Add dependency to EmptyView");
        }
    }
}