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
 * 标签操作控件
 * @author panyi
 */
public class LabelTextView2 extends AppCompatTextView {

	private static int STATUS_IDLE = 0;
	private static int STATUS_MOVE = 1;		// 移动状态
	private static int STATUS_DELETE = 2;	// 删除状态
	private static int STATUS_ROTATE = 3;	// 图片旋转状态

	// 当前状态
	private int currentStatus;
	// 当前操作的贴图数据
	private TextItem2 currentItem;
	private float oldx, oldy;

	// 存贮每层贴图数据
	private ArrayList<TextItem2> banks = new ArrayList<>();

	public LabelTextView2(Context context) {
		this(context, null);
	}

	public LabelTextView2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LabelTextView2(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		currentStatus = STATUS_IDLE;
	}

	public void addText(String text) {
		TextItem2 item = new TextItem2(getContext());
		item.setText(text);
		if (currentItem != null) {
			currentItem.isShowHelpBox = false;
		}
		banks.add(item);
		// 重绘视图
		invalidate();
	}

	/**
	 * 绘制客户页面
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (TextItem2 item : banks) {
			item.draw(canvas);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
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
				for (TextItem2 item : banks) {
					// 矫正触摸位置，适应旋转后的文字
//					PointF point = new PointF(x, y);
//					point = PointUtils.rotatePoint(point, new PointF(item.mTargetRect.centerX, item.mTargetRect.centerY), -item.mTargetRect.rotate);
//					boolean isDelete = item.mDeleteDstRect.contains(point.x, point.y);
//					boolean isRotate = item.generateRotateRect().contains(point.x, point.y);
//					boolean isMove = item.mTargetRect.contains(point.x, point.y);
					boolean isDelete = item.mDeleteDstRect.contains(x, y);
					boolean isRotate = item.mRotateDstRect.contains(x, y);
					boolean isMove = item.mHelpBoxRect.contains(x, y);
					if (isDelete || isRotate || isMove) {
						ret = true;
						if (currentItem != null) {
							currentItem.isShowHelpBox = false;
						}
						currentItem = item;
						currentItem.isShowHelpBox = true;
						oldx = x;
						oldy = y;
					}
					if (isDelete) {
						// 删除模式
						currentStatus = STATUS_DELETE;
					} else if (isRotate) {
						// 点击了旋转按钮
						currentStatus = STATUS_ROTATE;
					} else if (isMove) {
						// 移动模式/被选中一张贴图
						currentStatus = STATUS_MOVE;
					}
				}

				if (!ret && currentItem != null && currentStatus == STATUS_IDLE) {
					// 没有贴图被选择
					currentItem.isShowHelpBox = false;
					currentItem = null;
					invalidate();
				}

				if (currentItem != null && currentStatus == STATUS_DELETE) {
					// 删除选定贴图
					banks.remove(currentItem);
					currentItem.isShowHelpBox = false;
					currentItem = null;
					currentStatus = STATUS_IDLE;// 返回空闲状态
					invalidate();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				ret = true;
				if (currentStatus == STATUS_MOVE) {
					// 移动贴图
					float dx = x - oldx;
					float dy = y - oldy;
					if (currentItem != null) {
//						currentItem.updatePos(dx, dy);
						currentItem.updateRotateAndScale(dx, dy);
						invalidate();
					}// end if
					oldx = x;
					oldy = y;
				} else if (currentStatus == STATUS_ROTATE) {
					// 旋转 缩放图片操作
					float dx = x - oldx;
					float dy = y - oldy;
					if (currentItem != null) {
						currentItem.updateRotateAndScale(dx, dy);// 旋转
						invalidate();
					}// end if
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

	public ArrayList<TextItem2> getBanks() {
		return banks;
	}

	public void clear() {
		banks.clear();
		this.invalidate();
	}
}
