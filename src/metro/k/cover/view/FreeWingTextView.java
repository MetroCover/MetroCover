package metro.k.cover.view;

import metro.k.cover.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class FreeWingTextView extends TextView {

	private String mFont = "jiyunotsubasa.ttf";

	public FreeWingTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		getFont(context, attrs);
		init();
	}

	public FreeWingTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getFont(context, attrs);
		init();
	}

	public FreeWingTextView(Context context) {
		super(context);
		init();
	}

	/**
	 * フォントファイルを読み込む
	 * 
	 * @param context
	 * @param attrs
	 */
	private void getFont(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CustomTextView);
		mFont = a.getString(R.styleable.CustomTextView_font);
		a.recycle();
	}

	/**
	 * フォントを反映
	 */
	private void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), mFont);
		setTypeface(tf);
	}
}
