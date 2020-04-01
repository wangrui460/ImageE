package com.wangrui.imagee.sticker;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 贴图操作控件
 * 
 * @author panyi
 * 
 */
public class StickerView extends View {

	private static int STATUS_IDLE = 0;
	private static int STATUS_MOVE = 1;// 移动状态
	private static int STATUS_DELETE = 2;// 删除状态
	private static int STATUS_ROTATE = 3;// 图片旋转状态

	private int currentStatus;// 当前状态
	private StickerItem currentItem;// 当前操作的贴图数据
	private float oldx, oldy;

	private Paint rectPaint = new Paint();

    // 存贮每层贴图数据
	private List<StickerItem> banks = new ArrayList<>();

	public StickerView(Context context) {
		super(context);
		init(context);
	}

	public StickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		currentStatus = STATUS_IDLE;

		rectPaint.setColor(Color.RED);
		rectPaint.setAlpha(100);

	}

	public void addBitImage(final Bitmap addBit) {
		StickerItem item = new StickerItem(this.getContext());
		item.init(addBit, this);
		if (currentItem != null) {
			currentItem.isDrawHelpTool = false;
		}
        banks.add(item);
		this.invalidate();// 重绘视图
	}

	// 让当前 sticker 保持在最上层
	private void updateCurrentBitImage() {
		if (currentItem != null) {
			banks.remove(currentItem);
			banks.add(currentItem);
            this.invalidate();
		}
	}

	/**
	 * 绘制客户页面
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
        for (StickerItem item : banks) {
            item.draw(canvas);
        }
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean ret = super.onTouchEvent(event);// 是否向下传递事件标志 true为消耗

		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:

            StickerItem deleteItem = null;
			for (StickerItem item : banks) {
				if (item.detectDeleteRect.contains(x, y)) {// 删除模式
					// ret = true;
                    deleteItem = item;
					currentStatus = STATUS_DELETE;
				} else if (item.detectRotateRect.contains(x, y)) {// 点击了旋转按钮
					ret = true;
					if (currentItem != null) {
						currentItem.isDrawHelpTool = false;
					}
					currentItem = item;
					currentItem.isDrawHelpTool = true;
					currentStatus = STATUS_ROTATE;
					oldx = x;
					oldy = y;
				} else if (item.dstRect.contains(x, y)) {// 移动模式
					// 被选中一张贴图
					ret = true;
					if (currentItem != null) {
						currentItem.isDrawHelpTool = false;
					}
					currentItem = item;
					currentItem.isDrawHelpTool = true;
					currentStatus = STATUS_MOVE;
					oldx = x;
					oldy = y;
				}
			}

			if (!ret && currentItem != null && currentStatus == STATUS_IDLE) {// 没有贴图被选择
				currentItem.isDrawHelpTool = false;
				currentItem = null;
				invalidate();
			}

			if (deleteItem != null && currentStatus == STATUS_DELETE) {// 删除选定贴图
                banks.remove(deleteItem);
				currentStatus = STATUS_IDLE;// 返回空闲状态
				invalidate();
			}

			updateCurrentBitImage();

			break;
		case MotionEvent.ACTION_MOVE:
			ret = true;
			if (currentStatus == STATUS_MOVE) {
				// 移动贴图
				float dx = x - oldx;
				float dy = y - oldy;
				if (currentItem != null) {
					currentItem.updatePos(dx, dy);
					invalidate();
				}
				oldx = x;
				oldy = y;
			} else if (currentStatus == STATUS_ROTATE) {
				// 旋转 缩放图片操作
				float dx = x - oldx;
				float dy = y - oldy;
				if (currentItem != null) {
					currentItem.updateRotateAndScale(oldx, oldy, dx, dy);
					// 旋转
					invalidate();
				}
				oldx = x;
				oldy = y;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			ret = false;
			currentStatus = STATUS_IDLE;
			break;
		}
		return ret;
	}

    public List<StickerItem> getBanks() {
        return banks;
    }

    public void clear() {
		banks.clear();
		this.invalidate();
	}
}
