package me.tabak.nerdery.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import me.tabak.nerdery.R;
import me.tabak.nerdery.data.reddit.model.RedditLink;

public class RedditLinkDetailFragment extends Fragment {
  private static final String KEY_LINK = "link";

  public static RedditLinkDetailFragment newInstance(RedditLink link) {
    RedditLinkDetailFragment fragment = new RedditLinkDetailFragment();
    Bundle args = new Bundle();
    args.putSerializable(KEY_LINK, link);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_link_detail, container, false);
    ButterKnife.inject(this, view);
    return view;
  }
}
