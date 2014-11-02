package me.tabak.cakeday.ui.fragments;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import me.tabak.cakeday.MainActivity;
import me.tabak.cakeday.MyApplication;
import me.tabak.cakeday.R;
import me.tabak.cakeday.data.reddit.RedditService;
import me.tabak.cakeday.data.reddit.model.RedditLink;
import me.tabak.cakeday.data.reddit.model.RedditObject;
import me.tabak.cakeday.rx.EndlessObserver;
import me.tabak.cakeday.ui.recycler.anim.MyItemAnimator;
import me.tabak.cakeday.ui.viewholder.RedditLinkViewHolder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class RedditLinkListFragment extends Fragment {
  public static final int LIMIT = 25;
  @Inject RedditService mRedditService;
  @InjectView(R.id.list_recyclerview) RecyclerView mRecyclerView;
  @InjectView(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
  private List<RedditLink> mLinks = new ArrayList<>();
  private RedditLinkAdapter mAdapter;
  private MainActivity mActivity;
  private LinearLayoutManager mLayoutManager;
  private String mSort = "hot";
  private LinksRefreshListener mRefreshListener;
  private String mAfterId;
  private String mBeforeId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MyApplication.get(getActivity()).inject(this);
    mAdapter = new RedditLinkAdapter();
    mActivity = (MainActivity) getActivity();
    setHasOptionsMenu(true);
    mActivity.setTitle(getString(R.string.app_name) + " - " + getString(R.string.listing_hot));
    if (savedInstanceState == null) {
      createRequest(true).subscribe(new LinksObserver());
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_list, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRefreshListener = new LinksRefreshListener();
    mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);
    mLayoutManager = new LinearLayoutManager(getActivity());
    mRecyclerView.setLayoutManager(mLayoutManager);
    mRecyclerView.setAdapter(mAdapter);
    if (Build.VERSION.SDK_INT >= 11) {
      mRecyclerView.setItemAnimator(new MyItemAnimator(mLayoutManager));
    }
    mRecyclerView.setOnScrollListener(new LinkListScrollListener());
    if (mAdapter.getItemCount() == 0) {
      mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
    }
    mSwipeRefreshLayout.setColorSchemeColors(
        getResources().getColor(R.color.orange_bright),
        getResources().getColor(R.color.orange_dark),
        getResources().getColor(R.color.orange_light),
        getResources().getColor(R.color.orange_pale)
    );
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_link_list, menu);
    menu.findItem(R.id.menu_clear_all).setOnMenuItemClickListener(item -> {
      if (!mSwipeRefreshLayout.isRefreshing()) {
        mRefreshListener.onRefresh();
      }
      return true;
    });

    // Set up click listeners for the sorting menu
    SubMenu sortMenu = menu.findItem(R.id.menu_sort).getSubMenu();
    for (int i = 0; i < sortMenu.size(); i++) {
      sortMenu.getItem(i).setOnMenuItemClickListener(item -> {
        // Reset before and after ID when changing listings.
        mBeforeId = null;
        mAfterId = null;

        mActivity.setTitle(getString(R.string.app_name) + " - " + item.getTitle());
        mSort = item.getTitle().toString().toLowerCase();
        mRefreshListener.onRefresh();
        return true;
      });
    }
  }

  private Observable<List<RedditObject>> createRequest(boolean after) {
    // Null out the field we're not interested in.
    if (after) {
      mBeforeId = null;
    } else {
      mAfterId = null;
    }
    return mRedditService.getLinks(mSort, LIMIT, mAfterId, mBeforeId)
        .map(response -> response.getData().getChildren())
        .observeOn(AndroidSchedulers.mainThread());
  }

  private class RedditLinkAdapter extends RecyclerView.Adapter<RedditLinkViewHolder> {

    @Override
    public RedditLinkViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
      CardView cardView = new CardView(getActivity());
      cardView.setLayoutParams(new RecyclerView.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      getLayoutInflater(null).inflate(R.layout.view_reddit_link, cardView, true);
      return new RedditLinkViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(RedditLinkViewHolder holder, int position) {
      holder.bindView(mLinks.get(position), false);
      holder.itemView.setOnClickListener(v -> mActivity.showDetailFragment(mLinks.get(position)));
    }

    @Override
    public int getItemCount() {
      // Always add one extra slot at the end for the 'loading more items' view.
      return mLinks.size();
    }
  }

  private class LinksObserver extends EndlessObserver<List<RedditObject>> {
    private final boolean mAfter;

    /**
     * Constructor that always adds items to the end.
     */
    public LinksObserver() {
      mAfter = true;
    }

    /**
     * Constructor that allows you to specify whether links should be added to the beginning or the end.
     * @param after
     */
    public LinksObserver(boolean after) {
      mAfter = after;
    }

    @Override
    public void onError(Throwable e) {
      mSwipeRefreshLayout.setRefreshing(false);
      Timber.e(e, "Failed to load links from Reddit.");
      new AlertDialog.Builder(getActivity())
          .setMessage("You failed at browsing reddit.")
          .setPositiveButton("Retry", (dialog, which) -> createRequest(mAfter).subscribe(new LinksObserver()))
          .setNegativeButton("Cancel", null)
          .show();
    }

    @Override @SuppressWarnings("unchecked")
    public void onNext(List<RedditObject> items) {
      mSwipeRefreshLayout.setRefreshing(false);
      addItems(items);
    }

    private void addItems(List<RedditObject> items) {
      if (mLinks.size() > 2 * LIMIT) {
        // our list will hold 3 * LIMIT items.  If the list already has more than 2 * LIMIT items
        // we need to remove any extras.
        if (mAfter) {
          mLinks = new ArrayList<>(mLinks.subList(LIMIT, mLinks.size()));
          mAdapter.notifyItemRangeRemoved(0, LIMIT);
        } else {
          mLinks = new ArrayList<>(mLinks.subList(0, LIMIT * 2));
          mAdapter.notifyItemRangeRemoved(2 * LIMIT, LIMIT);
        }
      }

      List<RedditLink> links = (List) items;
      int startPos = mAfter ? mLinks.size() : 0;
      mLinks.addAll(startPos, links);

      // Store the first and last IDs so we can get more later
      if (mLinks.size() > 0) {
        mAfterId = mLinks.get(mLinks.size() - 1).getName();
        mBeforeId = mLinks.get(0).getName();
      }

      mAdapter.notifyItemRangeInserted(startPos, links.size());
    }
  }

  private class LinksRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
    @Override
    public void onRefresh() {
      mAdapter.notifyItemRangeRemoved(0, mLinks.size());
      mLinks.clear();
      createRequest(true).subscribe(new LinksObserver());
    }
  }

  private class LinkListScrollListener extends RecyclerView.OnScrollListener {
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      super.onScrolled(recyclerView, dx, dy);

      // Load more items when there are LIMIT/2 items left before the end.
      if (!mSwipeRefreshLayout.isRefreshing()) {
        if (mLayoutManager.findLastVisibleItemPosition() > mLinks.size() - LIMIT * .5) {
          mSwipeRefreshLayout.setRefreshing(true);
          createRequest(true).subscribe(new LinksObserver(true));
        } else if (mLayoutManager.findFirstVisibleItemPosition() < LIMIT * .5 && mLinks.size() > 2 * LIMIT) {
          mSwipeRefreshLayout.setRefreshing(true);
          createRequest(false).subscribe(new LinksObserver(false));
        }
      }
    }
  }
}
