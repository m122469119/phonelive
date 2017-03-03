package com.bolema.phonelive.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by yuanshuo on 2017/3/3.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    int mSpace ;
    int listSize;
    /**
     * @param space 传入的值，其单位视为dp
     */
    public SpaceItemDecoration(int space, int listSize) {
        this.mSpace = space;
        this.listSize = listSize;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int itemCount = listSize;
        int pos = parent.getChildAdapterPosition(view);
//        Log.d(TAG, "itemCount>>" +itemCount + ";Position>>" + pos);

        outRect.left = 0;
        outRect.top = 0;
        outRect.bottom = 0;


        if (pos != (itemCount -1)) {
            outRect.right = mSpace;
        } else {
            outRect.right = 0;
        }
    }
}
