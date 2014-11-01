package me.tabak.nerdery.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import me.tabak.nerdery.MainActivity;
import me.tabak.nerdery.MyApplication;
import me.tabak.nerdery.R;
import me.tabak.nerdery.data.reddit.model.RedditLink;
import me.tabak.nerdery.ui.viewholder.RedditLinkViewHolder;

public class RedditLinkDetailFragment extends RecyclerFragment {
  private static final String KEY_LINK = "link";
  @InjectView(R.id.recyclerview) RecyclerView mRecyclerView;
  private MainActivity mActivity;
  private CommentsAdapter mAdapter;
  private RedditLink mLink;
  private LinearLayoutManager mLayoutManager;

  public static RedditLinkDetailFragment newInstance(RedditLink link) {
    RedditLinkDetailFragment fragment = new RedditLinkDetailFragment();
    Bundle args = new Bundle();
    args.putSerializable(KEY_LINK, link);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MyApplication.get(getActivity()).inject(this);
    setHasOptionsMenu(true);
    mActivity = (MainActivity) getActivity();
    mLink = (RedditLink) getArguments().getSerializable(KEY_LINK);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mAdapter = new CommentsAdapter();
    mRecyclerView.setAdapter(mAdapter);
    mLayoutManager = new LinearLayoutManager(getActivity());
    mRecyclerView.setLayoutManager(mLayoutManager);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.link_detail, menu);
    menu.findItem(R.id.menu_show_list).setOnMenuItemClickListener(item -> {
      mActivity.hideDetailFragment();
      return true;
    });
  }

  private class CommentsAdapter extends RecyclerView.Adapter {
    public static final int TYPE_LINK = 0;
    public static final int TYPE_COMMENT = 1;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
      if (i == TYPE_LINK) {
        CardView cardView = new CardView(getActivity());
        cardView.setLayoutParams(new RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        getLayoutInflater(null).inflate(R.layout.view_reddit_link, cardView, true);
        return new RedditLinkViewHolder(cardView);
      } else if (i == TYPE_COMMENT) {
        return null;
      } else {
        throw new IllegalStateException("Naughty developer.");
      }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
      if (getItemViewType(i) == TYPE_LINK) {
        ((RedditLinkViewHolder)viewHolder).bindView(mLink, true);
        viewHolder.itemView.setOnClickListener(v -> mActivity.showWebView(mLink.getUrl()));
      } else if (getItemViewType(i) == TYPE_COMMENT) {

      } else {
        throw new IllegalStateException("Naughty developer.");
      }
    }

    @Override
    public int getItemViewType(int position) {
      if (position == 0) {
        return TYPE_LINK;
      } else {
        return TYPE_COMMENT;
      }
    }

    @Override
    public int getItemCount() {
      return 1;
    }
  }
}
