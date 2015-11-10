package com.dgmltn.slider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.View;
import android.widget.ImageView;

import com.dgmltn.slider.internal.AnimatedPinDrawable;
import com.dgmltn.slider.internal.Utils;

/**
 * A simple View that wraps an AnimatedPinDrawable. Almost all of the functionality
 * is part of AnimatedPinDrawable.
 */
public class PinView extends ImageView {

	private static final int DEFAULT_TEXT_COLOR = Color.WHITE;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({ STYLE_CIRCLE, STYLE_PIN, STYLE_NOTHING })
	public @interface PinStyle {
	}

	public static final int STYLE_CIRCLE = 0;
	public static final int STYLE_PIN = 1;
	public static final int STYLE_NOTHING = 2;

	private
	@PinStyle
	int pinStyle = STYLE_CIRCLE;

	Drawable drawable = null;
	float value = 0f;
	private String customText = null;
	boolean useCustomText = false;

	public PinView(Context context, AttributeSet attrs) {
		super(context, attrs);

		boolean clickable = true;

		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PinView, 0, 0);

			pinStyle = validatePinStyle(ta.getInt(R.styleable.PinView_pin_style, pinStyle));

			if (ta.hasValue(R.styleable.PinView_thumbColor)) {
				setImageTintList(ta.getColorStateList(R.styleable.PinView_thumbColor));
			}
			else {
				setImageTintList(generateDefaultColorStateListFromTheme(context));
			}

			setTextColor(ta.getColor(R.styleable.PinView_textColor, DEFAULT_TEXT_COLOR));

			if (ta.hasValue(R.styleable.PinView_android_text)) {
				setText(ta.getString(R.styleable.PinView_android_text));
			}

			setValue(ta.getFloat(R.styleable.PinView_value, value));

			clickable = ta.getBoolean(R.styleable.PinView_android_clickable, clickable);

			ta.recycle();
		}

		setPinStyle(pinStyle);
		setValue(value);
		setClickable(clickable);
	}

	public
	@PinStyle
	int getPinStyle() {
		return pinStyle;
	}

	public static
	@PinStyle
	int validatePinStyle(int s) {
		return (s == STYLE_PIN || s == STYLE_NOTHING) ? s : STYLE_CIRCLE;
	}

	public void setPinStyle(@PinStyle int pinStyle) {
		this.pinStyle = pinStyle;
		drawable =
			pinStyle == STYLE_PIN ? new AnimatedPinDrawable(getContext())
				: pinStyle == STYLE_NOTHING ? null
					: ContextCompat.getDrawable(getContext(), R.drawable.seekbar_thumb_material_anim);
		setImageDrawable(drawable);
	}

	public String getText() {
		return customText;
	}

	public void setText(String text) {
		useCustomText = true;
		customText = text;
		if (drawable != null && drawable instanceof AnimatedPinDrawable) {
			((AnimatedPinDrawable) drawable).setText(customText);
		}
	}

	public void setTextColor(int color) {
		if (drawable != null && drawable instanceof AnimatedPinDrawable) {
			((AnimatedPinDrawable) drawable).setTextColor(color);
		}
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		if (this.value == value) {
			return;
		}

		float oldVal = this.value;
		this.value = value;

		// Notify listeners before .setText, because one thing the listeners
		// might want to do is set custom text.
		if (listeners != null) {
			for (OnValueChangedListener l : listeners) {
				l.onValueChange(this, oldVal, value);
			}
		}

		if (!useCustomText && drawable != null && drawable instanceof AnimatedPinDrawable) {
			((AnimatedPinDrawable) drawable).setText(Integer.toString(Math.round(value)));
		}
	}

	private ArrayList<OnValueChangedListener> listeners;

	public void addOnValueChangedListener(OnValueChangedListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<>();
		}
		listeners.add(listener);
	}

	public void removeOnValueChangedListener(OnValueChangedListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	public static ColorStateList generateDefaultColorStateListFromTheme(Context context) {
		int[][] states = new int[][] {
			View.PRESSED_STATE_SET,
			View.ENABLED_STATE_SET,
			StateSet.WILD_CARD
		};

		int[] colors = new int[] {
			Utils.getThemeColor(context, android.R.attr.colorControlActivated),
			Utils.getThemeColor(context, android.R.attr.colorControlActivated),
			0x42000000 //TODO: get this from a theme attr?
		};

		return new ColorStateList(states, colors);
	}

	public interface OnValueChangedListener {
		/**
		 * Called upon a change of the current value.
		 *
		 * @param pin    The PinView associated with this listener.
		 * @param oldVal The previous value.
		 * @param newVal The new value.
		 */
		void onValueChange(PinView pin, float oldVal, float newVal);
	}

}
