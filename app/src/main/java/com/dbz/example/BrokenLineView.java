package com.dbz.example;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 *
 * @author Db_z
 * date 2020/3/18 14:49
 * @version V1.0
 */
public class BrokenLineView extends View {

    //xy坐标轴颜色
    private int mXYLineColor = 0;
    /**
     * xy坐标轴线的宽度
     */
    private int mXYLineWidth = 1;
    /**
     * xy坐标轴文字颜色
     */
    private int mXyTextColor = 0;
    /**
     * xy坐标轴文字大小
     */
    private int mXyTextSize = dpToPx(13);
    /**
     * 折线图中折线的颜色
     */
    private int mLinecolor = 0;
    /**
     * 折线图中点位信息颜色
     */
    private int mContentColor = 0;

    /**
     * x轴各个坐标点水平间距
     */
    private int interval = dpToPx(30);
    /**
     * 背景颜色
     */
    private int bgcolor = 0;
    /**
     * 绘制XY轴坐标对应的画笔
     */
    private Paint xyPaint;
    /**
     * 绘制XY轴的文本对应的画笔
     */
    private Paint xyTextPaint;
    /**
     * 画折线对应的画笔
     */
    private Paint mLinePaint;
    /**
     * 画折线对应的显示数据画笔
     */
    private Paint showPaint;
    private int width;
    private int height;
    /**
     * x轴的原点坐标
     */
    private int xOrigin;
    /**
     * y轴的原点坐标
     */
    private int yOrigin;
    /**
     * 第一个点X的坐标
     */
    private float xInit;
    /**
     * x轴坐标对应的数据
     */
    private List<XValue> xValues = new ArrayList<>();
    /**
     * y轴坐标对应的数据
     */
    private List<YValue> yValues = new ArrayList<>();
    /**
     * 折线对应的数据
     */
    private List<LineValue> lineValues = new ArrayList<>();
    /**
     * 当前选中点
     */
    private int selectIndex = 1;
    /**
     * X轴刻度文本对应的最大矩形，为了选中时，在x轴文本画的框框大小一致
     */
    private Rect xValueRect;

    /**
     * X轴文本的高度
     */
    private int xTextHeight;

    public BrokenLineView(Context context) {
        this(context, null);
    }

    public BrokenLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BrokenLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        xyPaint = new Paint();
        xyPaint.setAntiAlias(true);
        xyPaint.setStrokeWidth(mXYLineWidth);
        xyPaint.setStrokeCap(Paint.Cap.ROUND);
        xyPaint.setColor(mXYLineColor);

        xyTextPaint = new Paint();
        xyTextPaint.setAntiAlias(true);
        xyTextPaint.setTextSize(mXyTextSize);
        xyTextPaint.setStrokeCap(Paint.Cap.ROUND);
        xyTextPaint.setColor(mXyTextColor);
        xyTextPaint.setStyle(Paint.Style.STROKE);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(mXYLineWidth);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setColor(mLinecolor);
        mLinePaint.setStyle(Paint.Style.STROKE);

        showPaint = new Paint();
        showPaint.setAntiAlias(true);
        showPaint.setStrokeWidth(mXYLineWidth);
        showPaint.setStrokeCap(Paint.Cap.ROUND);
        showPaint.setColor(Color.parseColor("#6f6f6f"));
        showPaint.setStyle(Paint.Style.FILL);
        showPaint.setTextSize(mXyTextSize);
    }

    /**
     * 初始化自定义属性
     */
    private void initView(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.brokenLineView);
        mXYLineColor = array.getColor(R.styleable.brokenLineView_dbz_xy_line_color, mXYLineColor);
        mXYLineWidth = (int) array.getDimension(R.styleable.brokenLineView_dbz_xy_line_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mXYLineWidth, getResources().getDisplayMetrics()));
        mXyTextColor = array.getColor(R.styleable.brokenLineView_dbz_xy_text_color, mXyTextColor);
        mXyTextSize = (int) array.getDimension(R.styleable.brokenLineView_dbz_xy_text_size, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mXyTextSize, getResources().getDisplayMetrics()));
        mLinecolor = array.getColor(R.styleable.brokenLineView_dbz_line_color, mLinecolor);
        mContentColor = array.getColor(R.styleable.brokenLineView_dbz_content_color, mContentColor);
        bgcolor = array.getColor(R.styleable.brokenLineView_dbz_bg_color, bgcolor);
        interval = (int) array.getDimension(R.styleable.brokenLineView_dbz_interval, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, interval, getResources().getDisplayMetrics()));
        array.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            //得到宽度高度
            width = getWidth();
            height = getHeight();
            int dp2 = dpToPx(2);
            int dp3 = dpToPx(3);
            xOrigin = mXYLineWidth;//dp2是y轴文本距离左边，以及距离y轴的距离
//            xValueRect = getTextBounds("0000", xyTextPaint);
//            //X轴字高度
//            float textXHeight = xValueRect.height();
//            for (int i = 0; i < xValues.size(); i++) {//求取x轴文本最大的高度
//                Rect rect = getTextBounds(xValues.get(i).value, xyTextPaint);
//                if (rect.height() > textXHeight)
//                    textXHeight = rect.height();
//                if (rect.width() > xValueRect.width())
//                    xValueRect = rect;
//            }
            // 测量X轴月份的高度
            xValueRect = getTextBounds("00月00日", xyTextPaint);
            xTextHeight = xValueRect.width(); // 因为是垂直显示所以宽度就是高度
            // 把宽度分成10 左右各占一个间距  总宽度 - 左右距离和中间的距离 除以10列
            interval = (width - ((width / 10) - getPaddingLeft() - getPaddingRight())) / 10;
            // 要把底部文本的高度留出来
            yOrigin = (height - dp2 - xValueRect.height() - dp3 - mXYLineWidth) - xTextHeight;//dp3是x轴文本距离底边，dp2是x轴文本距离x轴的距离
            xInit = interval + xOrigin;
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(bgcolor);
        drawXY(canvas);
        drawBrokenLineAndPoint(canvas);
    }

    /**
     * 绘制折线和折线点
     */
    private void drawBrokenLineAndPoint(Canvas canvas) {
        if (xValues.size() <= 0)
            return;
        //设置显示折线的图层
        int layer = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        drawBrokenLine(canvas);
        drawBrokenPoint(canvas);
        // 将折线超出x轴坐标的部分截取掉
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(bgcolor);
        mLinePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        RectF rectF = new RectF(0, 0, xOrigin, height);
        canvas.drawRect(rectF, mLinePaint);
        mLinePaint.setXfermode(null);
        //保存图层
        canvas.restoreToCount(layer);
    }

    /**
     * 绘制折线对应的点位圆圈
     */
    private void drawBrokenPoint(Canvas canvas) {
        float dp3 = dpToPx(3);
        float dp5 = dpToPx(5);
        //绘制节点对应的原点
        for (int i = 0; i < xValues.size(); i++) {
            float x = xInit + interval * i;
            float y = (float) (yOrigin - yOrigin * (1 - 0.1f) * lineValues.get(i).num / yValues.get(yValues.size() - 1).num);
            //绘制折线点
            mLinePaint.setStyle(Paint.Style.FILL);
            mLinePaint.setColor(Color.WHITE);
            canvas.drawCircle(x, y, dp3, mLinePaint);
            mLinePaint.setStyle(Paint.Style.STROKE);
            mLinePaint.setColor(mLinecolor);
            canvas.drawCircle(x, y, dp3, mLinePaint);
            //绘制选中的点
            if (i == selectIndex - 1) {
                mLinePaint.setStyle(Paint.Style.FILL);
                mLinePaint.setColor(Color.WHITE);
                canvas.drawCircle(x, y, dp5, mLinePaint);
                mLinePaint.setColor(mContentColor);
                mLinePaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(x, y, dp5, mLinePaint);
                drawFloatTextBox(canvas, x, y - dp5, lineValues.get(i).value);
            }
        }
    }

    /**
     * 绘制点位信息弹出框
     */
    private void drawFloatTextBox(Canvas canvas, float x, float y, String text) {
        int dp6 = dpToPx(6);
        int dp20 = dpToPx(20);
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x - dp6, y - dp6);
        path.lineTo(x - dp20, y - dp6);
        path.lineTo(x - dp20, y - dp6 - dp20);
        path.lineTo(x + dp20, y - dp6 - dp20);
        path.lineTo(x + dp20, y - dp6);
        path.lineTo(x + dp6, y - dp6);
        path.lineTo(x, y);
        showPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, showPaint);
        //点位信息文字
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setTextSize(dpToPx(13));
        Rect rect = getTextBounds(text + "", mLinePaint);
        canvas.drawText(text + "", x - rect.width() / 2, y - dp6 - (dp20 - rect.height()) / 2, mLinePaint);
    }

    /**
     * 绘制折线
     */
    private void drawBrokenLine(Canvas canvas) {
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(mLinecolor);
        //绘制折线
        Path path = new Path();
        float x = xInit + interval * 0;
        float y = (float) (yOrigin - yOrigin * (1 - 0.1f) * lineValues.get(0).num / yValues.get(yValues.size() - 1).num);
        path.moveTo(x, y);
        for (int i = 1; i < xValues.size(); i++) {
            x = xInit + interval * i;
            y = (float) (yOrigin - yOrigin * (1 - 0.1f) * lineValues.get(i).num / yValues.get(yValues.size() - 1).num);
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            path.lineTo(x, y);
        }
        canvas.drawPath(path, mLinePaint);
    }

    /**
     * 绘制XY坐标
     *
     * @param canvas
     */
    private void drawXY(Canvas canvas) {
//        //绘制Y轴线
//        canvas.drawLine(xOrigin - mXYLineWidth / 2, 0, xOrigin - mXYLineWidth / 2, yOrigin, xyPaint);
//        //绘制y轴箭头
//        xyPaint.setStyle(Paint.Style.STROKE);
//        Path path = new Path();
//        path.moveTo(xOrigin - mXYLineWidth / 2 - dpToPx(5), dpToPx(12));
//        path.lineTo(xOrigin - mXYLineWidth / 2, mXYLineWidth / 2);
//        path.lineTo(xOrigin - mXYLineWidth / 2 + dpToPx(5), dpToPx(12));
//        canvas.drawPath(path, xyPaint);
//        //绘制y轴刻度
//        int yLength = (int) (yOrigin * (1 - 0.1f) / (yValues.size() - 1));//y轴上面空出10%,计算出y轴刻度间距
//        for (int i = 0; i < yValues.size(); i++) {
//            //绘制Y轴刻度
//            canvas.drawLine(xOrigin, yOrigin - yLength * i + mXYLineWidth / 2, xOrigin, yOrigin - yLength * i + mXYLineWidth / 2, xyPaint);
//            xyTextPaint.setColor(mXyTextColor);
//            //绘制Y轴文本
//            String text = yValues.get(i).value;
//            Rect rect = getTextBounds(text, xyTextPaint);
//            canvas.drawText(text, 0, text.length(), xOrigin - mXYLineWidth - dpToPx(2) - rect.width(), yOrigin - yLength * i + rect.height() / 2, xyTextPaint);
//        }

        //绘制X轴坐标
        canvas.drawLine(xOrigin, yOrigin + mXYLineWidth / 2, width, yOrigin + mXYLineWidth / 2, xyPaint);

//        //绘制x轴箭头
//        xyPaint.setStyle(Paint.Style.STROKE);
//        path = new Path();
//        //整个X轴的长度
//        float xLength = xInit + interval * (xValues.size() - 1) + (width - xOrigin) * 0.1f;
//        if (xLength < width)
//        if (xLength > width)
//            xLength = width;
//        path.moveTo(xLength - dpToPx(12), yOrigin + mXYLineWidth / 2 - dpToPx(5));
//        path.lineTo(xLength - mXYLineWidth / 2, yOrigin + mXYLineWidth / 2);
//        path.lineTo(xLength - dpToPx(12), yOrigin + mXYLineWidth / 2 + dpToPx(5));
//        path.moveTo(xLength, yOrigin + mXYLineWidth / 2);
//        path.lineTo(xLength, yOrigin + mXYLineWidth / 2);
//        path.lineTo(xLength, yOrigin + mXYLineWidth / 2);
//        canvas.drawPath(path, xyPaint);
        //绘制x轴刻度
        for (int i = 0; i < xValues.size(); i++) {
            float x = xInit + interval * i;
            if (x >= xOrigin) {//只绘制从原点开始的区域
                xyTextPaint.setColor(mXyTextColor);
                canvas.drawLine(x, yOrigin, x, yOrigin, xyPaint);
                // 如果是选中的 绘制垂直线
                if (i == selectIndex - 1) {
                    canvas.drawLine(x, yOrigin, x, (float) xValues.get(i).num, xyPaint);
                }
                //绘制X轴文本
                String text = xValues.get(i).value;
                Rect rect = getTextBounds(text, xyTextPaint);
                //绘制x轴选中文字和框
                xyTextPaint.setStyle(Paint.Style.FILL);

                float xV = x - rect.width() / 2;
                float yV = yOrigin + mXYLineWidth + dpToPx(2) + rect.height();
                // 画布旋转80度
                canvas.rotate(-80, xV, yV);
                canvas.drawText(text, xV - (xTextHeight - xTextHeight / 3), yV + (xTextHeight - xTextHeight / 2), xyTextPaint);
                canvas.rotate(80, xV, yV);
                // 水平方向显示的 xy 轴
//                    canvas.drawText(text, 0, text.length(), xV, yV, xyTextPaint);
//                } else {
//                    canvas.drawText(text, 0, text.length(), x - rect.width() / 2, yOrigin + xylinewidth + dpToPx(2) + rect.height(), xyTextPaint);
//                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                clickAction(event);
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    /**
     * 点击X轴坐标或者折线节点
     *
     * @param event
     */
    private void clickAction(MotionEvent event) {
        int dp8 = dpToPx(8);
        float eventX = event.getX();
        float eventY = event.getY();
        for (int i = 0; i < xValues.size(); i++) {
            //节点
            float x = xInit + interval * i;
            float y = (float) (yOrigin - yOrigin * (1 - 0.1f) * lineValues.get(i).num / yValues.get(yValues.size() - 1).num);
            if (eventX >= x - dp8 && eventX <= x + dp8 &&
                    eventY >= y - dp8 && eventY <= y + dp8 && selectIndex != i + 1) {//每个节点周围8dp都是可点击区域
                selectIndex = i + 1;
                invalidate();
                return;
            }
            //X轴刻度
            String text = xValues.get(i).value;
            Rect rect = getTextBounds(text, xyTextPaint);
            x = xInit + interval * i;
            y = yOrigin + mXYLineWidth + dpToPx(2);
            // 因为X轴上的字是90度角， 所以高度即时宽度
            if (eventX >= x - rect.height() / 2 - dp8 && eventX <= x + rect.height() + dp8 / 2 &&
                    eventY >= y - dp8 && eventY <= y + rect.width() + dp8 && selectIndex != i + 1) {
                selectIndex = i + 1;
                invalidate();
                return;
            }
            // 这个是正常的文本点击事件
//            if (eventX >= x - rect.width() / 2 - dp8 && eventX <= x + rect.width() + dp8 / 2 &&
//                    eventY >= y - dp8 && eventY <= y + rect.height() + dp8 && selectIndex != i + 1) {
//                selectIndex = i + 1;
//                invalidate();
//                return;
//            }
        }
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
        invalidate();
    }

    public void setxValue(List<XValue> xValues) {
        this.xValues = xValues;
    }

    public void setyValue(List<YValue> yValues) {
        this.yValues = yValues;
        invalidate();
    }

    public void setValue(List<LineValue> lineValues) {
        this.lineValues = lineValues;
        invalidate();
    }

    public void setValue(List<LineValue> lineValues, List<XValue> xValues, List<YValue> yValues) {
        this.lineValues = lineValues;
        this.xValues = xValues;
        this.yValues = yValues;
        invalidate();
    }

    /**
     * 获取丈量文本的矩形
     *
     * @param text
     * @param paint
     * @return
     */
    private Rect getTextBounds(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    /**
     * dp转化成为px
     *
     * @param dp
     * @return
     */
    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f * (dp >= 0 ? 1 : -1));
    }

    public static class XValue {
        public double num;
        public String value;

        public XValue(double num, String value) {
            this.num = num;
            this.value = value;
        }
    }

    public static class YValue {
        public double num;
        public String value;

        public YValue(double num, String value) {
            this.num = num;
            this.value = value;
        }
    }

    public static class LineValue {
        //具体数值
        public double num;
        //显示值
        public String value;

        public LineValue(double num, String value) {
            this.num = num;
            this.value = value;
        }
    }
}