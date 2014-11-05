package metro.k.cover.lock;

import metro.k.cover.R;
import metro.k.cover.Utilities;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
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
	private KeyViewListener mKeyViewListener;

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

	public void setKeyViewListener(final KeyViewListener li) {
		mKeyViewListener = li;
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
		final int[] windows = Utilities.getWindowSize(mContext);
		final int width = windows[0];
		final int height = windows[1];
		mOldx = mX0 = width / 2;

		Resources res = getResources();
		final int bottomMargin = res
				.getDimensionPixelSize(R.dimen.key_bottom_margin);
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
		layout(mOldx - mRadius, mOldy - mRadius, mOldx + mRadius, mOldy
				+ mRadius);
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
				if (mKeyViewListener != null) {
					mKeyViewListener.onUnLock();
				} else {
					LockUtil.getInstance().unlock(mContext);
				}
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
