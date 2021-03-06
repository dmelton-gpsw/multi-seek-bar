package com.dgmltn.multiseekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.dgmltn.multiseekbar.internal.AbsMultiSeekBar;

/**
 * Created by doug on 11/1/15.
 */
public class HorizontalSeekBar extends AbsMultiSeekBar {

	private int mLeftX;
	private int mRightX;
	private int mY;

	public HorizontalSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void initTrack() {
		super.initTrack();
		mTrackOffPaint.setStyle(Paint.Style.FILL);
		mTrackOnPaint.setStyle(Paint.Style.FILL);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mLeftX = getPaddingLeft();
		mRightX = w - getPaddingRight();
		mY = (getPaddingTop() + h - getPaddingBottom()) / 2;
	}

	@Override
	protected void drawConnectingLine(Canvas canvas, float from, float to, Paint paint) {
		int top = (int)(mY - paint.getStrokeWidth() / 2);
		int bot = (int)(top + paint.getStrokeWidth());
		canvas.drawRect(getXOnBar(from), top, getXOnBar(to), bot, paint);
	}

	@Override
	public float getNearestBarValue(float x, float y) {
		x = Math.min(mRightX, Math.max(mLeftX, x));
		return (x - mLeftX) / (mRightX - mLeftX) * max;
	}

	@Override
	protected void getPointOnBar(PointF out, float value) {
		out.set(getXOnBar(value), mY);
	}

	private float getXOnBar(float value) {
		return mLeftX + value * (mRightX - mLeftX) / max;
	}

	@Override
	protected void drawBar(Canvas canvas, Paint paint) {
		int top = (int)(mY - paint.getStrokeWidth() / 2);
		int bot = (int)(top + paint.getStrokeWidth());
		canvas.drawRect(mLeftX, top, mRightX, bot, paint);
	}
}
