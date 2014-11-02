package me.tabak.cakeday.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacerDecoration extends RecyclerView.ItemDecoration {
  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);
    // TODO - Add configuration-specific padding to look good on tablets?
  }
}
