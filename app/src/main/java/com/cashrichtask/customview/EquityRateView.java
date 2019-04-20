package com.cashrichtask.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public class EquityRateView extends View {

    /*private Paint innerCircleP;*/
    private Paint sharedValueP;
    /*private Paint textP;*/
    private Paint fixedValueP;

    /*private int shareValue = 50;
    private int fixedValue = 50;*/

    private float shareScaleValue = 180;
    private float fixScaleValue = 180;

    public EquityRateView(Context context) {
        super(context);
        init(context);
    }

    public EquityRateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        /*innerCircleP = new Paint();
        innerCircleP.setColor(Color.WHITE);
        innerCircleP.setStyle(Paint.Style.FILL);*/

        sharedValueP = new Paint();
        sharedValueP.setColor(ContextCompat.getColor(context, android.R.color.holo_blue_light));
        sharedValueP.setStyle(Paint.Style.FILL);

        fixedValueP = new Paint();
        fixedValueP.setColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark));
        fixedValueP.setStyle(Paint.Style.FILL);

        /*textP = new Paint();
        textP.setColor(ContextCompat.getColor(context, android.R.color.background_dark));
        textP.setStyle(Paint.Style.FILL);
        textP.setTextSize(72);*/
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = getHeight() < getWidth() ? getHeight() : getWidth();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawArc(rectF, 270, shareScaleValue, true, sharedValueP);
        canvas.drawArc(rectF, 270, -fixScaleValue, true, fixedValueP);

        // inner circle
        /*size = size - 120;
        canvas.drawCircle(centerX, centerY, size / 2, innerCircleP);*/

        /*canvas.drawText(String.format("%s%%", fixedValue), 120, getHeight() / 2, textP);
        canvas.drawText(String.format("%s%%", shareValue), size - 120, getHeight() / 2, textP);*/
    }

    public void updateValues(int shareValue) {
        /*this.shareValue = shareValue;
        this.fixedValue = 100 - shareValue;*/

        // TODO: 20-Apr-19 merge this methods in single method
        // TODO: 20-Apr-19 update logic to sync arc
        /*animateShareArc(shareScaleValue, (float) (shareValue * 3.6));
        animateFixArc(fixScaleValue, (float) ((100 - shareValue) * 3.6));*/

        shareScaleValue = (float) (shareValue * 3.6);
        fixScaleValue = (float) ((100 - shareValue) * 3.6);
        invalidate();
    }

    public void animateShareArc(final float oldS, final float newS) {
        new Thread(new Runnable() {

            boolean shouldContinue;

            @Override
            public void run() {
                shouldContinue = oldS != newS;
                float updateValue = oldS;
                while (shouldContinue) {
                    shareScaleValue = oldS < newS ? updateValue++ : updateValue--;
                    shouldContinue = updateValue != newS;
                    invalidate();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void animateFixArc(final float oldS, final float newS) {
        new Thread(new Runnable() {

            boolean shouldContinue;

            @Override
            public void run() {
                shouldContinue = oldS != newS;
                float updateValue = oldS;
                while (shouldContinue) {
                    fixScaleValue = oldS < newS ? updateValue++ : updateValue--;
                    shouldContinue = updateValue != newS;
                    invalidate();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}