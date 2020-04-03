package com.wangrui.imagee.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatTextView;

import com.wangrui.imagee.utils.PointUtils;

import java.util.ArrayList;

/**
 * 文本操作控件
 *
 * created by WR
 * 时间：2020-03-27 15:14
 */
public class LabelTextView extends AppCompatTextView {

	private static int STATUS_IDLE = 0;
	private static int STATUS_MOVE = 1;
	private static int STATUS_DELETE = 2;
	private static int STATUS_ROTATE = 3;

	// 当前状态
	private int mCurrentStatus;
	// 当前操作的贴图数据
	private TextItem mCurrentItem;
	private float mOldx, mOldy;

	// 存贮每层贴图数据
	private ArrayList<TextItem> mBanks = new ArrayList<>();

	public LabelTextView(Context context) {
		this(context, null);
	}

	public LabelTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LabelTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		mCurrentStatus = STATUS_IDLE;
	}

	public void addText(String text) {
		TextItem item = new TextItem(getContext());
		item.init(text, this);
		cancelCurrentItemDrawHelpState();
		mBanks.add(item);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (TextItem item : mBanks) {
			item.draw(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 是否向下传递事件标志 true为消耗
		boolean ret = super.onTouchEvent(event);

		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				for (TextItem item : mBanks) {
					// 矫正触摸位置，适应旋转后的文字
					PointF point = new PointF(x, y);
					point = PointUtils.rotatePoint(point, new PointF(item.mTargetRect.centerX, item.mTargetRect.centerY), -item.mTargetRect.rotate);
					boolean isDelete = item.generateDeleteRect().contains(point.x, point.y);
					boolean isRotate = item.generateRotateRect().contains(point.x, point.y);
					boolean isMove = item.mTargetRect.contains(point.x, point.y);
					if (isDelete || isRotate || isMove) {
						ret = true;
						cancelCurrentItemDrawHelpState();
						mCurrentItem = item;
						mCurrentItem.mIsDrawHelpTool = true;
						mOldx = x;
						mOldy = y;
					}
					if (isDelete) {
						// 删除模式
						mCurrentStatus = STATUS_DELETE;
					} else if (isRotate) {
						// 点击了旋转按钮
						mCurrentStatus = STATUS_ROTATE;
					} else if (isMove) {
						// 移动模式/被选中一张贴图
						mCurrentStatus = STATUS_MOVE;
					}
				}

				if (!ret && mCurrentItem != null && mCurrentStatus == STATUS_IDLE) {
					// 没有贴图被选择
					mCurrentItem.mIsDrawHelpTool = false;
					mCurrentItem = null;
					invalidate();
				}

				if (mCurrentItem != null && mCurrentStatus == STATUS_DELETE) {
					// 删除选定贴图
					mBanks.remove(mCurrentItem);
					mCurrentItem.mIsDrawHelpTool = false;
					mCurrentItem = null;
					mCurrentStatus = STATUS_IDLE;// 返回空闲状态
					invalidate();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				ret = true;
				if (mCurrentStatus == STATUS_MOVE) {
					// 移动贴图
					float dx = x - mOldx;
					float dy = y - mOldy;
					if (mCurrentItem != null) {
						mCurrentItem.updatePos(dx, dy);
						invalidate();
					}
					mOldx = x;
					mOldy = y;
				} else if (mCurrentStatus == STATUS_ROTATE) {
					// 旋转 缩放图片操作
					float dx = x - mOldx;
					float dy = y - mOldy;
					if (mCurrentItem != null) {
						mCurrentItem.updateRotateAndScale(dx, dy);// 旋转
						invalidate();
					}
					mOldx = x;
					mOldy = y;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				ret = false;
				mCurrentStatus = STATUS_IDLE;
				break;
		}
		return ret;
	}

	private void cancelCurrentItemDrawHelpState() {
		if (mCurrentItem != null) {
			mCurrentItem.mIsDrawHelpTool = false;
		}
	}

	public ArrayList<TextItem> getBanks() {
		return mBanks;
	}

	public void clear() {
		mBanks.clear();
		this.invalidate();
	}
}
