package me.tabak.nerdery.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import me.tabak.nerdery.MainActivity;
import me.tabak.nerdery.MyApplication;
import me.tabak.nerdery.R;
import me.tabak.nerdery.data.reddit.RedditService;
import me.tabak.nerdery.data.reddit.model.RedditComment;
import me.tabak.nerdery.data.reddit.model.RedditLink;
import me.tabak.nerdery.data.reddit.model.RedditListing;
import me.tabak.nerdery.data.reddit.model.RedditMore;
import me.tabak.nerdery.data.reddit.model.RedditMoreResponse;
import me.tabak.nerdery.data.reddit.model.RedditObject;
import me.tabak.nerdery.data.reddit.model.RedditResponse;
import me.tabak.nerdery.rx.EndlessObserver;
import me.tabak.nerdery.ui.recycler.anim.MyItemAnimator;
import me.tabak.nerdery.ui.viewholder.RedditCommentViewHolder;
import me.tabak.nerdery.ui.viewholder.RedditLinkViewHolder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class RedditLinkDetailFragment extends RecyclerFragment {
  private static final String KEY_LINK = "link";
  public static final int LIMIT = 25;
  @Inject RedditService mRedditService;
  private MainActivity mActivity;
  private CommentsAdapter mAdapter;
  private RedditLink mLink;
  private LinearLayoutManager mLayoutManager;
  private List<RedditComment> mComments = new ArrayList<>();
  private RedditMore mMore;
  private Observable<RedditMoreResponse> mMoreCommentsObservable;

  public static RedditLinkDetailFragment newInstance(RedditLink link) {
    RedditLinkDetailFragment fragment = new RedditLinkDetailFragment();
    Bundle args = new Bundle();
    args.putSerializable(KEY_LINK, link);
    fragment.setArguments(args);
    return fragment;
  }

  @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MyApplication.get(getActivity()).inject(this);
    setHasOptionsMenu(true);
    mActivity = (MainActivity) getActivity();
    mLink = (RedditLink) getArguments().getSerializable(KEY_LINK);
    loadComments();
  }

  private void loadComments() {
    mRedditService.getComments(mLink.getSubreddit(), mLink.getId(), "new", LIMIT)
        .map(new Func1<List<RedditResponse<RedditListing>>, List<RedditComment>>() {
          @Override
          public List<RedditComment> call(List<RedditResponse<RedditListing>> redditResponses) {
            // Map the response into an array of comments, and if a 'more' object is returned,
            // hang on to that so we can get more comments later.
            RedditResponse<RedditListing> commentListing = redditResponses.get(1);
            List<RedditObject> redditObjects = commentListing.getData().getChildren();
            List<RedditComment> comments = new ArrayList<>();
            for (RedditObject redditObject : redditObjects) {
              if (redditObject instanceof RedditComment) {
                comments.add((RedditComment) redditObject);
              } else if (redditObject instanceof RedditMore) {
                mMore = (RedditMore) redditObject;
              }
            }
            return comments;
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new EndlessObserver<List<RedditComment>>() {
          @Override
          public void onError(Throwable e) {
            mSwipeRefreshLayout.setRefreshing(false);
            Timber.e(e, "Failed to download comments.");
            new AlertDialog.Builder(getActivity())
                .setMessage("You failed at browsing reddit.")
                .setPositiveButton("Retry", (dialog, which) -> loadComments())
                .setNegativeButton("Cancel", null)
                .show();
          }

          @Override
          public void onNext(List<RedditComment> comments) {
            mSwipeRefreshLayout.setRefreshing(false);
            mComments = comments;
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyItemRangeInserted(0, comments.size() + 1);
          }
        });
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mAdapter = new CommentsAdapter();
    mLayoutManager = new LinearLayoutManager(getActivity());
    mRecyclerView.setLayoutManager(mLayoutManager);
    mRecyclerView.setItemAnimator(new MyItemAnimator(mLayoutManager));
    mRecyclerView.setOnScrollListener(new MoreCommentsScrollListener());
    mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // Only show the 'show list' button if we're in dual pane mode.
    if (mActivity.isDualPane()) {
      inflater.inflate(R.menu.link_detail, menu);
      menu.findItem(R.id.menu_show_list).setOnMenuItemClickListener(item -> {
        mActivity.hideDetailPane();
        return true;
      });
    }
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
        CardView cardView = new CardView(getActivity());
        cardView.setLayoutParams(new RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        getLayoutInflater(null).inflate(R.layout.view_reddit_comment, cardView, true);
        return new RedditCommentViewHolder(cardView);
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
        // Subtract one from the position since we always show the link first.
        RedditComment comment = mComments.get(i - 1);
        ((RedditCommentViewHolder)viewHolder).bindView(comment);
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
      return mComments.size() + 1;
    }
  }

  private class MoreCommentsScrollListener extends RecyclerView.OnScrollListener {
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      super.onScrolled(recyclerView, dx, dy);
      if (!mSwipeRefreshLayout.isRefreshing() && mMore != null && mMore.getChildren().size() > 0) {
        if (mLayoutManager.findLastVisibleItemPosition() > mComments.size() - LIMIT * .5) {
          mSwipeRefreshLayout.setRefreshing(true);
          List<String> strings = mMore.takeChildren(LIMIT);
          String children = TextUtils.join(",", strings);
          // Save this observable to a field so we can re-subscribe if there is a failure.
          mMoreCommentsObservable = mRedditService.getMoreComments("json", mMore.getParentId(), "new", children);
          mMoreCommentsObservable
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new MoreCommentsObserver());
        }
      }
    }
  }

  private class MoreCommentsObserver extends EndlessObserver<RedditMoreResponse> {
    @Override
    public void onError(Throwable e) {
      mSwipeRefreshLayout.setRefreshing(false);
      Timber.e(e, "Unable to load more comments");
      new AlertDialog.Builder(getActivity())
          .setMessage("Unable to load more comments :(")
          .setPositiveButton("Retry", (dialog, which) -> mMoreCommentsObservable
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new MoreCommentsObserver()))
          .setNegativeButton("Cancel", null)
          .show();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onNext(RedditMoreResponse response) {
      mSwipeRefreshLayout.setRefreshing(false);
      List<RedditObject> objects = response.getThings();
      List<RedditComment> comments = (List) objects;
      mComments.addAll(comments);
      mAdapter.notifyItemRangeInserted(mComments.size() - objects.size(), objects.size());
    }
  }
}
