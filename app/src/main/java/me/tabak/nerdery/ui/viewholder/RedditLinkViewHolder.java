package me.tabak.nerdery.ui.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import org.ocpsoft.prettytime.PrettyTime;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.tabak.nerdery.MainActivity;
import me.tabak.nerdery.R;
import me.tabak.nerdery.data.reddit.model.RedditLink;

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

  public RedditLinkViewHolder(View itemView) {
    super(itemView);
    mContext = itemView.getContext();
    ButterKnife.inject(this, itemView);
  }

  public void bindView(RedditLink link) {
    bindFirstLine(link);
    bindTitle(link);
    bindThirdLine(link);
    bindPreview(link);
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

  private void bindTitle(RedditLink link) {
    mTitleTextView.setText(link.getTitle());
    mTitleTextView.setOnClickListener(v -> {
      if (!TextUtils.isEmpty(link.getUrl())) {
        MainActivity activity = (MainActivity) v.getContext();
        activity.showWebView(link.getUrl());
      }
    });
  }

  private void bindThirdLine(RedditLink link) {
    String age = PRETTY_TIME.format(link.getCreatedUtc().toDate());
    String text = mContext.getString(R.string.link_bottom_line_template,
        link.getNumComments(), link.getDomain(), age);
    mBottomLineTextView.setText(text);
  }

  private void bindPreview(RedditLink link) {
    // don't show a preview image if it's a self post or has a default thumbnail
    String thumbnail = link.getThumbnail();
    if (TextUtils.isEmpty(thumbnail) || SELF.equals(thumbnail) || DEFAULT.equals(thumbnail)) {
      mPreviewImageView.setVisibility(View.GONE);
    } else {
      mPreviewImageView.setVisibility(View.VISIBLE);
      Ion.with(mPreviewImageView)
          .animateGif(true)
          .smartSize(true)
          .load(thumbnail);
    }
  }
}
