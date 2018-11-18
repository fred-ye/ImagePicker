package cn.fredye.imagepicker.ui.adapter;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by fred on 12/07/2018.
 */

public abstract class BaseRecyclerViewCursorAdapter <VH extends RecyclerView.ViewHolder> extends
        RecyclerView.Adapter<VH> {

    private Cursor mCursor;
    private int mRowIDColumn;

    BaseRecyclerViewCursorAdapter(Cursor c) {
        setHasStableIds(true);
        swapCursor(c);
    }

    protected abstract void onBindViewHolder(VH holder, Cursor cursor);


    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (!isDataValid(mCursor)) {
            throw new IllegalStateException("Cannot bind holder when cursor is in invalid state.");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position
                    + " when trying to bind holder");
        }

        onBindViewHolder(holder, mCursor);

    }

    @Override
    public int getItemViewType(int position) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position
                    + " when trying to get item view type.");
        }
        return getItemViewType(position, mCursor);
    }

    protected abstract int getItemViewType(int position, Cursor cursor);

    @Override
    public int getItemCount() {
        if (isDataValid(mCursor)) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        if (!isDataValid(mCursor)) {
            throw new IllegalStateException("Cannot lookup item id when cursor is in invalid state.");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position
                    + " when trying to get an item id");
        }

        return mCursor.getLong(mRowIDColumn);
    }

    public void swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return;
        }

        if (newCursor != null) {
            mCursor = newCursor;
            mRowIDColumn = mCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, getItemCount());
            mCursor = null;
            mRowIDColumn = -1;
        }
    }


    public Cursor getCursor() {
        return mCursor;
    }
    private boolean isDataValid(Cursor cursor) {
        return cursor != null && !cursor.isClosed();
    }
}
