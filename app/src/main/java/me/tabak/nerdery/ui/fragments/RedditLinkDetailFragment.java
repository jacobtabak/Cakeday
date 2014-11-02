package me.tabak.nerdery.ui.fragments;

import android.app.AlertDialog;
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
import me.tabak.nerdery.data.reddit.RedditService;
import me.tabak.nerdery.data.reddit.model.RedditComment;
import me.tabak.nerdery.data.reddit.model.RedditLink;
import me.tabak.nerdery.data.reddit.model.RedditListing;
import me.tabak.nerdery.data.reddit.model.RedditMore;
import me.tabak.nerdery.data.reddit.model.RedditObject;
import me.tabak.nerdery.data.reddit.model.RedditResponse;
import me.tabak.nerdery.rx.EndlessObserver;
import me.tabak.nerdery.ui.recycler.anim.MyItemAnimator;
import me.tabak.nerdery.ui.viewholder.RedditCommentViewHolder;
import me.tabak.nerdery.ui.viewholder.RedditLinkViewHolder;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class RedditLinkDetailFragment extends RecyclerFragment {
  private static final String KEY_LINK = "link";
  @Inject RedditService mService;
  @InjectView(R.id.recyclerview) RecyclerView mRecyclerView;
  private MainActivity mActivity;
  private CommentsAdapter mAdapter;
  private RedditLink mLink;
  private LinearLayoutManager mLayoutManager;
  private List<RedditComment> mComments = new ArrayList<>();
  private RedditMore mMore;

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
    mService.getComments(mLink.getSubreddit(), mLink.getId(), "new", 25, 1)
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
            Timber.e(e, "Failed to download comments.");
            new AlertDialog.Builder(getActivity())
                .setMessage("You failed at browsing reddit.")
                .setNeutralButton("Okay :(", null)
                .show();
          }

          @Override
          public void onNext(List<RedditComment> comments) {
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
}
