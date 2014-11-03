package metro.k.cover.lock;

import metro.k.cover.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

@SuppressLint("ClickableViewAccessibility")
public class KeyView extends View implements OnTouchListener, AnimationListener {

	private Context mContext;
	private int mX0;
	private int mY0;
	private int mOldx;
	private int mOldy;
	private int mRadius = 100;
	private int mUnlockDistance = 100;
	private Animation mAnimFadeIn;
	private Animation mAnimFadeOut;
	private boolean mIsAnimation = false;

	public KeyView(Context context) {
		super(context);
		init(context);
	}

	public KeyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public KeyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		setFocusable(true);
		setOnTouchListener(this);
		setupKeyPosition();
		Resources res = getResources();
		mRadius = res.getDimensionPixelSize(R.dimen.key_radius);
		mUnlockDistance = res.getDimensionPixelSize(R.dimen.unlock_distance);

		mAnimFadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
		mAnimFadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
		mAnimFadeOut.setAnimationListener(this);
	}

	private void setupKeyPosition() {
		Activity activity = (Activity) mContext;
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		final int width = displayMetrics.widthPixels;
		final int height = displayMetrics.heightPixels;
		mOldx = mX0 = width / 2;

		Resources res = getResources();
		final int bottomMargin = res.getDimensionPixelSize(R.dimen.key_bottom_margin);
		mOldy = mY0 = height - bottomMargin;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Resources resources = getResources();
		paint.setColor(resources.getColor(android.R.color.holo_blue_light));
		canvas.drawCircle(mRadius, mRadius, mRadius, paint);
		if (mIsAnimation) {
			mIsAnimation = false;
			return;
		}
		layout(mOldx - mRadius, mOldy - mRadius, mOldx + mRadius, mOldy + mRadius);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int x = (int) event.getRawX();
		int y = (int) event.getRawY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			int left = getLeft() + (x - mOldx);
			int top = getTop() + (y - mOldy);
			layout(left, top, left + getWidth(), top + getHeight());
			break;
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_UP:
			final int dx = Math.abs(mX0 - x);
			final int dy = Math.abs(mY0 - y);
			final double distance = Math.sqrt(dx * dx + dy * dy);
			if (distance > mUnlockDistance) {
				Activity activity = (Activity) mContext;
				activity.finish();
				return true;
			}
			mIsAnimation = true;
			startAnimation(mAnimFadeOut);
			break;
		}
		mOldx = x;
		mOldy = y;
		return true;
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		layout(mX0 - mRadius, mY0 - mRadius, mX0 + mRadius, mY0 + mRadius);
		mIsAnimation = true;
		startAnimation(mAnimFadeIn);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

}
