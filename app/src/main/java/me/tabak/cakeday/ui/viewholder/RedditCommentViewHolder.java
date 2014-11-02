package me.tabak.cakeday.ui.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import me.tabak.cakeday.MainActivity;
import me.tabak.cakeday.R;
import me.tabak.cakeday.data.HtmlHelper;
import me.tabak.cakeday.data.reddit.model.RedditComment;
import me.tabak.cakeday.ui.view.CustomLinkMovementMethod;
import org.ocpsoft.prettytime.PrettyTime;

public class RedditCommentViewHolder extends RecyclerView.ViewHolder {
  private final Context mContext;
  public static final PrettyTime PRETTY_TIME = new PrettyTime();
  private final View mView;
  private final RecyclerView.LayoutParams mLayoutParams;
  @InjectView(R.id.comment_metadata_textview) TextView mMetadataTextView;
  @InjectView(R.id.comment_body_textview) TextView mBodyTextView;

  public RedditCommentViewHolder(View itemView) {
    super(itemView);
    mContext = itemView.getContext();
    ButterKnife.inject(this, itemView);
    mView = itemView;
    mBodyTextView.setMovementMethod(new CustomLinkMovementMethod((MainActivity) itemView.getContext()));
    mLayoutParams =
        new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
  }

  public void bindView(RedditComment comment) {
    setDepthMargin(comment.getDepth());
    bindMetadata(comment);
    bindBody(comment);
  }

  private void setDepthMargin(int depth) {
    int marginLeft = depth * mContext.getResources().getDimensionPixelSize(R.dimen.small);
    mLayoutParams.leftMargin = marginLeft;
    mView.setLayoutParams(mLayoutParams);
  }

  private void bindBody(RedditComment comment) {
    mBodyTextView.setText(HtmlHelper.prepareHtml(comment.getBodyHtml()));
  }

  private void bindMetadata(RedditComment comment) {
    String age = PRETTY_TIME.format(comment.getCreatedUtc().toDate());
    String text = mContext.getString(R.string.comment_metadata_template, comment.getAuthor(),
        comment.getScore(), mContext.getResources().getQuantityString(R.plurals.points, comment.getScore()), age);
    TextAppearanceSpan span = new TextAppearanceSpan(mContext, R.style.text_orange_caption);
    SpannableString spannableString = new SpannableString(text);
    spannableString.setSpan(span, 0, comment.getAuthor().length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    mMetadataTextView.setText(spannableString);
  }
}
