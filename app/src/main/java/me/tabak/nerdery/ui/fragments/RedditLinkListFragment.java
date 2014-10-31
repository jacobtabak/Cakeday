package me.tabak.nerdery.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.tabak.nerdery.MainActivity;
import me.tabak.nerdery.MyApplication;
import me.tabak.nerdery.R;
import me.tabak.nerdery.data.reddit.RedditService;
import me.tabak.nerdery.data.reddit.model.RedditLink;
import me.tabak.nerdery.data.reddit.model.RedditObject;
import me.tabak.nerdery.rx.EndlessObserver;
import me.tabak.nerdery.ui.SpacerDecoration;
import me.tabak.nerdery.ui.recycler.anim.MyItemAnimator;
import me.tabak.nerdery.ui.viewholder.RedditLinkViewHolder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class RedditLinkListFragment extends Fragment {
  public static final int LIMIT = 25;
  @Inject RedditService mRedditService;
  @InjectView(R.id.recyclerview) RecyclerView mRecyclerView;
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
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_link_list, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mSwipeRefreshLayout.setColorSchemeColors(
        getResources().getColor(R.color.orange_bright),
        getResources().getColor(R.color.orange_dark),
        getResources().getColor(R.color.orange_light),
        getResources().getColor(R.color.orange_pale)
    );
    mRefreshListener = new LinksRefreshListener();
    mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);
    mLayoutManager = new LinearLayoutManager(getActivity());
    mRecyclerView.setLayoutManager(mLayoutManager);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.addItemDecoration(new SpacerDecoration());
    mRecyclerView.setItemAnimator(new MyItemAnimator(mLayoutManager));
    mRecyclerView.setOnScrollListener(new LinkListScrollListener());
    createRequest(true).subscribe(new LinksObserver());
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    // Appends the actionbar height to the padding so we can hide it later.
    final Toolbar toolbar = mActivity.getToolbar();
    toolbar.getViewTreeObserver().addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
          @SuppressWarnings("deprecation")
          @Override
          public void onGlobalLayout() {
            toolbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            int medium = getResources().getDimensionPixelSize(R.dimen.medium);
            mSwipeRefreshLayout.setProgressViewOffset(
                true, toolbar.getHeight(), toolbar.getHeight());
            mSwipeRefreshLayout.setProgressViewEndTarget(
                true, toolbar.getHeight() + medium);
            mRecyclerView.setPadding(
                mRecyclerView.getPaddingLeft(),
                mRecyclerView.getPaddingTop() + toolbar.getHeight(),
                mRecyclerView.getPaddingRight(),
                mRecyclerView.getPaddingBottom());
          }
        });
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
      holder.bindView(mLinks.get(position));
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
        mAfterId = mLinks.get(mLinks.size() - 1).getFullname();
        mBeforeId = mLinks.get(0).getFullname();
      }

      mAdapter.notifyItemRangeInserted(startPos, links.size());
    }
  }

  private class LinksRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
    @Override
    public void onRefresh() {
      ArrayList<RedditLink> tempLinks = new ArrayList<>(mLinks);
      Collections.reverse(tempLinks);
      for (RedditLink link : tempLinks) {
        mLinks.remove(link);
      }
      // We remove one extra because of the progressbar.
      mAdapter.notifyItemRangeRemoved(0, tempLinks.size() + 1);
      createRequest(true).subscribe(new LinksObserver());
    }
  }

  private class LinkListScrollListener extends RecyclerView.OnScrollListener {
    private boolean mAnimating;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      super.onScrolled(recyclerView, dx, dy);

      // Show or hide the toolbar when scrolling.
      if (!mAnimating) {
        Toolbar toolbar = mActivity.getToolbar();
        int minDistance = getResources().getDimensionPixelOffset(R.dimen.medium);
        float translation = ViewCompat.getTranslationY(toolbar);
        if ((dy > minDistance && translation == 0) ||
            (dy < minDistance && translation == -toolbar.getHeight())) {
          mAnimating = true;
          Timber.d("Starting animation");
          ViewCompat.animate(toolbar)
              .setInterpolator(new AccelerateInterpolator())
              .setDuration(200)
              .translationY(dy > 0 ? -toolbar.getHeight() : 0)
              .setListener(new ViewPropertyAnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(View view) {
                  super.onAnimationEnd(view);
                  mAnimating = false;
                }
              });
        }
      }

      // Load more items when the progressbar becomes visible.
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
