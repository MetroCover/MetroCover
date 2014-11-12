package metro.k.cover.view;

import metro.k.cover.R;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextWithFont extends EditText {

	public EditTextWithFont(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setTypeface(Typeface.createFromAsset(context.getAssets(), context
				.getResources().getString(R.string.font_free_wing)));
	}

	public EditTextWithFont(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setTypeface(Typeface.createFromAsset(context.getAssets(), context
				.getResources().getString(R.string.font_free_wing)));
	}

	public EditTextWithFont(Context context) {
		super(context);
		this.setTypeface(Typeface.createFromAsset(context.getAssets(), context
				.getResources().getString(R.string.font_free_wing)));
	}

}