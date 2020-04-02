package com.wangrui.imagee.sticker;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.wangrui.imagee.R;

/**
 * 贴纸
 *
 * @author panyi
 */
public class StickerItem {
    // 最小缩放比例
    private static final float MIN_SCALE = 0.15f;
    // 贴图与边框距离
    private static final int HELP_BOX_PAD = 5;
    // 删除、缩放 按钮宽高
    private static final int BUTTON_WIDTH = 30;

    private Bitmap mMainBitmap;
    private static Bitmap mDeleteBitmap;
    private static Bitmap mRotateBitmap;

    // 绘制目标坐标
    public RectF mDestRect;
    // 删除、旋转按钮 实际宽高
    private Rect mHelpToolsRect;
    // 删除按钮位置
    public RectF mDeleteRect;
    // 旋转按钮位置
    public RectF mRotateRect;

    private RectF mHelpBoxRect;
    // 变化矩阵
    public Matrix mMatrix;
    private float mRoatetAngle = 0;
    boolean mIsDrawHelpTool = false;
    private Paint mHelpBoxPaint = new Paint();
    // 加入屏幕时原始宽度
    private float mInitWidth;

    public RectF mDetectRotateRect;
    public RectF mDetectDeleteRect;

    public StickerItem(Context context) {

        mHelpBoxPaint.setColor(Color.WHITE);
        mHelpBoxPaint.setStyle(Style.STROKE);
        mHelpBoxPaint.setAntiAlias(true);
        mHelpBoxPaint.setStrokeWidth(4);

        // 导入工具按钮位图
        if (mDeleteBitmap == null) {
            mDeleteBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_sticker_delete);
        }
        if (mRotateBitmap == null) {
            mRotateBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_sticker_rotate);
        }
    }

    public void init(Bitmap addBit, View parentView) {
        mMainBitmap = addBit;
        int bitWidth = Math.min(addBit.getWidth(), parentView.getWidth() >> 1);
        int bitHeight = bitWidth * addBit.getHeight() / addBit.getWidth();
        int left = (parentView.getWidth() >> 1) - (bitWidth >> 1);
        int top = (parentView.getHeight() >> 1) - (bitHeight >> 1);
        mDestRect = new RectF(left, top, left + bitWidth, top + bitHeight);
        mMatrix = new Matrix();
        mMatrix.postTranslate(mDestRect.left, mDestRect.top);
        mMatrix.postScale((float) bitWidth / addBit.getWidth(),
                (float) bitHeight / addBit.getHeight(), mDestRect.left, mDestRect.top);
        // 记录原始宽度
        mInitWidth = mDestRect.width();
        mIsDrawHelpTool = true;
        mHelpBoxRect = new RectF(this.mDestRect);
        updateHelpBoxRect();

        mHelpToolsRect = new Rect(0, 0, mDeleteBitmap.getWidth(),
                mDeleteBitmap.getHeight());

        mDeleteRect = new RectF(mHelpBoxRect.left - BUTTON_WIDTH, mHelpBoxRect.top
                - BUTTON_WIDTH, mHelpBoxRect.left + BUTTON_WIDTH, mHelpBoxRect.top
                + BUTTON_WIDTH);
        mRotateRect = new RectF(mHelpBoxRect.right - BUTTON_WIDTH, mHelpBoxRect.bottom
                - BUTTON_WIDTH, mHelpBoxRect.right + BUTTON_WIDTH, mHelpBoxRect.bottom
                + BUTTON_WIDTH);

        mDetectRotateRect = new RectF(mRotateRect);
        mDetectDeleteRect = new RectF(mDeleteRect);
    }

    private void updateHelpBoxRect() {
        this.mHelpBoxRect.left -= HELP_BOX_PAD;
        this.mHelpBoxRect.right += HELP_BOX_PAD;
        this.mHelpBoxRect.top -= HELP_BOX_PAD;
        this.mHelpBoxRect.bottom += HELP_BOX_PAD;
    }

    /**
     * 位置更新
     *
     * @param dx
     * @param dy
     */
    public void updatePos(final float dx, final float dy) {
        this.mMatrix.postTranslate(dx, dy);// 记录到矩阵中

        mDestRect.offset(dx, dy);

        // 工具按钮随之移动
        mHelpBoxRect.offset(dx, dy);
        mDeleteRect.offset(dx, dy);
        mRotateRect.offset(dx, dy);

        this.mDetectRotateRect.offset(dx, dy);
        this.mDetectDeleteRect.offset(dx, dy);
    }

    /**
     * 旋转 缩放 更新
     *
     * @param dx
     * @param dy
     */
    public void updateRotateAndScale(final float oldx, final float oldy,
                                     final float dx, final float dy) {
        float c_x = mDestRect.centerX();
        float c_y = mDestRect.centerY();

        float x = this.mDetectRotateRect.centerX();
        float y = this.mDetectRotateRect.centerY();

        float n_x = x + dx;
        float n_y = y + dy;

        float xa = x - c_x;
        float ya = y - c_y;

        float xb = n_x - c_x;
        float yb = n_y - c_y;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);

        float scale = curLen / srcLen;// 计算缩放比

        float newWidth = mDestRect.width() * scale;
        if (newWidth / mInitWidth < MIN_SCALE) {// 最小缩放值检测
            return;
        }

        this.mMatrix.postScale(scale, scale, this.mDestRect.centerX(),
                this.mDestRect.centerY());// 存入scale矩阵
        scaleRect(this.mDestRect, scale);// 缩放目标矩形

        // 重新计算工具箱坐标
        mHelpBoxRect.set(mDestRect);
        updateHelpBoxRect();// 重新计算
        mRotateRect.offsetTo(mHelpBoxRect.right - BUTTON_WIDTH, mHelpBoxRect.bottom
                - BUTTON_WIDTH);
        mDeleteRect.offsetTo(mHelpBoxRect.left - BUTTON_WIDTH, mHelpBoxRect.top
                - BUTTON_WIDTH);

        mDetectRotateRect.offsetTo(mHelpBoxRect.right - BUTTON_WIDTH, mHelpBoxRect.bottom
                - BUTTON_WIDTH);
        mDetectDeleteRect.offsetTo(mHelpBoxRect.left - BUTTON_WIDTH, mHelpBoxRect.top
                - BUTTON_WIDTH);

        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1)
            return;
        float angle = (float) Math.toDegrees(Math.acos(cos));
        // System.out.println("angle--->" + angle);

        // 拉普拉斯定理
        float calMatrix = xa * yb - xb * ya;// 行列式计算 确定转动方向

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        // System.out.println("angle--->" + angle);
        mRoatetAngle += angle;
        this.mMatrix.postRotate(angle, this.mDestRect.centerX(),
                this.mDestRect.centerY());

        rotateRect(this.mDetectRotateRect, this.mDestRect.centerX(),
                this.mDestRect.centerY(), mRoatetAngle);
        rotateRect(this.mDetectDeleteRect, this.mDestRect.centerX(),
                this.mDestRect.centerY(), mRoatetAngle);
    }

    public void draw(Canvas canvas) {
        // 贴图元素绘制
        canvas.drawBitmap(mMainBitmap, this.mMatrix, null);

        if (mIsDrawHelpTool) {
            // 绘制辅助工具线
            canvas.save();
            canvas.rotate(mRoatetAngle, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
            canvas.drawRoundRect(mHelpBoxRect, 10, 10, mHelpBoxPaint);
            // 绘制工具按钮
            canvas.drawBitmap(mDeleteBitmap, mHelpToolsRect, mDeleteRect, null);
            canvas.drawBitmap(mRotateBitmap, mHelpToolsRect, mRotateRect, null);
            canvas.restore();
        }
    }

    /**
     * 缩放指定矩形
     *
     * @param rect
     * @param scale
     */
    private static void scaleRect(RectF rect, float scale) {
        float w = rect.width();
        float h = rect.height();

        float newW = scale * w;
        float newH = scale * h;

        float dx = (newW - w) / 2;
        float dy = (newH - h) / 2;

        rect.left -= dx;
        rect.top -= dy;
        rect.right += dx;
        rect.bottom += dy;
    }

    /**
     * 矩形绕指定点旋转
     *
     * @param rect
     * @param roatetAngle
     */
    private static void rotateRect(RectF rect, float center_x, float center_y,
                                   float roatetAngle) {
        float x = rect.centerX();
        float y = rect.centerY();
        float sinA = (float) Math.sin(Math.toRadians(roatetAngle));
        float cosA = (float) Math.cos(Math.toRadians(roatetAngle));
        float newX = center_x + (x - center_x) * cosA - (y - center_y) * sinA;
        float newY = center_y + (y - center_y) * cosA + (x - center_x) * sinA;

        float dx = newX - x;
        float dy = newY - y;

        rect.offset(dx, dy);
    }

    public Bitmap getMainBitmap() {
        return mMainBitmap;
    }
}
