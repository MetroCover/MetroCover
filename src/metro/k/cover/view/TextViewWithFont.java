package metro.k.cover.view;

import metro.k.cover.R;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewWithFont extends TextView {

	public TextViewWithFont(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setTypeface(Typeface.createFromAsset(context.getAssets(), context
				.getResources().getString(R.string.font_free_wing)));
	}

	public TextViewWithFont(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setTypeface(Typeface.createFromAsset(context.getAssets(), context
				.getResources().getString(R.string.font_free_wing)));
	}

	public TextViewWithFont(Context context) {
		super(context);
		this.setTypeface(Typeface.createFromAsset(context.getAssets(), context
				.getResources().getString(R.string.font_free_wing)));
	}

}