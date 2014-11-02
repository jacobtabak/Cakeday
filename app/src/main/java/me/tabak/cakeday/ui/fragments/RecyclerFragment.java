package me.tabak.cakeday.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import me.tabak.cakeday.R;

public class RecyclerFragment extends Fragment {
  @InjectView(R.id.recyclerview) RecyclerView mRecyclerView;
  @InjectView(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mSwipeRefreshLayout.setColorSchemeColors(
        getResources().getColor(R.color.orange_bright),
        getResources().getColor(R.color.orange_dark),
        getResources().getColor(R.color.orange_light),
        getResources().getColor(R.color.orange_pale)
    );
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
    ButterKnife.inject(this, view);
    return view;
  }
}
