package com.wangrui.imagee.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.wangrui.imagee.R;
import com.wangrui.imagee.utils.MatrixUtils;
import com.wangrui.imagee.utils.RectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 文本贴图处理控件
 */
public class TextItem2 extends View {

    public static final float TEXT_SIZE_DEFAULT = 80;
    public static final int PADDING = 32;

    public static final int TEXT_TOP_PADDING = 10;

    public static final int CHAR_MIN_HEIGHT = 60;

    // 删除、缩放 按钮宽高
    private static final int BUTTON_WIDTH = 30;


    //private String mText;
    private TextPaint mPaint = new TextPaint();
    private Paint debugPaint = new Paint();
    private Paint mHelpPaint = new Paint();

    private Rect mTextRect = new Rect();// warp text rect record
    public RectF mHelpBoxRect = new RectF();
    private Rect mDeleteRect = new Rect();//删除按钮位置
    private Rect mRotateRect = new Rect();//旋转按钮位置

    public RectF mDeleteDstRect = new RectF();
    public RectF mRotateDstRect = new RectF();

    private Bitmap mDeleteBitmap;
    private Bitmap mRotateBitmap;

    private int mCurrentMode = IDLE_MODE;
    //控件的几种模式
    private static final int IDLE_MODE = 2;//正常
    private static final int MOVE_MODE = 3;//移动模式
    private static final int ROTATE_MODE = 4;//旋转模式
    private static final int DELETE_MODE = 5;//删除模式

    private EditText mEditText;//输入控件

    public int layout_x = 0;
    public int layout_y = 0;

    private float last_x = 0;
    private float last_y = 0;

    public float mRotateAngle = 0;
    public float mScale = 1;
    private boolean isInitLayout = true;

    public boolean isShowHelpBox = true;

    private boolean mAutoNewLine = false;//是否需要自动换行
    private List<String> mTextContents = new ArrayList<String>(2);//存放所写的文字内容
    private String mText;

    public TextItem2(Context context) {
        super(context);
        initView(context);
    }

    public TextItem2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TextItem2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setEditText(EditText textView) {
        this.mEditText = textView;
    }

    private void initView(Context context) {
        debugPaint.setColor(Color.parseColor("#66ff0000"));

        mDeleteBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_sticker_delete);
        mRotateBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_sticker_rotate);

        mDeleteRect.set(0, 0, mDeleteBitmap.getWidth(), mDeleteBitmap.getHeight());
        mRotateRect.set(0, 0, mRotateBitmap.getWidth(), mRotateBitmap.getHeight());

        mDeleteDstRect = new RectF(0, 0, BUTTON_WIDTH << 1, BUTTON_WIDTH << 1);
        mRotateDstRect = new RectF(0, 0, BUTTON_WIDTH << 1, BUTTON_WIDTH << 1);

        mPaint.setColor(Color.BLUE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(TEXT_SIZE_DEFAULT);
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.LEFT);

        mHelpPaint.setColor(Color.BLACK);
        mHelpPaint.setStyle(Paint.Style.STROKE);
        mHelpPaint.setAntiAlias(true);
        mHelpPaint.setStrokeWidth(4);
    }

    public void setText(String text) {
        this.mText = text;
        invalidate();
    }

    public void setTextColor(int newColor) {
        mPaint.setColor(newColor);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isInitLayout) {
            isInitLayout = false;
            resetView();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (TextUtils.isEmpty(mText))
            return;

        parseText();
        drawContent(canvas);
    }

    protected void parseText() {
        if (TextUtils.isEmpty(mText))
            return;

        mTextContents.clear();

        String[] splits = mText.split("\n");
        for (String text : splits) {
            mTextContents.add(text);
        }//end for each
    }

    private void drawContent(Canvas canvas) {
        drawText(canvas);

        //draw x and rotate button
        int offsetValue = ((int) mDeleteDstRect.width()) >> 1;
        mDeleteDstRect.offsetTo(mHelpBoxRect.left - offsetValue, mHelpBoxRect.top - offsetValue);
        mRotateDstRect.offsetTo(mHelpBoxRect.right - offsetValue, mHelpBoxRect.bottom - offsetValue);

        RectUtil.rotateRect(mDeleteDstRect, mHelpBoxRect.centerX(),
                mHelpBoxRect.centerY(), mRotateAngle);
        RectUtil.rotateRect(mRotateDstRect, mHelpBoxRect.centerX(),
                mHelpBoxRect.centerY(), mRotateAngle);

        if (!isShowHelpBox) {
            return;
        }

        canvas.save();
        canvas.rotate(mRotateAngle, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        canvas.drawRoundRect(mHelpBoxRect, 10, 10, mHelpPaint);
        canvas.restore();


        canvas.drawBitmap(mDeleteBitmap, mDeleteRect, mDeleteDstRect, null);
        canvas.drawBitmap(mRotateBitmap, mRotateRect, mRotateDstRect, null);
    }

    private void drawText(Canvas canvas) {
        drawText(canvas, layout_x, layout_y, mScale, mRotateAngle);
    }

    public void drawText(Canvas canvas, int _x, int _y, float scale, float rotate) {
        if (!(mTextContents != null && mTextContents.size() > 0))
            return;

        int x = _x;
        int y = _y;
        int text_height = 0;

        mTextRect.set(0, 0, 0, 0);//clear
        Rect tempRect = new Rect();
        for (int i = 0; i < mTextContents.size(); i++) {
            String text = mTextContents.get(i);
            mPaint.getTextBounds(text, 0, text.length(), tempRect);
            //System.out.println(i + " ---> " + tempRect.height());
            text_height = Math.max(CHAR_MIN_HEIGHT, tempRect.height());
            if (tempRect.height() <= 0) {//处理此行文字为空的情况
                tempRect.set(0, 0, 0, text_height);
            }

            RectUtil.rectAddV(mTextRect, tempRect, TEXT_TOP_PADDING, CHAR_MIN_HEIGHT);
        }//end for i

        mTextRect.offset(x, y - text_height);


        mHelpBoxRect.set(mTextRect.left - PADDING, mTextRect.top - PADDING
                , mTextRect.right + PADDING, mTextRect.bottom + PADDING);
        RectUtil.scaleRect(mHelpBoxRect, scale);

        canvas.save();
        canvas.scale(scale, scale, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        canvas.rotate(rotate, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());

        int draw_text_y = y;
        for (int i = 0; i < mTextContents.size(); i++) {
            canvas.drawText(mTextContents.get(i), x, draw_text_y, mPaint);
            draw_text_y += text_height + TEXT_TOP_PADDING;
        }
        canvas.restore();
    }

//    private void drawText(Canvas canvas, Rect targetRect) {
//        mPaint.setTextSize(TEXT_SIZE_DEFAULT * targetRect.scale);
//
//        // 获取参数
//        float rotate = targetRect.rotate;
//        float centerX = targetRect.centerX();
//        float centerY = targetRect.centerY();
//
//        // 绘制文本
//        canvas.save();
//        canvas.rotate(rotate, centerX, centerY);
//
//        canvas.drawText(mText, centerX, centerY + targetRect.height() / 2, mPaint);
//
//        canvas.restore();
//    }
//
//    public void drawText(Canvas canvas, Matrix matrix) {
//        float scale = MatrixUtils.getValue(matrix, Matrix.MSCALE_X);
//        float moveX = MatrixUtils.getValue(matrix, Matrix.MTRANS_X);
//        float moveY = MatrixUtils.getValue(matrix, Matrix.MTRANS_Y);
//
//        // 获取参数
////        TextItem.TargetRect targetRect = new Rect(mTextRect);
//
////        targetRect.centerX = (targetRect.centerX() - moveX) / scale;
////        targetRect.centerY = (targetRect.centerY() - moveY) / scale;
////        targetRect.scale = targetRect.scale / scale;
//
//        drawText(canvas, mTextRect);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean ret = super.onTouchEvent(event);// 是否向下传递事件标志 true为消耗

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mDeleteDstRect.contains(x, y)) {// 删除模式
                    isShowHelpBox = true;
                    mCurrentMode = DELETE_MODE;
                } else if (mRotateDstRect.contains(x, y)) {// 旋转按钮
                    isShowHelpBox = true;
                    mCurrentMode = ROTATE_MODE;
                    last_x = mRotateDstRect.centerX();
                    last_y = mRotateDstRect.centerY();
                    ret = true;
                } else if (mHelpBoxRect.contains(x, y)) {// 移动模式
                    isShowHelpBox = true;
                    mCurrentMode = MOVE_MODE;
                    last_x = x;
                    last_y = y;
                    ret = true;
                } else {
                    isShowHelpBox = false;
                    invalidate();
                }// end if

                if (mCurrentMode == DELETE_MODE) {// 删除选定贴图
                    mCurrentMode = IDLE_MODE;// 返回空闲状态
                    clearTextContent();
                    invalidate();
                }// end if
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                if (mCurrentMode == MOVE_MODE) {// 移动贴图
                    mCurrentMode = MOVE_MODE;
                    float dx = x - last_x;
                    float dy = y - last_y;

                    layout_x += dx;
                    layout_y += dy;

                    invalidate();

                    last_x = x;
                    last_y = y;
                } else if (mCurrentMode == ROTATE_MODE) {// 旋转 缩放文字操作
                    mCurrentMode = ROTATE_MODE;
                    float dx = x - last_x;
                    float dy = y - last_y;

                    updateRotateAndScale(dx, dy);

                    invalidate();
                    last_x = x;
                    last_y = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ret = false;
                mCurrentMode = IDLE_MODE;
                break;
        }// end switch

        return ret;
    }

    public void clearTextContent() {
        if (mEditText != null) {
            mEditText.setText(null);
        }
        mTextContents.clear();
        setText(null);
    }


    /**
     * 旋转 缩放 更新
     *
     * @param dx
     * @param dy
     */
    public void updateRotateAndScale(final float dx, final float dy) {
        float c_x = mHelpBoxRect.centerX();
        float c_y = mHelpBoxRect.centerY();

        float x = mRotateDstRect.centerX();
        float y = mRotateDstRect.centerY();

        float n_x = x + dx;
        float n_y = y + dy;

        float xa = x - c_x;
        float ya = y - c_y;

        float xb = n_x - c_x;
        float yb = n_y - c_y;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);

        float scale = curLen / srcLen;// 计算缩放比

        mScale *= scale;
        float newWidth = mHelpBoxRect.width() * mScale;

        if (newWidth < 70) {
            mScale /= scale;
            return;
        }

        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1)
            return;
        float angle = (float) Math.toDegrees(Math.acos(cos));
        float calMatrix = xa * yb - xb * ya;// 行列式计算 确定转动方向

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        mRotateAngle += angle;
    }

    public void resetView() {
        layout_x = getMeasuredWidth() / 2;
        layout_y = getMeasuredHeight() / 2;
        mRotateAngle = 0;
        mScale = 1;
    }

    public float getScale() {
        return mScale;
    }

    public float getRotateAngle() {
        return mRotateAngle;
    }

    public boolean isAutoNewLine() {
        return mAutoNewLine;
    }

    public void setAutoNewline(boolean isAuto) {
        if (mAutoNewLine != isAuto) {
            mAutoNewLine = isAuto;
            postInvalidate();
        }
    }

}
