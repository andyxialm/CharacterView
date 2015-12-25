package cn.refactor.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * 作者 : andy
 * 日期 : 15/12/23 10:09
 * 邮箱 : andyxialm@gmail.com
 * 描述 : 字符视图
 */
public class CharacterView extends View {

    public static final int SHAPE_CIRCLE     = 0;
    public static final int SHAPE_RECT       = 1;
    public static final int SHAPE_ROUND_RECT = 2;

    private static final int DEFAULT_SHAPE        = SHAPE_CIRCLE;
    private static final int DEFAULT_BACKGROUND   = Color.RED;
    private static final int DEFAULT_TEXT_COLOR   = Color.WHITE;
    private static final int DEFAULT_BORDER_COLOR = Color.WHITE;
    private static final int DEFAULT_RADIUS       = 0xA;
    private static final int DEFAULT_TEXT_SIZE    = 0xE;
    private static final int DEFAULT_MIN_PADDING  = 0x5;

    private int mShape;
    private int mRadius;
    private int mTextSize;
    private int mTextColor;
    private int mBackgroundColor;
    private int mTextStyle;
    private int mBorderWidth;
    private int mBorderColor;
    private int mMinimumSize;
    private CharSequence mText;

    private Rect mTargetRect;
    private Paint mPaint, mTextPaint, mBorderPaint;

    public CharacterView(Context context) {
        this(context, null);
    }

    public CharacterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CharacterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CharacterView);
        mShape = ta.getInt(R.styleable.CharacterView_shape, DEFAULT_SHAPE);
        mTextStyle = ta.getInt(R.styleable.CharacterView_textStyle, Typeface.NORMAL);
        mTextColor = ta.getColor(R.styleable.CharacterView_textColor, DEFAULT_TEXT_COLOR);
        mTextSize = ta.getDimensionPixelSize(R.styleable.CharacterView_textSize, DisplayUtils.sp2px(context, DEFAULT_TEXT_SIZE));
        mText = ta.getString(R.styleable.CharacterView_text);
        mBackgroundColor = ta.getColor(R.styleable.CharacterView_backgroundColor, DEFAULT_BACKGROUND);
        mRadius = ta.getDimensionPixelSize(R.styleable.CharacterView_radius, DEFAULT_RADIUS);
        mBorderWidth = ta.getDimensionPixelSize(R.styleable.CharacterView_borderWidth, 0);
        mBorderColor = ta.getColor(R.styleable.CharacterView_borderColor, DEFAULT_BORDER_COLOR);
        ta.recycle();
        initValues();
    }

    private void initValues() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setText(CharSequence s) {
        mText = s;
    }

    public String getText() {
        return TextUtils.isEmpty(mText) ? "" : mText.toString();
    }

    public void setShapeType(Shape type) {
        if (Shape.Circle == type) {
            mShape = SHAPE_CIRCLE;
            return;
        }
        if (Shape.Rect == type) {
            mShape = SHAPE_RECT;
            return;
        }
        if (Shape.RoundRect == type) {
            mShape = SHAPE_ROUND_RECT;
            return;
        }
        mShape = DEFAULT_SHAPE;
    }

    public int getShapeType() {
        return mShape;
    }

    public void setBorderWidth(int dp) {
        mBorderWidth = DisplayUtils.dp2px(getContext(), dp);
    }

    public void setBorderWidthPx(int px) {
        mBorderWidth = px;
    }

    public void setBorderColor(int color) {
        mBorderColor = color;
    }

    public void setRadius(int dp) {
        mRadius = DisplayUtils.dp2px(getContext(), dp);;
    }

    public void setRadiusPx(int px) {
        mRadius = px;;
    }

    public void setBackgroundColor(int bgColor) {
        mBackgroundColor = bgColor;
    }

    public void setTextSize(int sp) {
        mTextSize = DisplayUtils.sp2px(getContext(), sp);
    }

    public void setTextStyle(int textStyle) {
        mTextStyle = textStyle;
    }

    public void setTextColor(int color) {
        mTextColor = color;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension(getMeasuredWidth() + (mBorderWidth << 1), widthMeasureSpec);
        int height = measureDimension(getMeasuredHeight() + (mBorderWidth << 1), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureDimension(int measuredWidth, int measureSpec) {
        int result;
        int specSize = MeasureSpec.getSize(measureSpec);
        int specMode = MeasureSpec.getMode(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                result = measuredWidth;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(getMinimumSize(), specSize);
                break;
            default:
                result = specSize;
                break;
        }
        return result;
    }

    private int getMinimumSize() {
        if (mMinimumSize == 0) {
            Paint textPaint = new TextPaint();
            textPaint.setTextSize(mTextSize);
            int textHeight = (int) (textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top);
            int textWidth = (int) textPaint.measureText(getText());
            int padding = DisplayUtils.dp2px(getContext(), (DEFAULT_MIN_PADDING << 1));
            mMinimumSize = Math.max(textWidth, textHeight) + padding + (mBorderWidth << 1);
        }
        return mMinimumSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawShape(canvas);
        drawText(canvas);
    }

    private void drawShape(Canvas canvas) {
        switch (getShapeType()) {
            case SHAPE_CIRCLE:
                drawCircle(canvas);
                break;
            case SHAPE_RECT:
                drawRect(canvas);
                break;
            case SHAPE_ROUND_RECT:
                drawRoundRect(canvas);
                break;
            default:
                drawCircle(canvas);
                break;
        }
    }

    private void drawCircle(Canvas canvas) {
        mPaint.setColor(mBackgroundColor);
        float innerStartX = getMeasuredWidth() >> 1;
        float innerStartY = getMeasuredHeight() >> 1;
        float innerRadius = (getMeasuredHeight() - 2 * mBorderWidth) >> 1;
        canvas.drawCircle(innerStartX, innerStartY, innerRadius , mPaint);

        float startX = getMeasuredWidth() >> 1;
        float startY = getMeasuredHeight() >> 1;
        float radius = ((getMeasuredHeight() - 2 * mBorderWidth) >> 1) + mBorderWidth / 2;
        if (mBorderWidth > 0) {
            mBorderPaint.setColor(mBorderColor);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(mBorderWidth);
            canvas.drawCircle(startX, startY, radius, mBorderPaint);
        }
    }

    private void drawRect(Canvas canvas) {
        mPaint.setColor(mBackgroundColor);
        float innerStartX = mBorderWidth == 0 ? 0.0f : (mBorderWidth >> 1);
        float innerStartY = mBorderWidth == 0 ? 0.0f : (mBorderWidth >> 1);
        canvas.drawRect(innerStartX, innerStartY, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        float startX = mBorderWidth == 0 ? 0.0f : (mBorderWidth >> 1);
        float startY = mBorderWidth == 0 ? 0.0f : (mBorderWidth >> 1);
        float endX = getMeasuredWidth() - (mBorderWidth >> 1);
        float endY = getMeasuredHeight() - (mBorderWidth >> 1);
        if (mBorderWidth > 0) {
            mBorderPaint.setColor(mBorderColor);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(mBorderWidth);
            canvas.drawRect(startX, startY, endX, endY, mBorderPaint);
        }
    }

    private void drawRoundRect(Canvas canvas) {
        mPaint.setColor(mBackgroundColor);
        float startX = mBorderWidth == 0 ? 0.0f : mBorderWidth >> 1;
        float startY = mBorderWidth == 0 ? 0.0f : mBorderWidth >> 1;
        float endX = getMeasuredWidth() - (mBorderWidth >> 1);
        float endY = getMeasuredHeight() - (mBorderWidth >> 1);

        if (mBorderWidth > 0) {
            mBorderPaint.setColor(mBorderColor);
            mBorderPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mBorderPaint.setStrokeWidth(mBorderWidth);
            canvas.drawRoundRect(new RectF(startX, startY, endX, endY), mRadius, mRadius, mBorderPaint);
        }

        float innerStartX = mBorderWidth == 0 ? 0.0f : mBorderWidth;
        float innerStartY = mBorderWidth == 0 ? 0.0f : mBorderWidth;
        float innerEndX = getMeasuredWidth() - mBorderWidth;
        float innerEndY = getMeasuredHeight() - mBorderWidth;
        canvas.drawRoundRect(new RectF(innerStartX, innerStartY, innerEndX, innerEndY), mRadius, mRadius, mPaint);
    }

    private void drawText(Canvas canvas) {
        if (mTargetRect == null) {
            mTargetRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }

        Typeface typeface = Typeface.create(Typeface.SANS_SERIF, mTextStyle);
        mTextPaint.setTypeface(typeface);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);

        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        int baseline = (mTargetRect.bottom + mTargetRect.top - fontMetrics.bottom - fontMetrics.top) >> 1;
        canvas.drawText(getText(), mTargetRect.centerX(), baseline, mTextPaint);
    }

    public enum Shape {
        Circle, Rect, RoundRect;
    }
}
