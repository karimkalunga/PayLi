package com.media.clouds.app.utils;

import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class Decorator extends RecyclerView.ItemDecoration {

    public static final String HORIZONTAL = "horizontal";
    public static final String VERTICAL = "vertical";
    public static final String GRID = "grid";
    private String spacingDirection;
    private int spanCount;
    private int spacing;

    public Decorator(int spacing, Context context, String spacingDirection) {
        this.spacing = getPixelDensity(context, spacing);
        this.spacingDirection = spacingDirection;
    }

    public Decorator(int spacing, Context context, int spanCount) {
        this.spacing = getPixelDensity(context, spacing);
        this.spanCount = spanCount;
        this.spacingDirection = GRID;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        switch (spacingDirection) {
            case VERTICAL:
                outRect.top = 0;
                outRect.bottom = 0;
                outRect.right = spacing;
                outRect.left = (position == 0) ? spacing : 0;
                break;

            case GRID:
                int column = position % spanCount;
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
                break;

            default:
                outRect.left = spacing;
                outRect.right = spacing;
                outRect.bottom = spacing;
                outRect.top = (position == 0) ? spacing : 0;
                break;
        }
    }

    /**
     * Converts integer value to DPI.
     * @param value integer.
     * @return DPI value.
     */
    private int getPixelDensity(Context context, int value) {
        float scalingFactor = context.getResources().getDisplayMetrics().density;
        return (int) (value * scalingFactor + 0.5f);
    }
}