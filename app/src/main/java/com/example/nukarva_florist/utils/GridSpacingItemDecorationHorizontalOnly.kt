package com.example.nukarva_florist.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecorationHorizontalOnly(
    private val spanCount: Int,
    private val spacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        // Only add spacing between items, not on the outer sides
        outRect.left = if (column == 1) spacing / 2 else 0
        outRect.right = if (column == 0) spacing / 2 else 0

        // Optional vertical spacing
        if (position < spanCount) {
            outRect.top = 0
        }
    }
}

