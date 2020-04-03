package com.wangrui.imagee.text;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.wangrui.imagee.R;
import com.wangrui.imagee.utils.MatrixUtils;
import com.wangrui.imagee.utils.PointUtils;

/**
 *
 *
 * created by WR
 * 时间：2020-03-27 15:14
 */
public class TextItem {

    private static final float MIN_SCALE = 0.2f;
    private static final int HELP_BOX_PAD = 20;
    private static final int HELP_BOX_PAD_BOTTOM = 30;
    private final int BUTTON_WIDTH = 30;
    private final int DEFAULT_TEXT_SIZE = 100;

    // 绘制文本
    private String mText;
    // 画笔
    public Paint mTextPaint;
    // 目标位置
    public TargetRect mTargetRect;
    // 删除、缩放按钮 宽高
    private int mButtonWidth;
    private int mButtonHeight;

    boolean mIsDrawHelpTool = false;
    private Paint mHelpBoxPaint = new Paint();

    private static Bitmap mDeleteBit;
    private static Bitmap mRotateBit;

    public TextItem(Context context) {

        mHelpBoxPaint.setColor(Color.WHITE);
        mHelpBoxPaint.setStyle(Style.STROKE);
        mHelpBoxPaint.setAntiAlias(true);
        mHelpBoxPaint.setStrokeWidth(4);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(DEFAULT_TEXT_SIZE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);

        // 导入工具按钮位图
        if (mDeleteBit == null) {
            mDeleteBit = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_sticker_delete);
        }
        if (mRotateBit == null) {
            mRotateBit = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_sticker_rotate);
        }

        mButtonWidth = mDeleteBit.getWidth();
        mButtonHeight = mDeleteBit.getHeight();
    }


    public void init(String text, View parentView) {
        mText = text;
        Rect srcRect = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), srcRect);

        int halfWidth = parentView.getWidth() >> 1;
        int halfHeight = parentView.getHeight() >> 1;
        int bitWidth = Math.min(srcRect.width(), halfWidth);
        int bitHeight = (bitWidth * srcRect.height() / srcRect.width());
        mTargetRect = new TargetRect(halfWidth, halfHeight, bitWidth, bitHeight);

        mIsDrawHelpTool = true;
    }

    // 位置更新
    public void updatePos(final float dx, final float dy) {
        mTargetRect.moveBy(dx, dy);
    }

    // 旋转 缩放 更新
    public void updateRotateAndScale(final float dx, final float dy) {
        float centerX = mTargetRect.centerX();
        float centerY = mTargetRect.centerY();

        Point rbPoint = getRightBottomPoint();
        float right = rbPoint.x;
        float bottom = rbPoint.y;

        float n_x = right + dx;
        float n_y = bottom + dy;

        float xa = right - centerX;
        float ya = bottom - centerY;

        float xb = n_x - centerX;
        float yb = n_y - centerY;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);

        float scale = curLen / srcLen;// 计算缩放比

        float newCcale = mTargetRect.scale * scale;
        if (newCcale < MIN_SCALE) {// 最小缩放值检测
            return;
        }

        // 缩放
        mTargetRect.scale = mTargetRect.scale * scale;

        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1)
            return;
        float angle = (float) Math.toDegrees(Math.acos(cos));

        // 拉普拉斯定理
        float calMatrix = xa * yb - xb * ya;// 行列式计算 确定转动方向

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        // 旋转
        mTargetRect.rotate += angle;
    }

    public void draw(Canvas canvas) {
        drawText(canvas, mTargetRect);

        if (mIsDrawHelpTool) {
            // 绘制辅助工具线
            canvas.save();
            canvas.rotate(mTargetRect.rotate, mTargetRect.centerX(), mTargetRect.centerY());
            canvas.drawRoundRect(generateHelpToolRect(), 10, 10, mHelpBoxPaint);
            // 绘制工具按钮
            canvas.drawBitmap(mDeleteBit, new Rect(0, 0, mButtonWidth, mButtonHeight), generateDeleteRect(), null);
            canvas.drawBitmap(mRotateBit, new Rect(0, 0, mButtonWidth, mButtonHeight), generateRotateRect(), null);
            canvas.restore();
        }
    }

    public void drawText(Canvas canvas, Matrix matrix) {
        float scale = MatrixUtils.getValue(matrix, Matrix.MSCALE_X);
        float moveX = MatrixUtils.getValue(matrix, Matrix.MTRANS_X);
        float moveY = MatrixUtils.getValue(matrix, Matrix.MTRANS_Y);
        TargetRect targetRect = mTargetRect.clone();
        targetRect.centerX = (targetRect.centerX() - moveX) / scale;
        targetRect.centerY = (targetRect.centerY() - moveY) / scale;
        targetRect.scale = targetRect.scale / scale;
        drawText(canvas, targetRect);
    }

    private void drawText(Canvas canvas, TargetRect targetRect) {
        mTextPaint.setTextSize(DEFAULT_TEXT_SIZE * targetRect.scale);
        float rotate = targetRect.rotate;
        float centerX = targetRect.centerX();
        float centerY = targetRect.centerY();
        canvas.save();
        canvas.rotate(rotate, centerX, centerY);
        canvas.drawText(mText, centerX, centerY + targetRect.height() / 2, mTextPaint);
        canvas.restore();
    }

    private Point getRightBottomPoint() {
        float centerX = mTargetRect.centerX();
        float centerY = mTargetRect.centerY();

        Rect rect = generateTextRect();
        float right = centerX + rect.width()/2f;
        float bottom = centerY + rect.height()/2f;

        // 旋转坐标，得到真实坐标
        Point point = new Point((int)right, (int)bottom);
        point = PointUtils.rotatePoint(point, new Point((int)centerX, (int)centerY), mTargetRect.rotate);
        return point;
    }

    private Rect generateTextRect() {
        Rect srcRect = new Rect();
        mTextPaint.setTextSize(DEFAULT_TEXT_SIZE * mTargetRect.scale);
        mTextPaint.getTextBounds(mText, 0, mText.length(), srcRect);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        int offset = (int) Math.abs(fontMetrics.leading * 0.3);
        srcRect.set(srcRect.left - offset, srcRect.top - offset, srcRect.right + offset, srcRect.bottom + offset);
        return srcRect;
    }

    private RectF generateHelpToolRect() {
        Rect rect = generateTextRect();
        float left = mTargetRect.centerX - rect.width()/2f - HELP_BOX_PAD;
        float top = mTargetRect.centerY - rect.height()/2f - HELP_BOX_PAD;
        float right = mTargetRect.centerX + rect.width()/2f + HELP_BOX_PAD;
        float bottom = mTargetRect.centerY + rect.height()/2f + HELP_BOX_PAD_BOTTOM;
        return new RectF(left, top, right, bottom);
    }

    RectF generateDeleteRect() {
        RectF rectf = generateHelpToolRect();
        float left = mTargetRect.centerX - rectf.width()/2f;
        float top = mTargetRect.centerY - rectf.height()/2f;
        return new RectF(left - BUTTON_WIDTH, top - BUTTON_WIDTH, left + BUTTON_WIDTH, top + BUTTON_WIDTH);
    }

    RectF generateRotateRect() {
        RectF rectf = generateHelpToolRect();
        float right = mTargetRect.centerX + rectf.width()/2f;
        float bottom = mTargetRect.centerY + rectf.height()/2f;
        return new RectF(right - BUTTON_WIDTH, bottom - BUTTON_WIDTH, right + BUTTON_WIDTH, bottom + BUTTON_WIDTH);
    }



    // 目标绘制矩形
    public static class TargetRect {
        final int originalWidth;  // 原始宽
        final int originalHeight; // 原始高
        final float originalCenterX;  // 原始宽
        final float originalCenterY; // 原始高
        // 平移
        float centerX;  // 中心点坐标X
        float centerY;  // 中心点坐标Y
        // 旋转
        int rotate = 0; // 旋转角度
        // 缩放
        float scale = 1; // 缩放比例
        boolean reversal = false; // 反转

        TargetRect(float originalCenterX, float originalCenterY, int originalWidth, int originalHeight) {
            this.centerX = this.originalCenterX = originalCenterX;
            this.centerY = this.originalCenterY = originalCenterY;
            this.originalWidth = originalWidth;
            this.originalHeight = originalHeight;
        }

        TargetRect(TargetRect source) {
            super();
            this.originalCenterX = source.originalCenterX;
            this.originalCenterY = source.originalCenterY;
            this.originalWidth = source.originalWidth;
            this.originalHeight = source.originalHeight;
            this.centerX = source.centerX;
            this.centerY = source.centerY;
            this.rotate = source.rotate;
            this.scale = source.scale;
            this.reversal = source.reversal;
        }

        public TargetRect clone() {
            return new TargetRect(this);
        }

        // 整体移动到指定位置
        public void moveTo(float x, float y) {
            this.centerX = x;
            this.centerY = y;
        }

        public void moveBy(float x, float y) {
            this.centerX += x;
            this.centerY += y;
        }

        public float centerX() {
            return centerX;
        }

        public float centerY() {
            return centerY;
        }

        public int originalWidth() {
            return originalWidth;
        }

        public int originalHeight() {
            return originalHeight;
        }

        public float width() {
            return originalWidth() * scale;
        }

        public float height() {
            return originalHeight() * scale;
        }

        public float left() {
            return centerX - width() / 2;
        }

        public float top() {
            return centerY - height() / 2;
        }

        public float right() {
            return centerX + width() / 2;
        }

        public float bottom() {
            return centerY + height() / 2;
        }

        // 判断点是否在区域范围内
        public boolean contains(float x, float y) {
            return x >= left() && x < right() && y >= top() && y < bottom();
        }

        @Override
        public String toString() {
            return "{" + "centerX=" + centerX + ", centerY=" + centerY +
                    ", rotate=" + rotate + ", scale=" + scale + '}';
        }
    }
}