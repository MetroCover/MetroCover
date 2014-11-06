package metro.k.cover.view;

import metro.k.cover.view.JazzyViewPager.TransitionEffect;
import android.content.res.Resources;
import android.util.TypedValue;

public final class ViewUtilities {

	public static int dpToPx(Resources res, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				res.getDisplayMetrics());
	}

	/**
	 * intからViewPagerのエフェクトを取得する
	 * 
	 * @param str
	 * @return
	 */
	public static TransitionEffect getTransitionEffectFromNumber(final int num) {
		switch (num) {
		case JazzyViewPager.EFFECT_STANDARD:
			return TransitionEffect.Standard;
		case JazzyViewPager.EFFECT_TABLET:
			return TransitionEffect.Tablet;
		case JazzyViewPager.EFFECT_CUBEIN:
			return TransitionEffect.CubeIn;
		case JazzyViewPager.EFFECT_CUBEOUT:
			return TransitionEffect.CubeOut;
		case JazzyViewPager.EFFECT_FLIPVERTICAL:
			return TransitionEffect.FlipVertical;
		case JazzyViewPager.EFFECT_FLIPHORIZONTAL:
			return TransitionEffect.FlipHorizontal;
		case JazzyViewPager.EFFECT_STACK:
			return TransitionEffect.Stack;
		case JazzyViewPager.EFFECT_ZOOMIN:
			return TransitionEffect.ZoomIn;
		case JazzyViewPager.EFFECT_ZOOMOUT:
			return TransitionEffect.ZoomOut;
		case JazzyViewPager.EFFECT_ROTATEUP:
			return TransitionEffect.RotateUp;
		case JazzyViewPager.EFFECT_ROTATEDOWN:
			return TransitionEffect.RotateDown;
		case JazzyViewPager.EFFECT_ACCODION:
			return TransitionEffect.Accordion;
		default:
			break;
		}

		return TransitionEffect.RotateDown;
	}
}
