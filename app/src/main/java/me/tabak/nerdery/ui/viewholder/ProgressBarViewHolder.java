package me.tabak.nerdery.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import me.tabak.nerdery.R;

public class ProgressBarViewHolder extends RecyclerView.ViewHolder {
  @InjectView(R.id.progressbar) ProgressBar mProgressBar;
  public ProgressBarViewHolder(View itemView) {
    super(itemView);
    ButterKnife.inject(this, itemView);
  }
}
