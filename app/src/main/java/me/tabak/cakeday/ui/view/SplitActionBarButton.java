package me.tabak.cakeday.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import me.tabak.cakeday.R;

public class SplitActionBarButton extends ImageButton implements View.OnLongClickListener {
    public SplitActionBarButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        int pixels = (int) getResources().getDimension(R.dimen.small);
        setPadding(pixels, 0, pixels, 0);
        setOnLongClickListener(this);
        setScaleType(ScaleType.CENTER_INSIDE);
        setClickable(true);
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(getContext(), getContentDescription().toString(), Toast.LENGTH_SHORT).show();
        return true;
    }
}