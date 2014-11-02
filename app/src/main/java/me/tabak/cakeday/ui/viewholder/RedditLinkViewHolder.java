package me.tabak.cakeday.ui.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.koushikdutta.ion.Ion;
import me.tabak.cakeday.MainActivity;
import me.tabak.cakeday.R;
import me.tabak.cakeday.data.HtmlHelper;
import me.tabak.cakeday.data.reddit.model.RedditLink;
import me.tabak.cakeday.ui.view.CustomLinkMovementMethod;
import org.ocpsoft.prettytime.PrettyTime;

public class RedditLinkViewHolder extends RecyclerView.ViewHolder {
  private static final String DEFAULT = "default";
  private static final String SELF = "self";
  private final Context mContext;
  public static final PrettyTime PRETTY_TIME = new PrettyTime();
  @InjectView(R.id.link_score_textview) TextView mScoreTextView;
  @InjectView(R.id.link_top_line_textview) TextView mTopLineTextView;
  @InjectView(R.id.link_title_textview) TextView mTitleTextView;
  @InjectView(R.id.link_bottom_line_textview) TextView mBottomLineTextView;
  @InjectView(R.id.link_preview_imageview) ImageView mPreviewImageView;
  @InjectView(R.id.link_full_imageview) ImageView mFullImageView;
  @InjectView(R.id.link_selftext_textview) TextView mSelfTextView;

  public RedditLinkViewHolder(View itemView) {
    super(itemView);
    mContext = itemView.getContext();
    ButterKnife.inject(this, itemView);
    mSelfTextView.setMovementMethod(new CustomLinkMovementMethod((MainActivity) itemView.getContext()));
  }

  public void bindView(RedditLink link, boolean full) {
    bindFirstLine(link);
    bindTitle(link, full);
    bindThirdLine(link);
    bindImage(link, full);
    bindSelfText(link, full);
  }

  private void bindFirstLine(RedditLink link) {
    mScoreTextView.setText(String.valueOf(link.getScore()));

    SpannableStringBuilder builder = new SpannableStringBuilder();
    TextAppearanceSpan greySpan = new TextAppearanceSpan(mContext, R.style.TextAppearance_AppCompat_Caption);
    builder.append(link.getAuthor());
    builder.append(" ").append(mContext.getString(R.string.in)).append(" ");
    int spanEnd = builder.length();
    int spanStart = spanEnd - mContext.getString(R.string.in).length() - 2;
    builder.setSpan(greySpan, spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    builder.append(link.getSubreddit());
    mTopLineTextView.setText(builder);
  }

  private void bindTitle(RedditLink link, boolean full) {
    mTitleTextView.setText(link.getTitle().replace("&amp;", "&"));
    if (full) {
      mTitleTextView.setTextAppearance(mContext, R.style.Base_TextAppearance_AppCompat_Medium);
    } else {
      mTitleTextView.setTextAppearance(mContext, R.style.Base_TextAppearance_AppCompat_Small);
    }
    mTitleTextView.setTextColor(mContext.getResources().getColor(android.R.color.black));
    mTitleTextView.setOnClickListener(v -> {
      if (!TextUtils.isEmpty(link.getUrl())) {
        MainActivity activity = (MainActivity) v.getContext();
        // If the link is a 'self' link, don't open the webview.
        if (link.isSelf()) {
          activity.showDetailFragment(link);
        } else {
          activity.showWebView(link.getUrl());
        }
      }
    });
  }

  private void bindThirdLine(RedditLink link) {
    String age = PRETTY_TIME.format(link.getCreatedUtc().toDate());
    String text = mContext.getString(R.string.link_bottom_line_template, link.getNumComments(),
        mContext.getResources().getQuantityString(R.plurals.comments, link.getNumComments()), link.getDomain(), age);
    mBottomLineTextView.setText(text);
  }

  private void bindImage(RedditLink link, boolean full) {
    mPreviewImageView.setVisibility(View.GONE);
    mFullImageView.setVisibility(View.GONE);
    if (!full) {
      // don't show a preview image if it's a self post or has a default thumbnail
      if (isValidThumb(link.getThumbnail())) {
        mPreviewImageView.setVisibility(View.VISIBLE);
        Ion.with(mPreviewImageView)
            .smartSize(true)
            .load(link.getThumbnail());
      }
    } else {
      String imageUrl = null;
      if (isImageUrl(link.getUrl())) {
        imageUrl = link.getUrl();
      } else if (isValidThumb(link.getThumbnail())) {
        imageUrl = link.getThumbnail();
      }
      if (imageUrl != null) {
        Ion.with(mFullImageView)
            .animateGif(true)
            .load(imageUrl)
            .setCallback((e, result) -> {
              if (e == null) {
                mFullImageView.setVisibility(View.VISIBLE);
              }
            });
      }
    }
  }

  private void bindSelfText(RedditLink link, boolean full) {
    if (full && !TextUtils.isEmpty(link.getSelftextHtml())) {
      mSelfTextView.setVisibility(View.VISIBLE);
      mSelfTextView.setText(HtmlHelper.prepareHtml(link.getSelftextHtml()));
    } else {
      mSelfTextView.setVisibility(View.GONE);
    }
  }

  private boolean isValidThumb(String url) {
    return (!TextUtils.isEmpty(url) && !SELF.equals(url) && !DEFAULT.equals(url));
  }

  private boolean isImageUrl(String url) {
    return !TextUtils.isEmpty(url) && (url.endsWith(".jpg") || url.endsWith(".gif") || url.endsWith(".png"));
  }
}
