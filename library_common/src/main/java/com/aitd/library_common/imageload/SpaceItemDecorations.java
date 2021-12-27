package com.aitd.library_common.imageload;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecorations extends RecyclerView.ItemDecoration{

    private int left;
    private int right;
    private int top;
    private int bottom;

    //leftRight为横向间的距离 topBottom为纵向间距离
    public SpaceItemDecorations(int left,int right, int top,int bottom) {
        this.left   = left;
        this.right  = right;
        this.top    = top;
        this.bottom = bottom;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        //竖直方向的
        if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
            //最后一项需要 bottom
            if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
                outRect.bottom = bottom;
            }else {
                if (bottom !=0){
                    outRect.bottom = bottom;
                }
            }
            outRect.top = top;
            outRect.left = left;
            outRect.right = right;
        } else {
            //最后一项需要right
            if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
                outRect.right = 0;
            }else{
                outRect.right = right;
            }
            outRect.top = top;
            outRect.left = left;
            outRect.bottom = bottom;
        }
    }
}
