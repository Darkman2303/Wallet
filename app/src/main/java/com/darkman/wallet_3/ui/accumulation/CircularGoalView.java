package com.darkman.wallet_3.ui.accumulation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;
import com.darkman.wallet_3.R;

public class CircularGoalView extends View {
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private Paint centerBgPaint;
    private RectF rectF;
    private float progress = 0f;
    private int strokeWidth = 50;

    public CircularGoalView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        TypedValue typedValue = new TypedValue();
        Context context = getContext();
        context.getTheme().resolveAttribute(R.attr.progressBarBackgroundColor, typedValue, true);
        backgroundPaint.setColor(typedValue.data);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        centerBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        rectF = new RectF();

        // --- 2. Настройка кисти для текста ---
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(0xFFFFFFFF);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        // Устанавливаем размер шрифта
        float textSizeSp = 32f;
        float textSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                textSizeSp,
                getResources().getDisplayMetrics()
        );
        textPaint.setTextSize(textSizePx);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float padding = strokeWidth / 2f;

        rectF.set(padding, padding, width - padding, height - padding);

        // Рисуем кольца
        canvas.drawArc(rectF, 0, 360, false, backgroundPaint);
        float sweepAngle = 360 * (progress / 100f);
        canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);

        canvas.drawCircle(width / 2, height / 2, 230f, centerBgPaint);


        String text = String.format("%.1f%%", progress);

        float xPos = width / 2;
        float yPos = height * 0.8f;

        canvas.drawText(text, xPos, yPos, textPaint);
    }

    public void setProgress(float progress, int color) {
        this.progress = progress;
        this.progressPaint.setColor(color);
        invalidate();
    }

    public void animateProgress(float toProgress, int color) {
        this.progressPaint.setColor(color);
        centerBgPaint.setColor(color);
        ValueAnimator animator = ValueAnimator.ofFloat(this.progress, toProgress);
        animator.setDuration(1600);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            this.progress = (float) animation.getAnimatedValue();
            invalidate();
        });

        animator.start();
    }
}
